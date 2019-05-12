package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.MainFragment.Adapter.SpinnerForFgList_Adapter;
import com.lc.musicplayer.MainFragment.FgSendDataToAct;
import com.lc.musicplayer.MainFragment.MainAlbumListFg;
import com.lc.musicplayer.MainFragment.MainPathListFg;
import com.lc.musicplayer.MainFragment.MainPlaylistFg;
import com.lc.musicplayer.MainFragment.MainSameStringItemEditFg;
import com.lc.musicplayer.MainFragment.MainSameStringItemFg;
import com.lc.musicplayer.MainFragment.MainSingerListFg;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.SameStringSongsFragment_Adapter;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;
/**
 * 终于知道为什么我不能愉快的用Fragment了,,, 原来时Intent搞的鬼!!!
 * 我的songList占了快1MB, 因为intent还要保存其他信息, 所以intent肯定超过1MB了,
 * 所以如果擅自加入songList在fg的bundle的话, 在songlist保存songlist的时候肯定要完蛋的,
 * 也就是说简单的锁屏 , 都会经历保存songlist的回调, 然后一保存就GG
 * 我现在打算用本地文件来做, 虽然会慢一点, 但是稳妥~!**/
public class MainFragmentActivity extends AppCompatActivity implements FgSendDataToAct {
    private MusicService.MyBinder myBinder;
    private MainPlaylistFg playlistFragment;
    private MainAlbumListFg albumFragment;
    private MainSingerListFg singerFragment;
    private MainPathListFg pathFragment;
    private MainSameStringItemFg sameSingleFragment;
    private MainSameStringItemEditFg sameStringSongsFragment_edit;
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
    private TextView songName , songSinger, duration;
    private ImageView playerPic;
    private Button startOrPause , nextSong, order, settingBtn, exitBtn;
    private SearchView searchView;
    private int fgNum, itemCount, usingPosition;
    private int lastOnPausePage = -1;
    private int currentListViewPosition =-1;
    private int currentListViewPositionFromTop = 0;
    private long firstTime, secondTime;
    private Switch switchBtn;
    private String TAG = "Ser1212";
    private FrameLayout frameLayout;
    private int i= 3 ;
    private ListView singleListListView, currentSameStringListListView;
    private SameStringSongsFragment_Adapter sAdapter;

    /**getFragments我猜返回的是当前显示的所有Fragment, 加载了但是没显示的不在getFragments里面
     * 而getBackStackEntryCount()是退回到这个stack的fg个数
     * fg**/
    @Override
    public void sendSameString(String string,int sameStringItemsCount) {
        Toast.makeText(this, string+", "+sameStringItemsCount+"首",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void sendSingleList(List<Integer> singleListFromFg) {
        this.singleList = singleListFromFg;
        showFragment(fgList.get(4));
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

        }
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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_layout);
        if (songList==null)
            songList =(List<Song>) Saver.readSongList("firstList");
        initIntent();
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
        initFindViewIdBy();
        initServiceThenStartIt();
        initAdapter();
        initFragment();
        if (lastOnPausePage==-1){
            showFragment(fgList.get(0));
            spinnerForFgList_sameString.setSelection(0,true);
        }
        else if (lastOnPausePage>=0&&lastOnPausePage<5){
            if (lastOnPausePage!=4){
                showFragment(fgList.get(lastOnPausePage));
                spinnerForFgList_sameString.setSelection(lastOnPausePage,true);
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
        exitBtn =findViewById(R.id.exit);
        spinnerForFgList_sameString= findViewById(R.id.spinnerForFgList);
    }
    public void initFragment(){
        if (getLastCustomNonConfigurationInstance() != null){
            fm=getSupportFragmentManager();
            removeAllFgBackStackEntry();
            List<Object> mObjectList = (List<Object>)getLastCustomNonConfigurationInstance();
            //Log.d(TAG, "s\n"+getLastCustomNonConfigurationInstance());
            fgList = (List<Fragment>)  mObjectList.get(0)  ;
            lastOnPausePage =(int) mObjectList.get(1);
            singleList =(List<Integer>) mObjectList.get(2);
            currentListViewPosition = (int)mObjectList.get(3);
            currentListViewPositionFromTop =(int) mObjectList.get(4);
            playlistFragment =(MainPlaylistFg) fgList.get(0);
            albumFragment = (MainAlbumListFg) fgList.get(1);
            singerFragment = (MainSingerListFg) fgList.get(2);
            pathFragment =(MainPathListFg) fgList.get(3);
            sameSingleFragment = (MainSameStringItemFg) fgList.get(4);
        }
        else {
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
            case Data.FragmentActivity: intent =new Intent(MainFragmentActivity.this, FragmentActivity.class);break;
            default:break;
        }
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
            if (songList!=null)
                myBinder.newInstancePlayer(songList);
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
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPackThenExit();
            }
        });
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, ": \ngetBackStackEntryCount  "+
                        getSupportFragmentManager().getBackStackEntryCount()+
                        "\ngetSupportFragmentManager().getFragments() " +
                        getSupportFragmentManager().getFragments()+"\ngetPrimaryNavigationFragment "
                        +fm.getPrimaryNavigationFragment()+"\n isAdded? "+ fgList.get(2).isAdded());
            }
        });
        spinnerForFgList_sameString.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showFragment(fgList.get(position));
                //lastOnPausePage=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
//    public void runFgTransaction(int i){
//        fm= getSupportFragmentManager();
//        if (i==Data.SameStringSingleList ){
//            if (titleString.size()<5) {
//                titleString.add("Single List Item");
//                listCountList.add(singleList.size());
//            }
//            listCountList.set(4,singleList.size());
//            ft= fm.beginTransaction();
//            ft.replace(R.id.fragmentList,MainSameStringItemFg.newInstance(singleList ));
//            ft.commit();
//        }
//        else if (i >= Data.SameStringSingleListEdit){
//            if (titleString.size()<6){
//                titleString.add("Item Edit");
//                listCountList.add(singleList.size());
//            }
//            listCountList.set(5,singleList.size());
//            ft= fm.beginTransaction();
//            ft.replace(R.id.fragmentList,MainSameStringItemEditFg.newInstance(songList, singleList));
//            ft.addToBackStack(null);
//            ft.commit();
//        }
//        else {
//            ft=fm.beginTransaction();
//            ft.replace(R.id.fragmentList, fgList.get(i));
//            //ft.addToBackStack(null);
//            ft.commit();
//        }
//        spinnerForFgList_sameString.setSelection(i);
//    }

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
        return objectList;
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
                            sendSingleList(singleList);
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
