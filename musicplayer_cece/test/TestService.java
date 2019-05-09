package com.lc.musicplayer.test;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;

import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Song;

import java.util.List;

public class TestService extends Service
{
    public SeekBar seekBar;
    public List<Song> songList;
           // = AudioUtils.getSongs(MyApplication.getContext());
    public Player player=new Player(songList);
    private MyBinder myBinder = new MyBinder();
    public Thread thread = null;
    public Thread thread2 =null;
    private boolean isStop=false;
    public static  int ii;

    @Override
    public void onCreate() {
        super.onCreate();
        Notification notification = new Notification(R.drawable.loading_03,
                "有通知到来", System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, TestService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        //notification.s(this, "这是通知的标题", "这是通知的内容", pendingIntent);
        startForeground(1, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread2=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread2.start();
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
        isStop =true;
        super.onDestroy();
        player.mediaPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }
    class MyBinder extends Binder{

        public int getIi(){
            return ii;
        }
        public void startDownload() {
            thread=new Thread(new Runnable() {
                @Override
                public void run() {
                }
            });
            thread.start();
            // 执行具体的下载任务
        }
        public int getProgress(){
            return 0;
        }
    }
}


//{
//        停止一个started服务有两种方法：
//
//        （1）在外部使用stopService()
//
//        （2）在服务内部(onStartCommand方法内部)使用stopSelf()方法。
// startId代表启动服务的次数,每次都会自动递增
//        }