package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.MainFragment.Adapter.SpinnerForFgList_Adapter;
import com.lc.musicplayer.MainFragment.DialogFg;
import com.lc.musicplayer.MainFragment.DialogFgDetails;
import com.lc.musicplayer.MainFragment.DialogFgFromPlaylistFg;
import com.lc.musicplayer.MainFragment.DialogFg_AddToPlaylist;
import com.lc.musicplayer.MainFragment.FgSendDataToAct;
import com.lc.musicplayer.MainFragment.MainAlbumListFg;
import com.lc.musicplayer.MainFragment.MainPathListFg;
import com.lc.musicplayer.MainFragment.MainPlaylistFg;
import com.lc.musicplayer.MainFragment.MainSameStringItemEditFg;
import com.lc.musicplayer.MainFragment.MainSameStringItemFg;
import com.lc.musicplayer.MainFragment.MainSingerListFg;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.SameStringSongsFragment_Adapter;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 终于知道为什么我不能愉快的用Fragment了,,, 原来时Intent搞的鬼!!!
 * 我的songList占了快1MB, 因为intent还要保存其他信息, 所以intent肯定超过1MB了,
 * 所以如果擅自加入songList在fg的bundle的话, 在songlist保存songlist的时候肯定要完蛋的,
 * 也就是说简单的锁屏 , 都会经历保存songlist的回调, 然后一保存就GG
 * 我现在打算用本地文件来做, 虽然会慢一点, 但是稳妥~!**/
public class MainFragmentActivity extends AppCompatActivity implements FgSendDataToAct , DialogFg.DialogFgCallback
        ,DialogFgDetails.DialogFgDetailsCallback, DialogFg_AddToPlaylist.CallBackAboutDialogFg_AddToPlaylistToEditActivity {
    private MusicService.MyBinder myBinder;
    private MainPlaylistFg playlistFragment;
    private MainAlbumListFg albumFragment;
    private MainSingerListFg singerFragment;
    private MainPathListFg pathFragment;
    private MainSameStringItemFg sameSingleFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Spinner spinnerForFgList_sameString;
    private SpinnerForFgList_Adapter spinnerAdapter;
    private static List<Song>  songList;
    private ArrayList<List<SameStringIdList>> allList;
    private List<Integer>  listCountList;
    private List<SameStringIdList> sameStringIdList;
    private List<Integer> singleList;
    private List<Fragment> fgList;
    private Player player;
    private List<String> titleString;
    private List<String> fgListString;
    private TextView songName , songSinger, duration, sameStringList;
    private ImageView playerPic;
    private Button startOrPause , nextSong, order, settingBtn ;
    private SearchView searchView;
    private DialogFg dialogFg;
    private int fgNum, itemCount, usingPosition;
    private int lastOnPausePage = -1;
    private int currentListViewPosition =-1;
    private int currentListViewPositionFromTop = 0;
    //private boolean isEverIntoFg4=false;

    private boolean isFromPlaylist = false;
    private int whichPositionFromPlaylist = -1;

    private long firstTime, secondTime;
    private Switch switchBtn;
    private String TAG = "Ser1212";
    private FrameLayout frameLayout;
    private ListView singleListListView, currentSameStringListListView;
    private List<TextView> dialogFgTvList;
    private SameStringSongsFragment_Adapter sAdapter;
    private DialogFgDetails dialogFgDetails;
    private List<TextView> dialogFgDetailsTvList;
    private ImageView dialogFgDetails_iv0;
    private LinearLayout detailsFgLlBg, dialogFgLlBg;
    private DialogFg_AddToPlaylist dialogFg_addToPlaylist;

    private Integer songIdWhichAddToPlaylist;

    @Override
    public void getTheSelectStringAndOriFavList(String theSelectString, List<Integer> oriFavList) {
        if (songIdWhichAddToPlaylist==null)
            return;
        oriFavList.add(songIdWhichAddToPlaylist);
        Player.SaveNewFavListInDataFile(this, theSelectString, oriFavList, "playlist");
        Toast.makeText(this, "已加入", Toast.LENGTH_SHORT).show();
        playlistFragment.updateData();
        dialogFg_addToPlaylist.dismiss();
    }

    @Override
    public void setANewList(String newListName) {
        List<Integer> addList = new ArrayList<>();
        addList.add(songIdWhichAddToPlaylist);
        Player.SaveNewFavListInDataFile(this, newListName, addList , "playlist");
        playlistFragment.updateData();
    }

    @Override
    public void sendSameString(String string,int sameStringItemsCount) {
        Toast.makeText(this, string+", "+sameStringItemsCount+"首",Toast.LENGTH_SHORT).show();
        sameStringList.setText(string+", "+sameStringItemsCount+"首");
        //isEverIntoFg4=true;
    }

    @Override
    public void sendPlaylistClickPosition(int position) {
        if (Saver.readData("playlist")!=null&&
                position< ((List<SameStringIdList>)(Saver.readData("playlist"))).size()){
            isFromPlaylist= true;
            this.whichPositionFromPlaylist = position;
        }
    }

    @Override
    public void sendSingleList(List<Integer> singleListFromFg, String sameString) {
        this.singleList = singleListFromFg;
        if (sameString!=null) {
            String cacheString = sameString +", "+singleList.size()+"首";
            titleString.set(4,cacheString );
        }
        showFragment(fgList.get(4));
        sameStringList.setText(titleString.get(4) );
        //sameStringList.setText(sameString+", "+singleList.size()+"首" );
        spinnerForFgList_sameString.setSelection(4,true);

        if (singleListListView!=null && this.singleList!=null) {
            sAdapter= new SameStringSongsFragment_Adapter(MyApplication.getContext()
                    ,singleList, songList,Data.SameStringSingleList, R.layout.samestringsongs_item_layout);
            singleListListView.setAdapter(sAdapter);
            singleListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    player.setUsingPositionList(singleList);
                    player.firstClickListItem(singleList.get(position) );
                }
            });
            dialogFgOnItemLongClickInit();
        }
        if (singleList!=null&&!singleList.isEmpty()){
            //如果显示第4页, 自动显示可编辑开关, 且有列表可编辑
            switchBtn.setVisibility(View.VISIBLE);
        }
    }

    public void dialogFgOnItemLongClickInit(){
        singleListListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                showDialogFg();
                dialogFgLlBg.setBackground(new BitmapDrawable(getResources(),(Player.blur(Player.loadingCover(songList.get(singleList.get(position)).getPath())))));
                dialogFgTvList.get(0).setText(songList.get(singleList.get(position)).getSong());
                dialogFgTvList.get(1).setText(songList.get(singleList.get(position)).getSinger()+" - "+
                        songList.get(singleList.get(position)).getAlbum());
                dialogFgTvList.get(2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        player.addSongIdToPlaybackQueue(singleList.get(position));
                        dialogFg.dismiss();
                    }
                });
                dialogFgTvList.get(3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogFgAddToPlaylist();
                        songIdWhichAddToPlaylist = singleList.get(position);
                        dialogFg.dismiss();
                    }
                });
                dialogFgTvList.get(4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetailsDialogFg();
                        detailsFgLlBg.setBackground(new BitmapDrawable(getResources(),(Player.blur(Player.loadingCover(songList.get(singleList.get(position)).getPath())))));
                        dialogFgDetails_iv0.setImageBitmap(Player.loadingCover(songList.get(singleList.get(position)).getPath()));
                        dialogFgDetailsTvList.get(0).setText(songList.get(singleList.get(position)).getSong());
                        dialogFgDetailsTvList.get(1).setText(songList.get(singleList.get(position)).getSinger());
                        dialogFgDetailsTvList.get(2).setText(songList.get(singleList.get(position)).getAlbum());
                        dialogFgDetailsTvList.get(3).setText(songList.get(singleList.get(position)).getPath());
                        String fileSize =  String.format("%.2f",((float)songList.get(singleList.get(position)).getFileSize()/(1024*1024)));
                        dialogFgDetailsTvList.get(4).setText(songList.get(singleList.get(position)).getDuration()+", "
                                +fileSize+"MB");
                        dialogFg.dismiss();
                    }
                });
                return true;
            }

        });
    }
    @Override
    public void sendPlayerListAndItem(List<Integer> singleList, int position) {
//        player.setUsingPositionList(singleList);
//        player.firstClickListItem(singleList.get(position));
    }
    @Override
    public void sendSingleListListView(ListView listView) {
        this.singleListListView = listView;
    }
    @Override
    public void sendSameStringListListView(ListView sameStringListListView) {
        this.currentSameStringListListView = sameStringListListView;
    }
    @Override
    public void sendItemPositionAndFromTop(int position, int fromTop) {
        currentListViewPosition = position;
        currentListViewPositionFromTop =fromTop;
    }
    @Override
    public void DialogSendData(LinearLayout linearLayout, List<TextView> tvList) {
        this.dialogFgLlBg = linearLayout;
        this.dialogFgTvList = tvList;
    }
    @Override
    public void sendDetailsIdList(LinearLayout linearLayout, ImageView imageView, List<TextView> textViewList) {
        detailsFgLlBg = linearLayout;
        dialogFgDetails_iv0 = imageView;
        dialogFgDetailsTvList = textViewList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_layout);
        if (songList==null){
            if ( Saver.readSongList("firstList")==null
                    //||((List<Song>)Saver.readSongList("firstList")).isEmpty()
                    ) {
                songList = AudioUtils.getSongs(this);
                Saver.saveSongList("firstList", songList);
            }
            else {
                songList =(List<Song>) Saver.readSongList("firstList");
            }
            //firstDataList =(List<Object>) Saver.readData("lastSavedObject");
            //songList = (List<Song>) firstDataList.get(0);
        }
        initIntent();
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
        initFindViewIdBy();
        initServiceThenStartIt();
        initAdapter();
        initFragment();
        //完善addToPlaylist用的
        dialogFg_addToPlaylist = new DialogFg_AddToPlaylist();

        if (lastOnPausePage==-1){
            showFragment(fgList.get(0));
            sameStringList.setText(titleString.get(0));
            spinnerForFgList_sameString.setSelection(0,true);
        }
        else if (lastOnPausePage>=0&&lastOnPausePage<5){
            if (lastOnPausePage!=4){
                showFragment(fgList.get(lastOnPausePage));
                spinnerForFgList_sameString.setSelection(lastOnPausePage,true);
                sameStringList.setText(titleString.get(lastOnPausePage));
            }
            else {
//                showFragment(fgList.get(4));
//                sameSingleFragment.initListView(singleList);
                handler.sendEmptyMessage(Data.Player_Loading_Msg);
            }
        }
        //showFragment(fgList.get(0));
        //当这个数字不为-1时, 代表onPause时不再是默认的那一页了

    }
    @Override
    protected void onResume(){
        super.onResume();
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
    }
    @Override
    protected void onPause(){
        super.onPause();
        handler.removeCallbacksAndMessages(null);
        //这里取值前还要判断singleListListView是不是null, 太麻烦, 我写在回调里了
//        currentListViewPosition=singleListListView.getFirstVisiblePosition();
//        currentListViewPositionFromTop = singleListListView.getChildAt(0).getTop();
        lastOnPausePage = spinnerForFgList_sameString.getSelectedItemPosition();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        //removeAllFragment();
        //removeAllFgBackStackEntry();
    }
    @Override
    public void onBackPressed(){
        //如果super了, 那么按下返回键就会回调fragment的backStack,
        //会出现item页走掉的现象, 所以干脆不super了, 控制起来更容易
        //super.onBackPressed();
        if(firstTime==0){
            firstTime = System.currentTimeMillis();
            Toast.makeText(this,"exit",Toast.LENGTH_SHORT).show();
        }
        else if (firstTime!=0){
            secondTime =System.currentTimeMillis();
            if (secondTime- firstTime>2000){
                firstTime = 0; secondTime=0;
                Toast.makeText(this,"exit",Toast.LENGTH_SHORT).show();
            }
            else if (secondTime-firstTime<=2000){
                intentPackThenExit();
            }
        }
    }
    public void initFindViewIdBy(){
        settingBtn=findViewById(R.id.setting);
        songName = findViewById(R.id.songName_fg);
        songSinger = findViewById(R.id.songSinger_fg);
        duration = findViewById(R.id.duration_fg);
        playerPic = findViewById(R.id.playerPic_fg);
        startOrPause = findViewById(R.id.startOrPause_fg);
        nextSong = findViewById(R.id.nextSong_fg);
        order = findViewById(R.id.order_fg);
        searchView=findViewById(R.id.searchView_fg);
        switchBtn= findViewById(R.id.switchBtn);
        spinnerForFgList_sameString= findViewById(R.id.spinnerForFgList);
        sameStringList =findViewById(R.id.sameStringList);
    }
    public void initFragment(){
        if (getLastCustomNonConfigurationInstance() != null){
            //转到屏幕时取得包
            fm=getSupportFragmentManager();
            removeAllFgBackStackEntry();
            List<Object> mObjectList = (List<Object>)getLastCustomNonConfigurationInstance();
            //Log.d(TAG, "s\n"+getLastCustomNonConfigurationInstance());
            fgList = (List<Fragment>)  mObjectList.get(0)  ;
            lastOnPausePage =(int) mObjectList.get(1);
            singleList =(List<Integer>) mObjectList.get(2);
            currentListViewPosition = (int)mObjectList.get(3);
            currentListViewPositionFromTop =(int) mObjectList.get(4);
            titleString.set(4, (String) mObjectList.get(5)   );
            //转到屏幕时才有下面这句, 因为这个时在转到屏幕时打包的
//            if (mObjectList.size()>=7)
//                isEverIntoFg4= (boolean) mObjectList.get(6);
            playlistFragment =(MainPlaylistFg) fgList.get(0);
            albumFragment = (MainAlbumListFg) fgList.get(1);
            singerFragment = (MainSingerListFg) fgList.get(2);
            pathFragment =(MainPathListFg) fgList.get(3);
            sameSingleFragment = (MainSameStringItemFg) fgList.get(4);
        }
        else {
            unPackBundle();
            fgList = new ArrayList<>();
            playlistFragment = MainPlaylistFg.newInstance();
            fgList.add(playlistFragment);
            albumFragment = MainAlbumListFg.newInstance();
            fgList.add(albumFragment);
            singerFragment = MainSingerListFg.newInstance();
            fgList.add(singerFragment);
            pathFragment = MainPathListFg.newInstance();
            fgList.add(pathFragment);
            sameSingleFragment = MainSameStringItemFg.newInstance();
            fgList.add(sameSingleFragment);
        }
        dialogFg = new DialogFg();
        dialogFgDetails = new DialogFgDetails();

        fgListString = new ArrayList<>();
        fgListString.add("playlistFragment");
        fgListString.add("albumFragment");
        fgListString.add("singerFragment");
        fgListString.add("pathFragment");
        fgListString.add("sameSingleFragment");

        fm= getSupportFragmentManager();
        addFg(playlistFragment, fgListString.get(0));
        addFg(albumFragment, fgListString.get(1));
        addFg(singerFragment, fgListString.get(2));
        addFg( pathFragment, fgListString.get(3));
        addFg(sameSingleFragment, fgListString.get(4));
    }
    public void infoUpdate() {
        if (songList==null||songList.isEmpty())
            return;
        songName.setText(songList.get(player.getUsingPositionId()).getSong());
        songSinger.setText(songList.get(player.getUsingPositionId()).getSinger());
        duration.setText(songList.get(player.getUsingPositionId()).getDuration());
        playerPic.setImageBitmap(Player.loadingCover(songList.get(player.getUsingPositionId()).getPath()));
        order.setText(Data.Order_Mode.get(player.order_Mode));
        startOrPause.setText(player.mediaPlayer.isPlaying() ? "Pause" : "Play");
    }
    public void initServiceThenStartIt() {
        usingPosition = getIntent().getIntExtra("usingPosition", 1);
        Intent startIntent = new Intent(MainFragmentActivity.this, MusicService.class);
        Intent bindIntent = new Intent(MainFragmentActivity.this,MusicService.class);
        bindService(bindIntent,sc,BIND_AUTO_CREATE);
        startService(startIntent);
        usingPosition=6;
    }
    public void initAdapter(){
        titleString = new ArrayList<>();
        titleString.add("Playlist");
        titleString.add("AlbumList");
        titleString.add("SingerList");
        titleString.add("PathList");
        titleString.add("Item");
        listCountList= new ArrayList();
        spinnerAdapter = new SpinnerForFgList_Adapter(this, titleString);
        spinnerForFgList_sameString.setAdapter(spinnerAdapter);
    }
    public void initIntent(){
        itemCount=getIntent().getIntExtra("itemCount",1);
    }
    public void intentPackThenChange(int whichActivity){
        Intent intent=null;
        switch (whichActivity){
            case Data.MainActivity:  intent =new Intent(MainFragmentActivity.this, MainActivity.class);break;
            case Data.PlayerActivity:  intent =new Intent(MainFragmentActivity.this, PlayerActivity.class);break;
            case Data.VpFragmentActivity: intent =new Intent(MainFragmentActivity.this, VpFragmentActivity.class);break;
            case Data.EditActivity:  {
                intent =new Intent(MainFragmentActivity.this, EditActivity.class);
                intent.putExtra("isFromPlaylist", isFromPlaylist);
                intent.putExtra("whichPositionFromPlaylist", whichPositionFromPlaylist);
                break;
            }
            case Data.SettingActivity :{intent =new Intent(MainFragmentActivity.this, SettingActivity.class);break;}
            case Data.SearchActivity:   {intent = new Intent(MainFragmentActivity.this, SearchActivity.class);break;}
            default:break;
        }
        intent.putExtras(packBundleAndLeaveThisAct());
        intent.putExtra("itemCount", itemCount);
        handler.removeCallbacksAndMessages(null);
        unbindService(sc);
        startActivity(intent);
        finish();
    }
    public void intentPackThenExit(){
        handler.removeCallbacksAndMessages(null);
        player = null;
        Intent stopIntent =new Intent(MainFragmentActivity.this, MusicService.class);
        unbindService(sc);
        stopService(stopIntent);
        finish();
    }
    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            myBinder = (MusicService.MyBinder) iBinder;
            if (myBinder.getSongListFromService()==null)
                myBinder.setServiceSongListFromActivity(songList);
            if (songList!=null){
                myBinder.newInstancePlayer(songList);
            }
            player=myBinder.getPlayer();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
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
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPackThenChange(Data.SearchActivity);
            }
        });
        playerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPackThenChange(Data.PlayerActivity);
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPackThenChange(Data.SettingActivity);
            }
        });
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //先检查null, 不要先检查isEmpty, 因为isEmpty是要singleList!=null才行. 而if会先做左边的, 注意注意..
                //if (singleList!=null&&!singleList.isEmpty()&&isEverIntoFg4)
                if (singleList!=null&&!singleList.isEmpty()){
                    Saver.saveData("singleListToEditActivity",  singleList, false);
                    intentPackThenChange(Data.EditActivity);
                }
            }
        });
        spinnerForFgList_sameString.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showFragment(fgList.get(position));
                sameStringList.setText(titleString.get(position));
                //lastOnPausePage=position;
                //如果显示第4页, 自动显示可编辑开关, 且有列表可编辑
                if (position==4&&singleList!=null&&!singleList.isEmpty()){
                    switchBtn.setVisibility(View.VISIBLE);
                }
                else {
                    switchBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    public void setSingleList(List<Integer> singleList) {
        this.singleList = singleList;
    }

    public Player getPlayer() {
        return player;
    }
    public void removeAllFgBackStackEntry(){
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            fm.popBackStackImmediate();
        }
    }

    private void addFg(Fragment fragment, String stringTag){
        for (Fragment fg: getSupportFragmentManager().getFragments())
            if (fragment==fg){
                fragment=fg;
                return;
        }
        if(!fragment.isAdded()) {
            fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragmentList, fragment, stringTag);
            //这个addToBackStack不是多余的, 他是说当你不用时, 可以把不用的fg扔到addToBackStack
            //然后我再下一次进入该act时, 会因为removeAllBackStackxxxx所以又会清掉addToBackStack
            //的fg, 减少了内存
            ft.addToBackStack(stringTag);
            ft.commit();
            //fgList.add(fragment);
        }
    }

    private void showFragment(Fragment fragment){
        for (Fragment fg : fgList){
            if (fg!=fragment)
                getSupportFragmentManager().beginTransaction().hide(fg).commit();
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    private void showDialogFg(){
        dialogFg.showNow(getSupportFragmentManager(), "dialogFg");
    }
    private void showDetailsDialogFg(){
        dialogFgDetails.showNow(getSupportFragmentManager(),"dialogFgDetails");
    }
    private void showDialogFgAddToPlaylist(){
        dialogFg_addToPlaylist.showNow(getSupportFragmentManager(), "dialogFg_addToPlaylist");
        dialogFg_addToPlaylist.showViewForAddToPlaylist();
    }

    private void removeAllFragment(){
        for (Fragment fg:fgList)
            getSupportFragmentManager().beginTransaction().detach(fg).commit();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        super.onRetainCustomNonConfigurationInstance();
        List<Object> objectList = new ArrayList<>();
        objectList.add(fgList);
        objectList.add(lastOnPausePage);
        objectList.add(singleList);
        //currentListViewPosition就是下面的值
        objectList.add(currentListViewPosition);
        objectList.add(currentListViewPositionFromTop);
        objectList.add(titleString.get(4));
        //LeaveAct的时候没有打包 isEverIntoFg4, 所以这里要注意不要数组越界
        //在解包时要判断有没有7个数据
        //objectList.add(isEverIntoFg4);
        return objectList;
    }
    public Bundle packBundleAndLeaveThisAct(){
        Bundle bundle =new Bundle();
        List<Object> objectList = new ArrayList<>();
        //由于没有保存fgList, 所以提取的时候下标index要改变一个单位
        //objectList.add(fgList);
        objectList.add(spinnerForFgList_sameString.getSelectedItemPosition());
        objectList.add(singleList);
        //currentListViewPosition就是下面的值
        //为什么要两个判断? 因为其实第一个判断在initFragment后就回调获得了,
        //但是那个时候的fg并没有listView的Data, 所以根本就没有setAdapter,
        //也就是说那个时候fg的listView的列表根本没建立起来, 所以get不到任何子Item信息
        //如果有了singleList!=null, 那就说明至少点击了一次项目fg, 然后回调使得setAdapter
        //这个时候便有真正的列表了, 这个地方很阴险哈哈哈
        if (singleListListView!=null&&singleList!=null&&singleListListView.getChildAt(0)!=null){
            objectList.add(singleListListView.getFirstVisiblePosition());
            objectList.add(singleListListView.getChildAt(0).getTop());
        }
        else {
            objectList.add(0);
            objectList.add(0);
        }
        objectList.add(titleString.get(4));
        //LeaveAct的时候没有打包 isEverIntoFg4, 所以这里要注意不要数组越界
        //在解包时要判断有没有7个数据
        bundle.putSerializable("objectList",(Serializable) objectList);
        return bundle;
    }
    public void unPackBundle(){
        if (getIntent().getSerializableExtra("objectList")!=null){
            List<Object> mObjectList = (List<Object>) getIntent().getSerializableExtra("objectList");
            lastOnPausePage =(int) mObjectList.get(0);
            singleList =(List<Integer>) mObjectList.get(1);
            currentListViewPosition = (int)mObjectList.get(2);
            currentListViewPositionFromTop =(int) mObjectList.get(3);
            titleString.set(4, (String) mObjectList.get(4)   );
            //LeaveAct的时候没有打包 isEverIntoFg4, 所以这里要注意不要数组越界
        }
    }

    private void delFg(String stringTag){
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(stringTag)!=null){
            Fragment fragment = fm.findFragmentByTag(stringTag);
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case Data.Player_Loading_Msg:{
                    if (player==null){
                        msg=obtainMessage(Data.Player_Loading_Msg);
                        handler.sendMessageDelayed(msg,100);
                    }
                    else {
                        handler.removeCallbacksAndMessages(null);
                        initOnClick();
                        infoUpdate();
                        msg=obtainMessage(Data.MainFragmentActivityInfoUpdate);
                        handler.sendMessage(msg);
                        if (lastOnPausePage==4){
                            sendSingleList(singleList, null);
                            singleListListView.
                                    setSelectionFromTop(currentListViewPosition,currentListViewPositionFromTop);
                        }
                    }
                    break;
                }
                case Data.MainFragmentActivityInfoUpdate:{
                    if (player.musicInfoNeedUpdate) {
                        infoUpdate();
                        player.musicInfoNeedUpdate=false;
                    }
                    msg=obtainMessage(Data.MainFragmentActivityInfoUpdate);
                    handler.sendMessageDelayed(msg,500);
                    break;
                }
                default: break;
            }
        }
    };
}
