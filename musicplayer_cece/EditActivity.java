package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.MainFragment.DialogFg_AddToPlaylist;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.EditPageAdapter;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements DialogFg_AddToPlaylist.CallBackAboutDialogFg_AddToPlaylistToEditActivity {
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
    //private List<Song> playlistQueue;
    private EditPageAdapter adapter;
    private LinearLayout playerCtrl, ctrlBtn;
    private boolean modeIsSelect =false;
    private List<Integer> currentEditList;
    private DialogFg_AddToPlaylist dialogFg_addToPlaylist;
    private FragmentManager fm;
    private boolean isFromPlaylist = false;
    private boolean isFromSearchActivity =false;
    private int whichPositionFromPlaylist=-1;


    private String favSingleListStringFromDialogFg_AddToPlaylist;
    private List<Integer> oriFavListFromDialogFg_AddToPlaylist;
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
        isFromPlaylist = getIntent().getBooleanExtra("isFromPlaylist", false);
        isFromSearchActivity = getIntent().getBooleanExtra("isFromSearchActivity", false);
        //currentEditList是数据入口, 非常重要, 可以写个if在这里, 如果是mainFgAct进入, 则加载下面的
        //若从search进去的, 则加载"searchFile"
        if (isFromPlaylist&&!isFromSearchActivity){
            currentEditList=new ArrayList<>();
            currentEditList = (List<Integer>) Saver.readData("singleListToEditActivity");
        }
        else if (isFromSearchActivity&&!isFromPlaylist){
            currentEditList=new ArrayList<>();
            SameStringIdList sameStringIdList = (SameStringIdList) Saver.readData("fromSearchActivity");
            currentEditList = Player.sameStringListToList(sameStringIdList);
        }
        setContentView(R.layout.edit_page_layout);
        initIntent();
        initService();
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
        initFindId();
        dialogFg_addToPlaylist = new DialogFg_AddToPlaylist();
    }
    @Override
    public void onBackPressed(){
        if (modeIsSelect==true)
            doneSelection();
        else if (modeIsSelect!=true&&!isFromSearchActivity&&isFromPlaylist)
            intentPackToFragment();
        else if (modeIsSelect!=true&&isFromSearchActivity&&!isFromPlaylist)
            intentPackToSearchActivity();
    }

    public void doneSelection(){
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
    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onResume(){
        super.onResume();
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
    }

    public void initService() {
        usingPosition = getIntent().getIntExtra("usingPosition", -1);
        Intent startIntent = new Intent(EditActivity.this, MusicService.class);
        Intent bindIntent = new Intent(EditActivity.this,MusicService.class);
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
        //playlistQueue = Player.singleListToSongList(player.getUsingPositionList(), song_list);
        //playlistQueue = Player.singleListToSongList(currentEditList, song_list);
        adapter = new EditPageAdapter(EditActivity.this, currentEditList, song_list, R.layout.edit_page_item_layout, player, listView, modeIsSelect);
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
                    player.setUsingPositionList(currentEditList);
                    player.firstClickListItem(currentEditList.get(position));
                }
                else {
                    int i = 0 ;
                    for (int j=0; j<listView.getCheckedItemPositions().size();j++){
                        if (listView.getCheckedItemPositions().get(listView.getCheckedItemPositions().keyAt(j)))
                            i++;
                    }
                    tvListMode.setText(i+"/"+currentEditList.size());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (modeIsSelect==true)
                    return false;
                modeIsSelect=!modeIsSelect;
                if (modeIsSelect){
                    listView.clearChoices();
                    modeIsSelect =true;
                    adapter.setModeSelect(true);
                    //tvListMode.setText("0/"+playlistQueue.size());
                    if (currentEditList!=null)
                        tvListMode.setText("0/"+currentEditList.size());
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
                selectList = Player.getSelectSongIdList(listView, currentEditList, song_list);
                Player.setInverselySelect(currentEditList, selectList, listView, tvListMode, adapter );
            }
        });
        btnAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Player.getSelectSongIdList(listView, currentEditList, song_list).isEmpty()
                        && Player.getSelectSongIdList(listView, currentEditList, song_list)!=null   ){
                    selectList = Player.getSelectSongIdList(listView, currentEditList, song_list);
                    showDialogFgAddToPlaylist();
                    doneSelection();
                }
            }
        });
        btnAddToPlaybackQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectList = Player.getSelectSongIdList(listView,currentEditList, song_list);
                player.addSongIdListToPlaybackQueue(selectList);
                doneSelection();
            }
        });
        btnDeleteSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectList = Player.getSelectSongIdList(listView, currentEditList, song_list);
                //根据selectList作为参数, 完成removeFromPlaylist, 所以我打算在MainPlaylistFg中设置longClick进入该Activity,
                //然后设置参数Boolean isFromPlaylist 是的话下面语句, 否则下下面语句,
                //或者单独搞一个fragment(不要这样搞, 这样是fragment嵌套fragment)
                //这一步是把这些歌曲从当前列表currentEditList删除掉
                //if(如果当前列表是来自于currentEditList的话) 执行下面的
                currentEditList = Player.getInverselySelectList(EditActivity.this, selectList, currentEditList);
                if (isFromPlaylist){
                    Player.exchangeFromPlayList(whichPositionFromPlaylist, currentEditList);
                }
                adapter.setModeSelect(false);
                adapter.setList(currentEditList);
                doneSelection();
            }
        });
    }

    private void showDialogFgAddToPlaylist(){
        dialogFg_addToPlaylist.showNow(getSupportFragmentManager(), "dialogFg_addToPlaylist");
        dialogFg_addToPlaylist.showViewForAddToPlaylist();
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
        whichPositionFromPlaylist = getIntent().getIntExtra("whichPositionFromPlaylist", -1);
        itemCount=getIntent().getIntExtra("itemCount",0);
        // listView.setSelection(itemCount);
    }
    public void intentPack(){
        unbindService(sc);
        Intent intent = new Intent(EditActivity.this,PlayerActivity.class);
        itemCount = listView.getFirstVisiblePosition();
        intent.putExtra("itemCount",itemCount);
        if (getIntent().getSerializableExtra("objectList")!=null)
            intent.putExtra("objectList",getIntent().getSerializableExtra("objectList"));
        startActivity(intent);
        finish();
    }
    public void intentPackToSearchActivity(){
        unbindService(sc);
        Intent intent = new Intent(EditActivity.this,SearchActivity.class);
        if (getIntent().getSerializableExtra("objectList")!=null)
            intent.putExtra("objectList",getIntent().getSerializableExtra("objectList"));
        startActivity(intent);
        finish();
    }
    public void intentPackToFragment(){
        Intent intent = new Intent(EditActivity.this,MainFragmentActivity.class);
        itemCount = listView.getFirstVisiblePosition();
        unbindService(sc);
        intent.putExtra("itemCount",itemCount);
        if (getIntent().getSerializableExtra("objectList")!=null)
            intent.putExtra("objectList",getIntent().getSerializableExtra("objectList"));
        startActivity(intent);
        finish();
    }
    @Override
    public void getTheSelectStringAndOriFavList(String theSelectString, List<Integer> oriFavList) {
        //这里有个坑, 如果你的playlist名字中有非法字符, 例如\n这种, 那么就会导致添加失败
        //现在在新建歌单时会检查名字了
        favSingleListStringFromDialogFg_AddToPlaylist = theSelectString;
        oriFavListFromDialogFg_AddToPlaylist=oriFavList;
        oriFavListFromDialogFg_AddToPlaylist =
                Player.theListWhichAddSelectSongIdInNewPlaylist(selectList, oriFavListFromDialogFg_AddToPlaylist);
        Player.SaveNewFavListInDataFile( this,favSingleListStringFromDialogFg_AddToPlaylist, oriFavListFromDialogFg_AddToPlaylist, "playlist");
        Toast.makeText(EditActivity.this,  "已加入",Toast.LENGTH_SHORT).show();
        dialogFg_addToPlaylist.dismiss();
    }
    @Override
    public void setANewList(String newListName) {
        Player.SaveNewFavListInDataFile(this, newListName, selectList, "playlist");
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
                        if (currentEditList!=null && !currentEditList.isEmpty()
                                && (player.getUsingPosition()<currentEditList.size())
                                && (currentEditList.get(player.getUsingPosition())==player.getUsingPositionId()))
                        //if (currentEditList!=null&&!currentEditList.isEmpty()&&currentEditList.contains(player.getUsingPositionId()))
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
