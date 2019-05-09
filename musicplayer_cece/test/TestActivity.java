package com.lc.musicplayer.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public  class TestActivity extends AppCompatActivity {

    //private List<Song> songList = AudioUtils.getSongs(MyApplication.getContext());
    private List<Song> songList;
    public int usingPosition;
    public MusicService.MyBinder myBinder;
    public MusicService musicService;
    private Player player;
    private TextView songName = null;
    private TextView songSinger = null;
    private TextView duration = null;
    private ImageView playerPic = null;
    private Button startOrPause = null;
    private Button nextSong = null;
    private Button order = null;
    private Bitmap bitmap;
    private long time11, time22, time33, time44, time2_1, time4_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
/**
 * @ url 是文件名, 你也可以用ID来标记他.
 * **/
