package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lc.musicplayer.MainFragment.MainPlaylistFg;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Listview_Adapter;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;


import java.io.Serializable;
import java.util.List;
//    /**
//    * 我发现了一个大坑, infoUpdate ,这个ACT是先onCreate的infoupdate执行完,
//    * 再onCreateService, 再返回这个ACT的ServiceConnection
//    * 执行onServiceConnected的player=myBinder.getPlayer();
//    * 这导致了Act中onCreate的infoupdate的player还没获得Service的player
//    * 就开始update信息了, 这导致了找不到player导致闪退
//    * 有两个方法解决, 一个是判断player是否为null, 另一个我比较喜欢, 那就是
//    * 再Service的player声明static, 我试过了, 声明之后MainActivity中赋值的player
//    * 就是MusicService的player, 但是这有个不好的地方, 那就是Service的player要public.
//    * 我也打算尝试一下static songlist
//                                  现在也吧player弄成private了...也可以了, 因为handler里处理了
//    *
//    *
//    * */
public class MainActivity extends AppCompatActivity {
    private TextView songName = null;
    private TextView songSinger = null;
    private TextView duration = null;
    private ImageView playerPic = null;
    private Button startOrPause = null;
    private Button nextSong = null;
    private Button order = null;
    private ListView listView=null;
    private List<Song> song_list ;
    public int usingPosition;
    protected MusicService musicService;
    private MusicService.MyBinder myBinder;
    private Player player;
    private  int itemCount;
    private List<Song> playlistQueue;
    private Listview_Adapter adapter;

    long firstTime = 0;
    long secondTime = 0;

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            myBinder = (MusicService.MyBinder) iBinder;
            musicService = myBinder.getService();
            if (myBinder.getPlayer()!=null){
                player=myBinder.getPlayer();
                if (myBinder.getSongListFromService()!=null)
                    song_list=myBinder.getSongListFromService();
            }
            else {
                myBinder.setServiceSongListFromActivity(song_list);
                myBinder.newInstancePlayer(song_list);
                player = myBinder.getPlayer();
            }
            infoUpdate();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    private final String TAG = "Ser1212";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        song_list =(List<Song>) Saver.readSongList("firstList");
        setContentView(R.layout.list_layout);
        initIntent();
        initService();
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
        init();
        //initIntent();
    }
    @Override
    public void onBackPressed(){
        intentPackToFragment();
    }
//    {
//        if(firstTime==0){
//            firstTime = System.currentTimeMillis();
//            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//        }
//        else if (firstTime!=0){
//            secondTime =System.currentTimeMillis();
//            if (secondTime- firstTime>2000){
//                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                firstTime = 0; secondTime=0;
//            }
//            else if (secondTime-firstTime<=2000){
//                player=null;
//                unbindService(sc);
//                Intent stopIntent=new Intent(this,MusicService.class);
//                stopService(stopIntent);
//                finish();
//            }
//        }
//    }

    @Override
    protected void onStart(){
        super.onStart();
        //Log.d(TAG,"onStart");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d(TAG, "onRestart()");
    }
    @Override
    protected void onResume(){
        super.onResume();
        //Log.d(TAG, "onResume()");
        //listView.setSelection(itemCount);
        //这里不能设置msg直接infoUpdate, 因为有可能
        // 第一次打开时还没有获取player就来到这里运行onResume的
        //infoUpdate, 导致第一页总是打不开
        handler.sendEmptyMessage (Data.Player_Loading_Msg );
    }
    @Override
    protected void onPause(){
        super.onPause();
        itemCount = listView.getFirstVisiblePosition();
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //player=null;
    }

    public void initService() {
        usingPosition = getIntent().getIntExtra("usingPosition", -1);
        Intent startIntent = new Intent(MainActivity.this, MusicService.class);
        Intent bindIntent = new Intent(MainActivity.this,MusicService.class);
        bindService(bindIntent,sc,BIND_AUTO_CREATE);
        startService(startIntent);
        usingPosition=0;
    }
    public void init() {
        //player = MusicService.player;
        songName = findViewById(R.id.songName1);
        songSinger = findViewById(R.id.songSinger1);
        duration = findViewById(R.id.duration1);
        playerPic = findViewById(R.id.playerPic1);
        startOrPause = findViewById(R.id.startOrPause1);
        nextSong = findViewById(R.id.nextSong1);
        order = findViewById(R.id.order1);
    }
    public void initListView(){
        listView = findViewById(R.id.list_view);
        listView.setDividerHeight(1);
        playlistQueue = Player.singleListToSongList(player.getUsingPositionList(), song_list);
        adapter = new Listview_Adapter(MainActivity.this, playlistQueue, R.layout.list_item, player);
        listView.setAdapter(adapter);
        listView.setSelection(player.getUsingPositionList().indexOf(player.getUsingPositionId()));
    }
    public void initOnClick(){
        playerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPack();
            }
        });
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick
                    (AdapterView<?> parent, View view, int position, long id)
            {  //这里最好设置一下新的歌曲列表
                //player.setUsingPositionList( Player.getOrderList(song_list.size()));
                player.firstClickListItem(playlistQueue.get(position).getId());
                 }
        });
    }
    public void infoUpdate() {
        songName.setText(song_list.get(player.getUsingPositionId()).getSong());
        songSinger.setText(song_list.get(player.getUsingPositionId()).getSinger());
        duration.setText(song_list.get(player.getUsingPositionId()).getDuration());
        playerPic.setImageBitmap(Player.loadingCover(song_list.get(player.getUsingPositionId()).getPath()));
        order.setText(Data.Order_Mode.get(player.order_Mode));
        startOrPause.setText(player.mediaPlayer.isPlaying() ? "Pause" : "Play");
    }

    public void initIntent(){
        itemCount=getIntent().getIntExtra("itemCount",0);
       // listView.setSelection(itemCount);
    }
    public void intentPack(){
        Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
        itemCount = listView.getFirstVisiblePosition();
        intent.putExtra("itemCount",itemCount);
        startActivity(intent);
        finish();
    }
    public void intentPackToFragment(){
        Intent intent = new Intent(MainActivity.this,FragmentActivity.class);
        itemCount = listView.getFirstVisiblePosition();
        unbindService(sc);
        intent.putExtra("itemCount",itemCount);
        startActivity(intent);
        finish();
    }

    //Handler 的逻辑是先等待BlindService方法运行后,
    // 且等待onServiceConnection回调的完成了musicService = myBinder.getService();
    // 以及player=myBinder.getPlayer();后, 获取了这个activity的player, player不是null了
    //就清空msg队列, 然后加入initOnClick()与infoUpdate()的方法, 为什么不在onCreate
    // 加入initOnClick(), 是因为那个时候这个activity的player还是null, 而initOnClick()中是
    // 根据player来设置的, 所以必须等到player绑定到musicService的player(现在修改成private)后才能允许设置
    //否则会出错.        再加一句, song_list也是从myBinder.getPlayer()拿到的, 所以也要等.
    //所以initListView()也要放在handler里.

    Handler handler = new Handler() {
        @Override
        public  void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case Data.Player_Loading_Msg:{
                    if (player==null){
                        msg=obtainMessage(Data.Player_Loading_Msg);
                        handler.sendMessageDelayed(msg,300);
                    }
                    else {
                        handler.removeCallbacksAndMessages(null);
                        initListView(); initOnClick(); //initIntent();
                        msg=obtainMessage(Data.MainActivityInfoUpdate);
                        handler.sendMessageDelayed(msg,500);
                    }
                    break;
                }
                case Data.MainActivityInfoUpdate:{
                    if (player.musicInfoNeedUpdate) {
                        listView.setAdapter(adapter);
                        listView.setSelection(player.getUsingPositionList().indexOf(player.getUsingPositionId()));
                        infoUpdate();
                        player.musicInfoNeedUpdate=false;
                    }
                    msg=obtainMessage(Data.MainActivityInfoUpdate);
                    handler.sendMessageDelayed(msg,500); break;
                }
                default: break;
            }
        }
    };
}




