package com.lc.musicplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private final String TAG = "Ser1212";
    private MusicService.MyBinder myBinder;
    private Button btnInitAlbumPicWithAlbumIdBackground, btnInitAlbumPicWithAlbumId, btnExit, btnToVpFragmentActivity;
    private SeekBar seekBarProgress;
    private TextView seekBarProgressText;
    private int iCount;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        initService();
        initViewFindId();
        initOnClick();
    }

    @Override
    public void onBackPressed() {
        intentPackThenChange(Data.MainFragmentActivity);
    }
    private void initViewFindId(){
        btnInitAlbumPicWithAlbumIdBackground =findViewById(R.id.btnInitAlbumPicWithAlbumIdBackground);
        btnInitAlbumPicWithAlbumId = findViewById(R.id.btnInitAlbumPicWithAlbumId);
        btnExit=findViewById(R.id.btnExit);
        btnToVpFragmentActivity = findViewById(R.id.btnToVpFragmentActivity);
        seekBarProgress=findViewById(R.id.seekBarProgress);
        seekBarProgressText = findViewById(R.id.seekBarProgressText);
    }
    private void initOnClick(){
        btnInitAlbumPicWithAlbumIdBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBinder!=null)
                    myBinder.initAlbumPicWithAlbumIdAtBackground();
            }
        });
        btnInitAlbumPicWithAlbumId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(10);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Song> songList = (List<Song>) Saver.readSongList("firstList");
                        Bitmap bitmap ;
                        byte[] picture;
                        MediaMetadataRetriever mediaMetadataRetriever =new MediaMetadataRetriever();
                        for (int i=0;i<songList.size();i++){
                            mediaMetadataRetriever.setDataSource(songList.get(i).getPath());
                            iCount = i;
                            if (mediaMetadataRetriever.getEmbeddedPicture()!=null) {
                                songList.get(i).setAlbumPicExist(true);
//                                picture = mediaMetadataRetriever.getEmbeddedPicture();
//                                bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
//                                bitmap = Player.bitmapTo128N(bitmap);
//                                Saver.setLocalCachePath(" "+songList.get(i).getAlbum_Picture_Id(), bitmap, 100);
                            }
                            else{
                                songList.get(i).setAlbumPicExist(false);
                            }
                            Saver.exchangeSongList("firstList", songList);
                        }
                        handler.removeCallbacksAndMessages(null);
                    }
                }).start();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                intentPackThenExit();
            }
        });
        btnToVpFragmentActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPackThenChange(Data.VpFragmentActivity);
            }
        });
    }




Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 10:{
                seekBarProgress.setProgress((int)((float)(iCount*256)/2238));
                seekBarProgressText.setText(" "+iCount+"/"+2238);
                handler.sendEmptyMessageDelayed(10, 300);
            }
        }
    }
};

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            myBinder = (MusicService.MyBinder) iBinder;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {}
    };
    public void initService() {
        Intent startIntent = new Intent(this, MusicService.class);
        Intent bindIntent = new Intent(this,MusicService.class);
        bindService(bindIntent,sc,BIND_AUTO_CREATE);
        startService(startIntent);
    }
    public void intentPackThenExit(){
        Intent stopIntent =new Intent(this, MusicService.class);
        unbindService(sc);
        stopService(stopIntent);
        finish();
    }
    public void intentPackThenChange(int whichActivity){
        Intent intent=null;
        switch (whichActivity){
            case Data.MainActivity:  intent =new Intent(this, MainActivity.class);break;
            case Data.PlayerActivity:  intent =new Intent(this, PlayerActivity.class);break;
            case Data.VpFragmentActivity: intent =new Intent(this, VpFragmentActivity.class);break;
            case Data.EditActivity:   intent =new Intent(this, EditActivity.class);break;
            case Data.MainFragmentActivity:intent = new Intent(this,MainFragmentActivity.class);break;
            default:break;
        }
        unbindService(sc);
        if (getIntent().getSerializableExtra("objectList")!=null)
            intent.putExtra("objectList",getIntent().getSerializableExtra("objectList"));
        startActivity(intent);
        finish();
    }
}
