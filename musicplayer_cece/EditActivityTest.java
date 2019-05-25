package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.test.EditActivityTestAdapter;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;

public class EditActivityTest extends AppCompatActivity {
    private TextView songName = null;
    private TextView songSinger = null;
    private TextView duration = null;
    private ImageView playerPic = null;
    private Button startOrPause = null;
    private Button nextSong = null;
    private Button order = null;
    private Button btnAddToPlaylist, btnAddToPlaybackQueue, btnDeleteSongs, btnInverselySelect ;
    private TextView tvListMode;
    private ListView listView=null;
    private List<Song> song_list ;
    public int usingPosition;
    protected MusicService musicService;
    private MusicService.MyBinder myBinder;
    private Player player;
    private  int itemCount;
    private List<Song> playlistQueue;
    private EditActivityTestAdapter adapter;
    private LinearLayout playerCtrl, ctrlBtn;
    private boolean modeIsSelect =false;

    long firstTime = 0;
    long secondTime = 0;

    private List<Integer> selectList=new ArrayList<>();

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
            //for(int i=0;i<player.getUsingPositionList().size();i++)    selectList.add(false);

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    private final String TAG = "Ser1212";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        song_list =(List<Song>) Saver.readSongList("firstList");
        setContentView(R.layout.edit_page_layout);
        initIntent();
        initService();
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
        initFindId();
    }
    @Override
    public void onBackPressed(){
        if (modeIsSelect==true)
            doneSelection();
        else
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

    public void doneSelection(){
        if (player!=null)
            playlistQueue = Player.singleListToSongList(player.getUsingPositionList(), song_list);
        tvListMode.setVisibility(View.GONE);
        playerCtrl.setVisibility(View.VISIBLE);
        ctrlBtn.setVisibility(View.GONE);
        modeIsSelect = false;
        adapter.setModeSelect(false);
        adapter.notifyDataSetChanged();
    }
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
        Intent startIntent = new Intent(EditActivityTest.this, MusicService.class);
        Intent bindIntent = new Intent(EditActivityTest.this,MusicService.class);
        bindService(bindIntent,sc,BIND_AUTO_CREATE);
        startService(startIntent);
        usingPosition=0;
    }
    public void initFindId() {
        songName = findViewById(R.id.songName1);
        songSinger = findViewById(R.id.songSinger1);
        duration = findViewById(R.id.duration1);
        playerPic = findViewById(R.id.playerPic1);
        startOrPause = findViewById(R.id.startOrPause1);
        nextSong = findViewById(R.id.nextSong1);
        order = findViewById(R.id.order1);
        playerCtrl = findViewById(R.id.playerCtrl);
        ctrlBtn = findViewById(R.id.ctrlBtn);
        btnAddToPlaylist = findViewById(R.id.btnAddToPlaylist);
        btnAddToPlaybackQueue =findViewById(R.id.btnAddToPlaybackQueue);
        btnDeleteSongs = findViewById(R.id.btnDeleteSongs);
    }
    public void initListView(){
        tvListMode=findViewById(R.id.tvListMode);
        btnInverselySelect = findViewById(R.id.btnInverselySelect);
        listView = findViewById(R.id.list_view);
        listView.setDividerHeight(1);
        playlistQueue = Player.singleListToSongList(player.getUsingPositionList(), song_list);
        adapter = new EditActivityTestAdapter(EditActivityTest.this, playlistQueue, R.layout.list_item, player,listView,modeIsSelect);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
        listView.setSelection(player.getUsingPositionList().indexOf(player.getUsingPositionId()));
        tvListMode.setText("多选");
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
                    (AdapterView<?> parent, View view, int position, long id) {
                if (!modeIsSelect){
                    adapter.notifyDataSetChanged();
                    listView.setSelection(player.getUsingPosition());
                    player.firstClickListItem(playlistQueue.get(position).getId());
                }
                else {
                    int i = 0 ;
                    for (int j=0; j<listView.getCheckedItemPositions().size();j++){
                        if (listView.getCheckedItemPositions().get(listView.getCheckedItemPositions().keyAt(j)))
                            i++;
                    }
                    tvListMode.setText(i+"/"+playlistQueue.size());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                modeIsSelect=!modeIsSelect;
                if (modeIsSelect){
                    listView.clearChoices();
                    modeIsSelect =true;
                    adapter.setModeSelect(true);
                    tvListMode.setText("0/"+playlistQueue.size());
                    adapter.notifyDataSetChanged();
                    tvListMode.setVisibility(View.VISIBLE);
                    playerCtrl.setVisibility(View.GONE);
                    ctrlBtn.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        btnInverselySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectList = getSelectSongIdList(listView, playlistQueue);
//                for (int i=0;i<playlistQueue.size();i++){
//                    Log.d(TAG, "onClick: "+playlistQueue.get(i).getId());
//                }
                setInverselySelect(playlistQueue, selectList, listView, tvListMode, adapter );
            }
        });
        btnAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getSelectSongIdList(listView, playlistQueue).isEmpty()&& getSelectSongIdList(listView, playlistQueue)!=null)
                    selectList = getSelectSongIdList(listView, playlistQueue);
                doneSelection();
            }
        });
        btnAddToPlaybackQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    selectList = getSelectSongIdList(listView,playlistQueue);
                    player.addSongIdListToPlaybackQueue(selectList);
                    //playlistQueue = Player.singleListToSongList(player.getUsingPositionList(), song_list);
                    //adapter = new EditPageAdapter(EditActivityTest.this, playlistQueue, R.layout.list_item, player,listView,modeIsSelect);
                    doneSelection();
            }
        });
        btnDeleteSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneSelection();
            }
        });
    }
    public List<Integer> getSelectSongIdList(ListView listView,List<Song> playlistQueue){
        List<Integer> mSelectList = new ArrayList<>();
        if (listView==null)
            return null;
        if (listView.getCheckedItemPositions()==null || listView.getCheckedItemPositions().size()==0)
            return mSelectList;
        for (int j=0;j<listView.getCheckedItemPositions().size();j++)
            if (listView.getCheckedItemPositions().get(listView.getCheckedItemPositions().keyAt(j)))
                mSelectList.add(   playlistQueue.get(listView.getCheckedItemPositions().keyAt(j)).getId()   );
        if (mSelectList.isEmpty()){
            Toast.makeText(EditActivityTest.this, "没有选中任何歌曲", Toast.LENGTH_SHORT).show();
            return mSelectList;
        }
        else{
            return mSelectList;
        }
    }
    public void setInverselySelect(List<Song> playlistQueue, List<Integer> selectList
            , ListView listView, TextView tvListMode, EditActivityTestAdapter adapter ){
        if (playlistQueue.isEmpty()||listView==null||selectList==null)
            return;
        List<Integer> trueList = new ArrayList<>();
        for (int j=0;j<listView.getCheckedItemPositions().size();j++)
            if (listView.getCheckedItemPositions().get(listView.getCheckedItemPositions().keyAt(j)))
                trueList.add( listView.getCheckedItemPositions().keyAt(j) );
        for (int j = 0; j<playlistQueue.size(); j++){
            listView.setItemChecked(j, true);
        }
        for (int k = 0; k< trueList.size(); k++){
            listView.setItemChecked(trueList.get(k), false);
        }
        tvListMode.setText((playlistQueue.size()- selectList.size())+"/"+playlistQueue.size());
        adapter.notifyDataSetChanged();
    }


    public void infoUpdate() {
        if (song_list==null||song_list.isEmpty())
            return;
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
        Intent intent = new Intent(EditActivityTest.this,PlayerActivity.class);
        itemCount = listView.getFirstVisiblePosition();
        intent.putExtra("itemCount",itemCount);
        if (getIntent().getSerializableExtra("objectList")!=null)
            intent.putExtra("objectList",getIntent().getSerializableExtra("objectList"));
        startActivity(intent);
        finish();
    }
    public void intentPackToFragment(){
        Intent intent = new Intent(EditActivityTest.this,MainFragmentActivity.class);
        itemCount = listView.getFirstVisiblePosition();
        unbindService(sc);
        intent.putExtra("itemCount",itemCount);
        if (getIntent().getSerializableExtra("objectList")!=null)
            intent.putExtra("objectList",getIntent().getSerializableExtra("objectList"));
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
                        listView.setSelection(player.getUsingPositionList().indexOf(player.getUsingPositionId()));
                        infoUpdate();
                        adapter.notifyDataSetChanged();
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
