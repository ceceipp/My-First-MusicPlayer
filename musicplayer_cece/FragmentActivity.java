package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;
import com.lc.musicplayer.tools.ViewPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class FragmentActivity extends AppCompatActivity{
    private List<Song> songList;
    private MusicService.MyBinder myBinder;
    public MusicService musicService;
    private Player player;
    private TextView songName = null;
    private TextView songSinger = null;
    private TextView duration = null;
    private ImageView playerPic = null;
    private Button startOrPause = null;
    private Button nextSong = null;
    private Button order = null;
    private long firstTime, secondTime;
    private SearchView searchView;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<String> titleLists ;
    private int itemCount;
    private int position;
    private List<Integer> singleList;
    private int usingPosition;
    private Switch switchBtn;
    private Button exitBtn;
    private int curPage, lastPage, initPage;
//有个坑, 就是activity先onCreate, 再onResume, 所以你的handler事件有可能直接进入了infoUpdate中
// 又有一个坑, 退出不能完全退出, 必须手动清屏, 原来是因为unbindService失败, 因为infoUpdate
// 一直调用player, 因为player在service, 所以会出现unbindService但是又调用player的情况,
// 只需在unbind之前令player为null, 因为又handler循环事件, 所以不会导致infoUpdate找不到
// player又提前退出(因错误退出而不是unbind的伪退出), handler可以直接耗时间导致完全退出

    public void setTitleLists(int count,String string){
        if (string!=null)
            titleLists.set(count, string);
        else
            titleLists.set(0,"  ");
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            myBinder = (MusicService.MyBinder) iBinder;
            //player=myBinder.getPlayer();
            songList=myBinder.getSongListFromService();
            //myBinder.setServiceSongListFromActivity(songList);
            myBinder.newInstancePlayer(songList);
            player=myBinder.getPlayer();
            //infoUpdate();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //songList=(List<Song>) Saver.readSongList("firstList");
        lastPage=curPage=initPage=0;
        initService();
        initIntent();
        setContentView(R.layout.fragment_viewpager_layout);
        //不加这句不行, 因为在转换屏幕方向的时候会导致这个activity关闭,
        //然后因为这个唯一的activity关闭了, 所以service意外关闭了, 然后又重启service.
        //还有一种原因就是可能转换屏幕后, UI会强制刷新吧...而这个时候songList是null,
        //所以出错了,这种原因可能性更大...
        //找到了, 应该是第二个原因, 是因为fgactivity的前两个(为什么是前两个?
        // 因为viewpager是预载页面的)fragment的oriSongList是要从
        //这个FGActivity get到的, 然后屏幕转向导致adapter强制更新(应该是), 然后开始展示
        //fragment, 没有数据导致错误, 后来我尝试在前两个fragment独自加载oriSongList数据,
        //就算不加下面这句转换屏幕也不会出错闪退了, 这就更加证明了上面说的话,这个问题暂时想不到更好的办法解决.
        //现在是在getOriSongList做个判断, 如果这个act的songList为空, 那就返还Saver的list给他
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!上面的都是可能错误的, 下面的是最新发现
        //大坑!!!!viewpager的预载方式也十分坑爹, 他一般会预载左边和右边的一页,
        // viewPager是这样预载它的fg的: 假如他要预载fg0 和fg1,它的当前页面是应该显示fg0的,
        // 它是先分别预载fg0的onAttach和fg1的onAttach(总之就是预载onCreateView之前的页面),
        // 这两个预载完了以后再预载onCreateView(这就十分坑爹了, 查错很容易误导). 然后再看fg的加载.
        // 一般来说, fg是这样建立的: 先fgAct建立, 然后handler等待绑定service,
        //然后获得service的songlist, 然后faAct配置viewpager来启动fragment,
        // 然后fragment onAttach中获得mActivity(这就是fgAct的指向),
        //并获得"已经从service得到songlist的fgAct"的songlist(注意, 此时songlist不是null,这是重点),
        //然后这样子fg获得了一个不是null的songlist, 再进入onCreateView.此后正常运作.
        //但是!!!!!!!!!!! 如果在这之后进行屏幕转向, 整个UI都要更新, fg和fgAct就没有像上面那样
        // 重新完完全全由fgAct配置viewpager来唤醒fg, 而是类似于同步的时间来一同加载(这里貌似不应该用同步, 但词穷)
        //这个时候, 顺序是这样的: (假设viewpager当前要加载的是fg0,fg1是下一个预载的 )
        //先加载fg0的onAttach和fg1的onAttach, 然后此时fgAct其实已经重新建立了, 但是还没有进入onCreate,
        //至此其实对于fgAct影响不大, 因为这个时候都还没获取songlist的, songlist在service拿到的,
        //但是重点来了, 在上面的两个onAttach完成后, 此时fgAct才刚刚进入onCreate,
        // 然后此刻fgAct并没有viewpager(因为我的fgAct需要耗时拿songlist,
        // 所以我把viewpager配置写在了拿songlist之后, 就是sc绑定那里), 然后onCreate走完后,
        // TM的不知道Android什么机制, 竟然在这个时候启动了fg0的onCreateView
        // (fg0 竟然擅自启动了, 竟然不是利用viewpager启动的), 很明显, 由于此时fgAct还没运行到
        // 绑定服务sc那里, 所以fgAct的songlist为null, 然后fg0又傻不拉几自动加载, 在onCreateView中
        //获得fgAct的songlist(这个songlist是null !!!!), 然后就没数据加载了, 自然就闪退了!!!
        //这也太坑了把, fg自己会恢复啊,, 所以我现在打算尝fg直接拿service的songlist, 这个坑啊啊啊啊啊啊
        //感觉直接取service的songlist也算是内存泄漏. 另外, 我试了试fg的生命周期, 我发现转动屏幕的这个
        //操作不是把fg onPause而已, 而是完完全全的fg onDestroy, 这意味着我在fg内bundle.put, get (songlist)
        //也没有任何意义, 所以我还打算每个fg单独配songlist数据来源, 那就是直接Saverxxxx然后再令songlist为null
        //释放资源...或许fragment的设计就是加载一些静态的资源吧????? 感觉用act直接控制fragment要简单得多
        //而不是用viewpager来搞...

        //songList = (List<Song>) Saver.readSongList("firstList");
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(Data.Player_Loading_Msg,200);
        init();
        initFragment();
        //initFragment();
        //initIntent();
    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    @Override
    protected void onResume(){
        super.onResume();
        handler.sendEmptyMessageDelayed(Data.Player_Loading_Msg,200);
    }
    @Override
    protected void onPause(){
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        player.musicInfoNeedUpdate=false;
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        int cachePage=0;
        if (curPage != lastPage) {
            cachePage = curPage;
            viewPager.setCurrentItem(lastPage,false);
            lastPage=cachePage;
        }
        else{
            viewPager.setCurrentItem(0,false);
        }

        if(firstTime==0){
            firstTime = System.currentTimeMillis();
            Toast.makeText(this,"back",Toast.LENGTH_SHORT).show();
        }
        else if (firstTime!=0){
            secondTime =System.currentTimeMillis();
            if (secondTime- firstTime>2000){
                firstTime = 0; secondTime=0;
                Toast.makeText(this,"back",Toast.LENGTH_SHORT).show();
            }
            else if (secondTime-firstTime<=2000){
                intentPackThenChange(Data.MainFragmentActivity);
            }
        }
    }

    public void initFragment(){
        titleLists = new ArrayList<>();
        viewPager= findViewById(R.id.listViewPager);
        titleLists.add("Playlist");
        titleLists.add("AlbumList");
        titleLists.add("SingerList");
        titleLists.add("PathList");
        titleLists.add("Item");
        titleLists.add("Edit");
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),titleLists);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
    }


    public void initService() {
        usingPosition = getIntent().getIntExtra("usingPosition", 1);
        Intent startIntent = new Intent(FragmentActivity.this, MusicService.class);
        Intent bindIntent = new Intent(FragmentActivity.this,MusicService.class);
        bindService(bindIntent,sc,BIND_AUTO_CREATE);
        startService(startIntent);
        usingPosition=6;
    }
    public void initOnClick(){
        startOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.startOrPause();
            }
        });
        nextSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.nextSong();
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { player.change_Order_Mode();order.setText(Data.Order_Mode.get(player.order_Mode)); }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                player.findSongWithTitle(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //player.findSongWithTitle(newText);
                return false;
            }
        });
        playerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPackThenChange(Data.PlayerActivity);
            }
        });
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Player.cache(songList, Player.idToSameAlbumConvert(songList) );
                    songList.set(0,songList.get(8));
                    Saver.saveSongList("firstList",songList);
//                    Iterator it = songList.iterator(); {
//                        Song value =(Song) it.next();
//                        if (value==songList.get(0)) {
//                            it.remove();
//                        }
//                    }
                    //songList.get(songList.size()-1).setPath(null);
                    //songList.remove(0);
                    Toast.makeText(
                            FragmentActivity.this, "Wait~",Toast.LENGTH_LONG).show();
                }
            }
        });
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPackThenChange(Data.MainFragmentActivity);
            }
        });
    }
    public void initIntent(){
        itemCount=getIntent().getIntExtra("itemCount",0);
    }
    public void intentPackThenChange(int whichActivity){
        Intent intent=null;
        switch (whichActivity){
            case Data.MainActivity:  intent =new Intent(FragmentActivity.this, MainActivity.class);break;
            case Data.MainFragmentActivity:  intent =new Intent(FragmentActivity.this, MainFragmentActivity.class);break;
            case Data.PlayerActivity: intent =new Intent(FragmentActivity.this, PlayerActivity.class);break;
            default:break;
        }
        handler.removeCallbacksAndMessages(null);
        intent.putExtra("itemCount", itemCount);
        unbindService(sc);
        startActivity(intent);
        finish();
    }
    public void init() {
        songName = findViewById(R.id.songName_fg);
        songSinger = findViewById(R.id.songSinger_fg);
        duration = findViewById(R.id.duration_fg);
        playerPic = findViewById(R.id.playerPic_fg);
        startOrPause = findViewById(R.id.startOrPause_fg);
        nextSong = findViewById(R.id.nextSong_fg);
        order = findViewById(R.id.order_fg);
        searchView=findViewById(R.id.searchView_fg);
        switchBtn= findViewById(R.id.switchBtn);
        exitBtn =findViewById(R.id.exit);
    }
    public void infoUpdate() {
        songName.setText(songList.get(player.getUsingPositionId()).getSong());
        songSinger.setText(songList.get(player.getUsingPositionId()).getSinger());
        duration.setText(songList.get(player.getUsingPositionId()).getDuration());
        playerPic.setImageBitmap(Player.loadingCover(songList.get(player.getUsingPositionId()).getPath()));
        order.setText(Data.Order_Mode.get(player.order_Mode));
        startOrPause.setText(player.mediaPlayer.isPlaying() ? "Pause" : "Play");
    }
    public Player getPlayer(){return player;}

    public List<Song> getOriSongList( ) {
        return songList;
    }

    public void setSingleListPosition(int position){
        this.position =position;
    }
    public int getSingleListPosition(){
        return position;
    }
    public void setSingleList(List<Integer> singleList){
        this.singleList = singleList;
    }
    public List<Integer> getSingleList(){
        this.singleList = singleList;
        return this.singleList;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public void setHandlerWork(){
        handler.sendEmptyMessage(Data.FragmentActivityInfoUpdate);
    }


    Handler handler = new Handler() {
        @Override
        public  void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case Data.Player_Loading_Msg:{
                    if (player==null||songList==null){
                        msg=obtainMessage(Data.Player_Loading_Msg);
                        handler.sendMessageDelayed(msg,500);
                    }
                    else {
                        handler.removeCallbacksAndMessages(null);
                        //注意:songList在绑定服务那里get到, 所以要等
                        init(); initOnClick();  infoUpdate();
                        msg=obtainMessage(Data.FragmentActivityInfoUpdate);
                        handler.sendMessageDelayed(msg,200);
                    }
                    break;
                }
                case Data.FragmentActivityInfoUpdate: {
                    if (player.musicInfoNeedUpdate) {
                            infoUpdate(); player.musicInfoNeedUpdate=false;
                    }
                    msg=obtainMessage(Data.FragmentActivityInfoUpdate);
                    handler.sendMessageDelayed(msg,500);
                    break;
                }
                default: break;
            }
        }
    };
}
