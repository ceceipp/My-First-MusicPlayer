package com.lc.musicplayer.test;

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

import com.lc.musicplayer.MainActivity;
import com.lc.musicplayer.MainFragment.Adapter.SpinnerForFgList_Adapter;
import com.lc.musicplayer.MainFragment.FgSendDataToAct;
import com.lc.musicplayer.MainFragment.MainAlbumListFg;
import com.lc.musicplayer.MainFragment.MainPathListFg;
import com.lc.musicplayer.MainFragment.MainPlaylistFg;
import com.lc.musicplayer.MainFragment.MainSameStringItemEditFg;
import com.lc.musicplayer.MainFragment.MainSameStringItemFg;
import com.lc.musicplayer.MainFragment.MainSingerListFg;
import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.PlayerActivity;
import com.lc.musicplayer.R;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.SameStringSongsFragment_Adapter;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
/**
 * 终于知道为什么我不能愉快的用Fragment了,,, 原来时Intent搞的鬼!!!
 * 我的songList占了快1MB, 因为intent还要保存其他信息, 所以intent肯定超过1MB了,
 * 所以如果擅自加入songList在fg的bundle的话, 在songlist保存songlist的时候肯定要完蛋的,
 * 也就是说简单的锁屏 , 都会经历保存songlist的回调, 然后一保存就GG
 * 我现在打算用本地文件来做, 虽然会慢一点, 但是稳妥~!**/
public class TestActivity extends AppCompatActivity implements FgSendDataToAct {
    private MusicService.MyBinder myBinder;
    private MainPlaylistFg playlistFragment;
    private MainAlbumListFg albumFragment;
    private MainSingerListFg singerFragment;
    private MainPathListFg pathFragment;
    private TestFg sameSingleFragment;
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
    private int fgNum, itemCount, usingPosition, lastOnPausePage;
    private long firstTime, secondTime;
    private Switch switchBtn;
    private String TAG = "Ser1212";
    private FrameLayout frameLayout;
    private int i= 3 ;
    private Fragment singleFg;
    private SameStringSongsFragment_Adapter sAdapter;
    private ListView singleListListView;

    /**getFragments我猜返回的是当前显示的所有Fragment, 加载了但是没显示的不在getFragments里面
     * 而getBackStackEntryCount()是退回到这个stack的fg个数
     * fg**/
    @Override
    public void sendSameString(String string,int i) {
        Toast.makeText(this, string,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void sendSingleList(List<Integer> singleList) {
        this.singleList = singleList;
        Log.d(TAG, "sendSingleList: ");
        showFragment(fgList.get(4));
        //setListView();
    }

    @Override
    public void sendSameStringListListView(ListView listView) {

    }


    public void setListView(){
        sAdapter= new SameStringSongsFragment_Adapter(MyApplication.getContext()
                ,this.singleList, songList,Data.SameStringSingleList, R.layout.samestringsongs_item_layout);
        singleListListView.setAdapter(sAdapter);
        singleListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                player.setUsingPositionList(singleList);
                player.firstClickListItem(singleList.get(position) );
            }
        });
    }

    @Override
    public void sendItemPositionAndFromTop(int position, int fromTop) {

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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_layout);
        if (songList==null)
            songList =(List<Song>) Saver.readSongList("firstList");
        itemCount=getIntent().getIntExtra("itemCount",1);
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
        initFindViewIdBy();
        initServiceThenStartIt();
        titleString = new ArrayList<>();
        titleString.add("Playlist");
        titleString.add("AlbumList");
        titleString.add("SingerList");
        titleString.add("PathList");
        titleString.add("Item");
        listCountList= new ArrayList();
        listCountList.add( Player.initDefaultPlaylist(songList,null).size() );
        listCountList.add(Player.idToSameAlbumConvert(songList).size());
        listCountList.add(Player.idToSameSingerConvert(songList).size());
        listCountList.add(Player.idToSamePathConvert(songList).size());
        listCountList.add(0);
        spinnerAdapter = new SpinnerForFgList_Adapter(this, titleString);
        spinnerForFgList_sameString.setAdapter(spinnerAdapter);
        playlistFragment = MainPlaylistFg.newInstance();
        albumFragment = MainAlbumListFg.newInstance();
        singerFragment = MainSingerListFg.newInstance();
        pathFragment = MainPathListFg.newInstance();
        sameSingleFragment = TestFg.newInstance();
        fgList = new ArrayList<>();
        fgListString = new ArrayList<>();
        fgListString.add("playlistFragment");
        fgListString.add("albumFragment");
        fgListString.add("singerFragment");
        fgListString.add("pathFragment");
        fgListString.add("ItemFragment");
        fm= getSupportFragmentManager();
        addFg(playlistFragment, fgListString.get(0));
        addFg(albumFragment, fgListString.get(1));
        addFg(singerFragment, fgListString.get(2));
        addFg( pathFragment, fgListString.get(3));
        addFg(sameSingleFragment, fgListString.get(4));
        showFragment(fgList.get(0));
        //当这个数字不为-1时, 代表onPause时不再是默认的那一页了
        lastOnPausePage = -1;
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
        //要把fg全部取出才不会出错,,我也不清楚为何,,为了让下次还有这个视图, 我要把最后一页的内容页码记录下来
        //removeAllFgBackStackEntry();
        //lastOnPausePage = spinnerForFgList_sameString.getSelectedItemPosition();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
//        if(firstTime==0){
//            firstTime = System.currentTimeMillis();
//            Toast.makeText(this,"exit",Toast.LENGTH_SHORT).show();
//        }
//        else if (firstTime!=0){
//            secondTime =System.currentTimeMillis();
//            if (secondTime- firstTime>2000){
//                firstTime = 0; secondTime=0;
//                Toast.makeText(this,"exit",Toast.LENGTH_SHORT).show();
//            }
//            else if (secondTime-firstTime<=2000){
//                intentPackThenExit();
//            }
//        }
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

        playlistFragment = MainPlaylistFg.newInstance();
        albumFragment = MainAlbumListFg.newInstance();
        singerFragment = MainSingerListFg.newInstance();
        pathFragment = MainPathListFg.newInstance();
        fgList = new ArrayList<>();
        fgListString = new ArrayList<>();
        fgListString.add("playlistFragment");
        fgListString.add("albumFragment");
        fgListString.add("singerFragment");
        fgListString.add("pathFragment");
    }
    public void fragmentTransaction(){
        fm= getSupportFragmentManager();
        addFg(playlistFragment, fgListString.get(0));
        addFg(albumFragment, fgListString.get(1));
        addFg(singerFragment, fgListString.get(2));
        addFg( pathFragment, fgListString.get(3));
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
        Intent startIntent = new Intent(TestActivity.this, MusicService.class);
        Intent bindIntent = new Intent(TestActivity.this,MusicService.class);
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
        listCountList= new ArrayList();
        listCountList.add( Player.initDefaultPlaylist(songList,null).size() );
        listCountList.add(Player.idToSameAlbumConvert(songList).size());
        listCountList.add(Player.idToSameSingerConvert(songList).size());
        listCountList.add(Player.idToSamePathConvert(songList).size());
        spinnerAdapter = new SpinnerForFgList_Adapter(this, titleString);
        spinnerForFgList_sameString.setAdapter(spinnerAdapter);
    }
    public void initIntent(){
        itemCount=getIntent().getIntExtra("itemCount",1);
    }
    public void intentPackThenChange(int whichActivity){
        Intent intent=null;
        switch (whichActivity){
            case Data.MainActivity:  intent =new Intent(TestActivity.this, MainActivity.class);break;
            case Data.PlayerActivity:  intent =new Intent(TestActivity.this, PlayerActivity.class);break;
            case Data.FragmentActivity: intent =new Intent(TestActivity.this, FragmentActivity.class);break;
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
        Intent stopIntent =new Intent(TestActivity.this, MusicService.class);
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
            myBinder.newInstancePlayer(songList);
            player=myBinder.getPlayer();
            //infoUpdate();
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
                //intentPackThenExit();
                fm = getSupportFragmentManager();
                ft= fm.beginTransaction();
                ft.show(fgList.get(2));
                ft.commit();
            }
        });
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, ".\nfm.getFragments(): " + fm.getFragments() +"    fm.getBackStackEntryCount():  "+fm.getBackStackEntryCount());
                //addFg(fgList.get(3),fgListString.get(3));
                setListView();
            }
        });
        spinnerForFgList_sameString.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //runFgTransaction(position);
                showFragment(fgList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    /**
     * 注意, 加了  ft.addToBackStack(null); 之后, 按返回键暂时没办法让当前数字自动更新, 除非又用
     * handler哈哈哈
     * **/
    public void runFgTransaction(int i){
        fm= getSupportFragmentManager();
        if (i==Data.SameStringSingleList ){
            if (titleString.size()<5) {
                titleString.add("Single List Item");
                listCountList.add(singleList.size());
            }
            listCountList.set(4,singleList.size());
            if (fgListString.size()>=5)
                delFg(fgListString.get(4));
            singleFg=null;
            singleFg = MainSameStringItemFg.newInstance( );
            addFg(singleFg, titleString.get(4));
            showFragment(fgList.get(4));
        }
        else if (i >= Data.SameStringSingleListEdit){
            if (titleString.size()<6){
                titleString.add("Item Edit");
                listCountList.add(singleList.size());
            }
            listCountList.set(5,singleList.size());
            ft= fm.beginTransaction();
            ft.replace(R.id.fragmentList,MainSameStringItemEditFg.newInstance(songList, singleList));
            ft.addToBackStack(null);
            ft.commit();
        }
        else {
            ft=fm.beginTransaction();
            ft.replace(R.id.fragmentList, fgList.get(i));
            //ft.addToBackStack(null);
            ft.commit();
        }
        spinnerForFgList_sameString.setSelection(i);
    }

    public void runFgTransaction2(int i){
        fm= getSupportFragmentManager();
        if (i==Data.SameStringSingleList ){
            listCountList.set(4,singleList.size());
            ft= fm.beginTransaction();
            ft.replace(R.id.fragmentList,MainSameStringItemFg.newInstance( ));
            ft.addToBackStack(null);
            ft.commit();
        }
        else if (i >= Data.SameStringSingleListEdit){
            if (titleString.size()<6){
                titleString.add("Item Edit");
                listCountList.add(singleList.size());
            }
            listCountList.set(5,singleList.size());
            ft= fm.beginTransaction();
            ft.replace(R.id.fragmentList,MainSameStringItemEditFg.newInstance(songList, singleList));
            ft.addToBackStack(null);
            ft.commit();
        }
        else {
            ft=fm.beginTransaction();
            ft.replace(R.id.fragmentList, fgList.get(i));
            //ft.addToBackStack(null);
            ft.commit();
        }
        spinnerForFgList_sameString.setSelection(i);
    }

    public void showSingleListFg(List<Integer> singleList) {
        setSingleList(singleList);
//        runFgTransaction(Data.SameStringSingleList);
        showFragment(fgList.get(4));
        //然后再set这个fragment的view
    }

    public void setSingleList(List<Integer> singleList) {
        this.singleList = singleList;
    }

    public Player getPlayer() {
        return player;
    }
    public void removeAllFg(){
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            fm.popBackStackImmediate();
        }
        ft=fm.beginTransaction();
        ft.remove(fm.getFragments().get(0));
        ft.commit();
    }
//    private void addFg(Fragment fragment, String stringTag){
//        FragmentManager fm=getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(R.id.fragmentList, fragment, stringTag);
//        ft.addToBackStack(stringTag);
//        ft.commit();
//    }

    private void removeFg(String stringTag){
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(stringTag);
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();
    }

    private void addFg(Fragment fragment, String stringTag){
        if(!fragment.isAdded()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragmentList, fragment, stringTag);
            ft.addToBackStack(stringTag);
            ft.commit();
            fgList.add(fragment);
        }
    }

    private void showFragment(Fragment fragment){
        for (Fragment fg : fgList){
            if (fg!=fragment)
                getSupportFragmentManager().beginTransaction().hide(fg).commit();
        }
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
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
                        handler.sendMessageDelayed(msg,500);
                    }
                    else {
                        handler.removeCallbacksAndMessages(null);
//                        initFragment();
//                        fragmentTransaction();
                        Log.d(TAG, ".\ngetBackStackEntryCount: "+fm.getBackStackEntryCount()+"  getFragments "+fm.getFragments().size());
                        initOnClick();
                        infoUpdate();
                        //initAdapter();
//                        if (lastOnPausePage>=0&&lastOnPausePage<4){
//                                spinnerForFgList_sameString.setSelection(lastOnPausePage);
//                                lastOnPausePage = -1;
//                        }
//                        else if (lastOnPausePage==4&&lastOnPausePage==5){
//                            spinnerForFgList_sameString.setSelection(1);
//                            lastOnPausePage = -1;
//                        }
                        msg=obtainMessage(Data.MainFragmentActivityInfoUpdate);
                        handler.sendMessage(msg);
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
