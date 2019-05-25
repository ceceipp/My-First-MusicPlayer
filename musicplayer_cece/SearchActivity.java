package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lc.musicplayer.MainFragment.Adapter.SearchActivityAdapter;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private TextView songName , songSinger, duration;
    private ImageView playerPic;
    private Button startOrPause, nextSong, order;
    private SearchView searchViewAct;
    private ListView listView;
    private SearchActivityAdapter  adapter;
    private Player player;
    private List<Song> oriSongList ;
    private List<SameStringIdList> albumList;
    private List<SameStringIdList> singerList;

    private List<SameStringIdList> sameTitleList, sameAlbumList, sameSingerList, searchResultList;
    private MusicService.MyBinder myBinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page_layout);
        loadingSongList();
        initFindId();
        initService();
        listViewSetData();
    }
    public void loadingSongList(){
        if (oriSongList==null||oriSongList.isEmpty())
            oriSongList = AudioUtils.getSongs(this);
        else
            oriSongList =(List<Song>) Saver.readSongList("firstList");
    }
    public void initFindId() {
        songName = findViewById(R.id.songName1);
        songSinger = findViewById(R.id.songSinger1);
        duration = findViewById(R.id.duration1);
        playerPic = findViewById(R.id.playerPic1);
        startOrPause = findViewById(R.id.startOrPause1);
        nextSong = findViewById(R.id.nextSong1);
        order = findViewById(R.id.order1);
        searchViewAct = findViewById(R.id.searchViewAct);
        listView = findViewById(R.id.listViewSearchAct);
    }
    public void initService() {
        Intent startIntent = new Intent(this, MusicService.class);
        Intent bindIntent = new Intent(this,MusicService.class);
        bindService(bindIntent,sc,BIND_AUTO_CREATE);
        startService(startIntent);
    }
    public void listViewSetData(){
        sameTitleList = new ArrayList<>();
        sameAlbumList = new ArrayList<>();
        sameSingerList = new ArrayList<>();
        adapter = new SearchActivityAdapter(sameTitleList, sameAlbumList, sameSingerList, oriSongList,this, R.layout.searchactivity_item_layout );
        listView.setAdapter(adapter);
        albumList = (List<SameStringIdList>) Saver.readData("albumList");
        singerList = (List<SameStringIdList>) Saver.readData("singerList");
    }
    @Override
    protected void onResume() {
        super.onResume();
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            myBinder = (MusicService.MyBinder) iBinder;
            if (myBinder.getPlayer()!=null){
                player=myBinder.getPlayer();
                if (myBinder.getSongListFromService()!=null)
                    oriSongList=myBinder.getSongListFromService();
            }
            else {
                myBinder.setServiceSongListFromActivity(oriSongList);
                myBinder.newInstancePlayer(oriSongList);
                player = myBinder.getPlayer();
            }
            player.musicInfoNeedUpdate=true;
            infoUpdate(player, oriSongList);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
    public void initOnClick(){
        playerPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPackThenChange(SearchActivity.this, Data.PlayerActivity);
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
            @Override//带着 searchResultList 进入EditAct
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        searchViewAct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                sameTitleList = Player.getSearchResultListFromOriListSongTitle(s, oriSongList);
                sameAlbumList = Player.getSearchResultListFromOriListAlbum(s, albumList);
                sameSingerList = Player.getSearchResultListFromOriListSinger(s, singerList);
                adapter.setSame3List(sameTitleList, sameAlbumList, sameSingerList);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Saver.saveData("fromSearchActivity", adapter.getItem(position), false);
                intentPackThenChange(SearchActivity.this, Data.EditActivity);
            }
        });
    }

    @Override
    public void onBackPressed() {
        intentPackThenChange(this , Data.MainFragmentActivity);
    }

    public void infoUpdate(Player player, List<Song> song_list) {
        if (player!=null&&player.musicInfoNeedUpdate&&song_list!=null&&!song_list.isEmpty()){
            songName.setText(song_list.get(player.getUsingPositionId()).getSong());
            songSinger.setText(song_list.get(player.getUsingPositionId()).getSinger());
            duration.setText(song_list.get(player.getUsingPositionId()).getDuration());
            playerPic.setImageBitmap(Player.loadingCover(song_list.get(player.getUsingPositionId()).getPath()));
            order.setText(Data.Order_Mode.get(player.order_Mode));
            startOrPause.setText(player.mediaPlayer.isPlaying() ? "Pause" : "Play");
            player.musicInfoNeedUpdate=false;
        }
    }
    public void intentPackThenChange(Context thisActivity, int whichActivity){
        Intent intent=null;
        switch (whichActivity){
            case Data.MainActivity:  intent =new Intent(thisActivity, MainActivity.class);break;
            case Data.PlayerActivity:  intent =new Intent(thisActivity, PlayerActivity.class);break;
            case Data.VpFragmentActivity: intent =new Intent(thisActivity, VpFragmentActivity.class);break;
            case Data.EditActivity:  {
                intent =new Intent(thisActivity, EditActivity.class);
                intent.putExtra("isFromPlaylist", false);
                intent.putExtra("whichPositionFromPlaylist", 0);
                intent.putExtra("isFromSearchActivity", true);
                break;
            }
            case Data.SettingActivity :{intent =new Intent(thisActivity, SettingActivity.class);break;}
            case Data.MainFragmentActivity :{intent =new Intent(thisActivity, MainFragmentActivity.class);break;}
            default:break;
        }
        if (getIntent().getSerializableExtra("objectList")!=null)
            intent.putExtra("objectList",getIntent().getSerializableExtra("objectList"));
        handler.removeCallbacksAndMessages(null);
        unbindService(sc);
        startActivity(intent);
        finish();
    }










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
                        initOnClick();
                        handler.removeCallbacksAndMessages(null);
                        msg=obtainMessage(Data.SearchActivity);
                        handler.sendMessageDelayed(msg,500);
                    }
                    break;
                }
                case Data.SearchActivity:{
                    infoUpdate(player, oriSongList);
                    msg=obtainMessage(Data.SearchActivity);
                    handler.sendMessageDelayed(msg,500); break;
                }
                default: break;
            }
        }
    };
}






















