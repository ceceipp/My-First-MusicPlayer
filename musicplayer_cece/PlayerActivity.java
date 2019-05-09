package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView songName = null;
    private TextView songSinger = null;
    private TextView duration = null;
    private ImageView playerPic = null;
    private TextView songAlbum = null;
    private Button exitPlayer = null;
    public String[] songInfo = null;
    public SeekBar seekBar = null;
    private Button startOrPause = null;
    private Button nextSong = null;
    private Button preSong = null;
    private Button order = null;
    public  List<Song> songList;
    private Player player ;
    private int itemCount;
    private MusicService musicService;
    private MusicService.MyBinder myBinder;
    private final String TAG ="Ser1212";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        initIntent();
        initService();
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
        init();
    }
    @Override
    protected void onResume(){
        super.onResume();
        //if (player!=null)
        //这里不能设置msg直接infoUpdate, 因为有可能
        // 第一次打开时还没有获取player就来到这里运行onResume的
        //infoUpdate, 导致第一页总是打不开
        handler.sendEmptyMessage(Data.Player_Loading_Msg);
    }
    protected void onPause(){
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onStop(){
        super.onStop();
    }
    public void  onDestroy(){
        super.onDestroy();handler.removeCallbacksAndMessages(null);
        //player=null;
    }
    @Override
    public void onBackPressed(){
        intentPackToFragment();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exitPlayer:      {   intentPackThenChange();  break;                           }
            case R.id.startOrPause:{    player.startOrPause();  break;               }
            case R.id.nextSong:       {    player.nextSong();  break;                      }
            case R.id.preSong:         {    player.preSong();  break;                        }
            case R.id.order:             {    player.change_Order_Mode();  break;      }
            default: break;
        }
        infoUpdate();
        infoUpdateRealTime();
    }
    public void initOnclick(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    int mSec=0;
                    mSec = (int)((float)progress/200*songList.get(player.getUsingPositionId()).getDurationMsec());
                    player.seekTo(mSec);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String string=null;
                string = AudioUtils.formatTime(player.mediaPlayer.getCurrentPosition());
                Toast.makeText(PlayerActivity.this,string,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public  void  init(){
        //ArrayList<String> songInfoList = bundleUnZip();
        songName=findViewById(R.id.songName);
        songSinger=findViewById(R.id.songSinger);
        songAlbum=findViewById(R.id.songAlbum);
        duration=findViewById(R.id.duration);
        playerPic=findViewById(R.id.playerPic);
        exitPlayer =findViewById(R.id.exitPlayer);
        seekBar =findViewById(R.id.seekBar);
        startOrPause = findViewById(R.id.startOrPause);
        nextSong = findViewById(R.id.nextSong);
        preSong = findViewById(R.id.preSong);
        order =findViewById(R.id.order);
        duration.setOnClickListener(this);
        playerPic.setOnClickListener(this);
        exitPlayer.setOnClickListener(this);
        startOrPause.setOnClickListener(this);
        nextSong.setOnClickListener(this);
        preSong.setOnClickListener(this);
        order.setOnClickListener(this);
    }
    public void infoUpdate(){
        order.setText(Data.Order_Mode.get(player.order_Mode));
        startOrPause.setText(player.mediaPlayer.isPlaying()?"Pause":"Play");
        songName.setText(songList.get(player.getUsingPositionId()).getSong());
        songSinger.setText(songList.get(player.getUsingPositionId()).getSinger());
        //duration.setText(songList.get(player.getUsingPositionId()).getDuration());
        songAlbum.setText(songList.get(player.getUsingPositionId()).getAlbum());
        playerPic.setImageBitmap(Player.loadingCover(songList.get(player.getUsingPositionId()).getPath()));
    }
    public void initIntent(){
        itemCount=getIntent().getIntExtra("itemCount",0);
    }
    public void intentPackThenChange(){
        Intent intent =new Intent(PlayerActivity.this, MainActivity.class);
        intent.putExtra("itemCount", itemCount);
        unbindService(sc);
        Intent stopIntent=new Intent(this,MusicService.class);
        //stopService(stopIntent);
        startActivity(intent);
        finish();
    }
    public void intentPackToFragment(){
        Intent intent = new Intent(PlayerActivity.this,FragmentActivity.class);
        unbindService(sc);
        intent.putExtra("itemCount",itemCount);
        startActivity(intent);
        finish();
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            myBinder = (MusicService.MyBinder) iBinder;
            musicService = myBinder.getService();
            player=myBinder.getPlayer();
            songList=myBinder.getSongListFromService();
            infoUpdate();
            infoUpdateRealTime();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    public void initService() {
        Intent startIntent = new Intent(PlayerActivity.this, MusicService.class);
        Intent bindIntent = new Intent(PlayerActivity.this,MusicService.class);
        bindService(bindIntent,sc,BIND_AUTO_CREATE);
    }
    public ArrayList<String> bundleUnZip(){
        songInfo = getIntent().getExtras().getStringArray("SongInfo");
        ArrayList<String> songInfoList = new ArrayList();
        for (int  i=0 ; i <songInfo.length;i++)
            songInfoList.add(songInfo[i]);
        return songInfoList;
    }
    //Handler 的逻辑是先等待BlindService方法运行后,
    // 且等待onServiceConnection回调的完成了musicService = myBinder.getService();
    // 以及player=myBinder.getPlayer();后, 获取了这个activity的player, player不是null了
    //就清空msg队列, 然后加入initOnClick()与infoUpdate()的方法, 为什么不在onCreate
    // 加入initOnClick(), 是因为那个时候这个activity的player还是null,
    // 而initOnClick()中是根据player来设置的,
    // 所以必须等到player绑定到musicService的player(现在修改成private)后才能允许设置
    //否则会出错

    Handler handler = new Handler() {
        @Override
        public  void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case Data.Player_Loading_Msg:{
                    if (player==null){
                        msg=obtainMessage(Data.Player_Loading_Msg);
                        handler.sendMessageDelayed(msg,500);
                    }
                    else {
                        handler.removeCallbacksAndMessages(null);
                        initOnclick();
                        msg=obtainMessage(Data.PlayerActivityInfoUpdate);
                        handler.sendMessageDelayed(msg,300);
                    }
                    break;
                }
                case Data.PlayerActivityInfoUpdate:{
                    infoUpdateRealTime();
                    if (player.musicInfoNeedUpdate) {
                        infoUpdate();
                        player.musicInfoNeedUpdate=false;
                    }
                    msg=obtainMessage(Data.PlayerActivityInfoUpdate);
                    handler.sendMessageDelayed(msg,500);
                    break;
                }
                default: break;
            }
        }
    };

    public void infoUpdateRealTime(){
        seekBar.setProgress(player.seekBarPercentage());
        duration.setText(
                AudioUtils.formatTime(player.mediaPlayer.getCurrentPosition())+
                        "\n"+songList.get(player.getUsingPositionId()).getDuration());
    }
}

