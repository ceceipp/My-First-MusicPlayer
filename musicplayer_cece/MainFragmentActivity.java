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
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.MainFragment.Adapter.SpinnerForFgList_Adapter;
import com.lc.musicplayer.MainFragment.MainAlbumListFg;
import com.lc.musicplayer.MainFragment.MainPathListFg;
import com.lc.musicplayer.MainFragment.MainPlaylistFg;
import com.lc.musicplayer.MainFragment.MainSameStringItemEditFg;
import com.lc.musicplayer.MainFragment.MainSameStringItemFg;
import com.lc.musicplayer.MainFragment.MainSingerListFg;
import com.lc.musicplayer.R;
import com.lc.musicplayer.fragment.AlbumFragment;
import com.lc.musicplayer.fragment.PathFragment;
import com.lc.musicplayer.fragment.PlaylistFragment;
import com.lc.musicplayer.fragment.SameStringSongsFragment;
import com.lc.musicplayer.fragment.SameStringSongsFragment_Edit;
import com.lc.musicplayer.fragment.SingerFragment;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentActivity extends AppCompatActivity {
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
    private List<Song>  songList;
    private List<SameStringIdList> sameStringIdList;
    private List<Fragment> fgList;
    private Player player;
    private TextView songName , songSinger, duration;
    private ImageView playerPic;
    private Button startOrPause , nextSong, order, settingBtn, exitBtn;
    private SearchView searchView;
    private int fgNum, itemCount, usingPosition;
    private Switch switchBtn;
    private String TAG = "Ser1212";
    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_layout);
        songList =(List<Song>) Saver.readSongList("firstList");
        initIntent();
        initFindViewIdBy();
        initFragment();
        fragmentTransaction();
        initServiceThenStartIt();
        initOnClick();
    }

    protected void onResume(){
        super.onResume();
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
    }
    @Override
    protected void onPause(){
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
    protected void onDestroy(){
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        player.musicInfoNeedUpdate=false;
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
        playlistFragment = MainPlaylistFg.newInstance(songList,null);
        albumFragment = MainAlbumListFg.newInstance(songList,null);
        singerFragment = MainSingerListFg.newInstance(songList,null);
        pathFragment = MainPathListFg.newInstance(songList,null);
        sameSingleFragment = MainSameStringItemFg.newInstance(songList,null);
        sameStringSongsFragment_edit = MainSameStringItemEditFg.newInstance(songList, null);
        fgList = new ArrayList<>();
        fgList.add(playlistFragment);
        fgList.add(albumFragment);
        fgList.add(singerFragment);
        fgList.add(pathFragment);
        fgList.add(sameSingleFragment);
        fgList.add(sameStringSongsFragment_edit);
        //PathFragment pathFragment2222 = PathFragment.newInstance();
        //sameSingleFragment= SameStringSongsFragment.newInstance();
       // sameStringSongsFragment_edit=SameStringSongsFragment_Edit.newInstance();
    }
    public void fragmentTransaction(){
        fm= getSupportFragmentManager();
        ft=fm.beginTransaction();
        ft.add(R.id.fragmentList, playlistFragment);
//        ft.add(R.id.fragmentList, albumFragment);
//        ft.add(R.id.fragmentList, singerFragment);
//        ft.add(R.id.fragmentList, pathFragment);
        //ft.add(R.id.fragmentList, pathFragment2222);
        // ft.addToBackStack(null);
        ft.commit();
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
        List<String> titleString = new ArrayList<>();
        titleString.add("Playlist");
        titleString.add("AlbumList");
        titleString.add("SingerList");
        titleString.add("PathList");
        titleString.add("Single List Item");
        titleString.add("Item Edit");
        //防止出错
        sameStringIdList = Player.idToSameAlbumConvert(songList);
        spinnerAdapter = new SpinnerForFgList_Adapter(this, titleString, sameStringIdList);
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
                intentPackThenExit();
            }
        });
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(MainFragmentActivity.this, " "+ fm.getFragments().size(), Toast.LENGTH_SHORT).show();
            }
        });
        spinnerForFgList_sameString.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                runFgTransaction(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void runFgTransaction(int i){
        ft=fm.beginTransaction();
        ft.replace(R.id.fragmentList, fgList.get(i));
        ft.addToBackStack(null);
        ft.commit();
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
                        infoUpdate();initAdapter();
                        msg=obtainMessage(Data.MainFragmentActivityInfoUpdate);
                        handler.sendMessage(msg);
                    }
                    break;
                }
                case Data.MainFragmentActivityInfoUpdate:{
                    if (player.musicInfoNeedUpdate) {
                        infoUpdate(); player.musicInfoNeedUpdate=false;
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
