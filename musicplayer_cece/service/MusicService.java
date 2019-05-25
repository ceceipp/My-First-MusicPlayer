package com.lc.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;

public  class  MusicService extends Service {
    private final static String TAG ="Ser1212";
    public static List<Song> songList;
            //=(List<Song>) Saver.readSongList("firstList");
    private static   Player player;
            //= new Player(songList);
    public MyBinder myBinder = new MyBinder();
    public int clickSongPosition;

    @Override
    public void  onCreate(){
        //songList = Player.initSongList("firstList");
        //player = new Player();
        //Player.initAlbumPicCache(songList);
        super.onCreate();

//        player.setUsingPositionId(player.getUsingPositionList().size()-1);
//        player.stop();
        //notificationBuildUp();
    }

    //服务执行的操作
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //Log.d("Ser1212","MS : "+this) ;
        return  super.onStartCommand(intent, flags, startId);
    }

    //销毁服务时调用
    @Override
    public void  onDestroy(){
        super.onDestroy();
        List<Object> lastSavedObject =new ArrayList<>();
        //lastSavedObject.add(songList);
        if (player!=null&&player.getUsingPositionList()!=null){
            lastSavedObject.add(player.getUsingPositionList());
            lastSavedObject.add(player.getUsingPositionId());
            lastSavedObject.add(player.getCurrentMSec());
        }
        Saver.saveData("lastSavedObject",lastSavedObject, false);
        player.mediaPlayer.stop();
        player.mediaPlayer.release();
        player=null;
    }
    @Override
    public  IBinder onBind(Intent intent){
        return myBinder;
    }

    public class MyBinder extends  Binder{
        public  MusicService getService(){
            return MusicService.this;
        }
        public Player getPlayer(){
            return player;
        }
        public List<Song> getSongListFromService(){return songList; }
        public void setServiceSongListFromActivity(List<Song> songListFromActivity){songList=songListFromActivity;}
        public void newInstancePlayer(List<Song> songListFromActivity){
            if (player==null){
                player= new Player(songListFromActivity);
                List<Object> lastSavedObject = (List<Object>) Saver.readData("lastSavedObject");
                if (lastSavedObject!=null){
                    player.setUsingPositionList((List<Integer>) lastSavedObject.get(0));
                    if ( lastSavedObject.get(1)!=null)
                        player.firstClickListItem((int) lastSavedObject.get(1));
                    player.stop();
                    player.seekTo((int)lastSavedObject.get(2));
                }
                else{
                    player.firstClickListItem(player.getUsingPositionList().get(0));
                    player.stop();
                }
            }
        }
        public void initAlbumPicWithAlbumIdAtBackground( ){
            if (songList!=null&&!songList.isEmpty())
                Player.initAlbumPicBackground(songList);
        }
    }

    public void notificationBuildUp(){
        NotificationManager notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);

        /**
         *  实例化通知栏构造器
         */

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        /**
         *  设置Builder
         */
        //设置标题
        mBuilder.setContentTitle(songList.get(player.getUsingPositionId()).getSong())
                //设置内容
                .setContentText(songList.get(player.getUsingPositionId()).getSinger())
                //设置大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.loading_03))
                //设置小图标
                .setSmallIcon(R.drawable.loading_03)
                //设置通知时间
                .setWhen(System.currentTimeMillis())
                //首次进入时显示效果
                .setTicker(songList.get(player.getUsingPositionId()).getPath()+songList.get(player.getUsingPositionId()).getSong()+songList.get(player.getUsingPositionId()).getDuration())
                //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                .setDefaults(Notification.DEFAULT_LIGHTS);
        //发送通知请求发布要在状态栏中显示的通知。
        // 如果您的应用程序已经发布了具有相同ID的通知但尚未取消，
        // 则它将被更新的信息替换。
        // @param id此应用程序中唯一的标识符。
        // @param notification一个{@link Notification}对象，
        // 描述向用户显示的内容。 不能为空。
        notificationManager.notify(10 ,mBuilder.build());
    }

}
