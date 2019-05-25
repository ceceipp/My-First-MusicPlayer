package com.lc.musicplayer.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.PlayerActivity;
import com.lc.musicplayer.R;

import java.util.List;

public class EzForCheck {
    //    PlayerActivity
    {
    }

    //    private String getResourcesUri(int resId) {
//        Resources resources = MyApplication.getContext().getResources();
//        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
//                resources.getResourcePackageName(resId) + "/" +
//                resources.getResourceTypeName(resId) + "/" +
//                resources.getResourceEntryName(resId);
//        return uriPath;
//    }
    {
    }
//以下方法在滑动时不会中断播放, 借鉴了网上的一些方法

    //seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            if(fromUser){
//                int mSec=0;
//                mSec = (int)((float)progress/200*songList.get(player.getUsingPositionId()).getDurationMsec());
//                //player.seekTo(mSec);
//                handler.removeMessages(updateProgress);
//                handler.removeCallbacks(lastSeekBarRunnable);
//                final  int mmSec=mSec;
//                lastSeekBarRunnable = new Runnable(){
//                    @Override
//                    public void run(){
//                        player.seekTo(mmSec);
//                        Log.d("Ser1212","Done");
//                    }
//                };
//                handler.postDelayed(lastSeekBarRunnable, 300);
//            }
//        }
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            handler.removeMessages(updateProgress);
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            String string=null;
//            //string = AudioUtils.formatTime(player.mediaPlayer.getCurrentPosition());
//            string = AudioUtils.formatTime
//                    ((int)((float)seekBar.getProgress()/200*
//                            songList.get(player.getUsingPositionId()).getDurationMsec()));
//            Toast.makeText(PlayerActivity.this,string,Toast.LENGTH_SHORT).show();
//            handler.sendEmptyMessageDelayed(updateProgress,500);
//
//        }
//    });
    {
    }//MainActivity

    //        {
// listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                clickSongPosition = position;
//                itemIsClicked = true;
//                Toast.makeText(MainActivity.this, "Click"+position, Toast.LENGTH_SHORT).show();
//            }
//        });
//  }
//
//    private void test(){
//        play(song_list.get(clickSongPosition).getPath());
//        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
//        Bundle bundle = new Bundle();
//        String[] songInfo =  {
//                song_list.get(clickSongPosition).getSong(),
//                song_list.get(clickSongPosition).getSinger(),
//                song_list.get(clickSongPosition).getDuration(),
//                song_list.get(clickSongPosition).getPath(),
//        };
//        bundle.putStringArray("SongInfo", songInfo);
//        intent.putExtras(bundle);
//        startActivity(intent);
//        finish();
//    }
//
//               Bundle bundle = new Bundle();
//               String[] songInfo =  {
//                       song_list_final.get(position).getSong(),
//                       song_list_final.get(position).getSinger(),
//                       song_list_final.get(position).getDuration(),
//                       song_list_final.get(position).getPath(),
//               };
////
////
////               for (int i=0; i < 2000; i++){
////                   bundle.putSerializable(song_list_final.get(i).getSong(), song_list_final.get(i));
////               }
//               //这里的我想用S序列化打包Song对象, 但是貌似超出缓冲区了, 所以失败
////               for (int i=0;i < 1360;i++){
////                  intent.putExtra(" "+i, song_list_final.get(i));
////               }
////               intent.putExtra(" "+1359,song_list_final.get(1366));
//               bundle.putStringArray("SongInfo", songInfo);
//               intent.putExtras(bundle);
//              startActivity(intent);
//               finish();
//
//    private void play(String path) {
//        //播放之前要先把音频文件重置
//        try {
//            mediaPlayer.reset();
//            //调用方法传进去要播放的音频路径
//            mediaPlayer.setDataSource(path);
//            //异步准备音频资源
//            mediaPlayer.prepareAsync();
//            //调用mediaPlayer的监听方法，音频准备完毕会响应此方法
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();//开始音频
//                }
//            });
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
    {
    }//AudioUtils
//song.setSong( cursor.getSameString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
//                song.setSinger( cursor.getSameString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
//                song.setPath(cursor.getSameString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
//                song.setDuration(formatTime(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))));
//                song.setFileSize( cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));//
{}//TestActivity
//    package com.lc.musicplayer.test;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.SeekBar;
//
//import com.lc.musicplayer.R;
//
//public  class TestActivity extends AppCompatActivity {
//    private Button btnStartService;
//    private Button btnStopService;
//    private Button btnBindService;
//    private Button btnUnbindService;
//    private SeekBar seekBar;
//    private TestService.MyBinder myBinder;
//    public int seekBarInt=0;
//    public static final int updateProgress =2 ;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.testactivity);
//        btnStartService=findViewById(R.id.btnStartService);
//        btnStopService=findViewById(R.id.btnStopService);
//        btnBindService =findViewById(R.id.btnBindService);
//        btnUnbindService =findViewById(R.id.btnUnbindService);
//        seekBar=findViewById(R.id.seekBar);
//
//        Log.d("Ser1212", "Activity thread id is " + Thread.currentThread().getId());
//
//
//        btnStartService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent startIntent = new Intent(TestActivity.this, TestService.class);
//                startService(startIntent);
//                //testOnclick();
//
//            }
//        });
//        btnStopService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent stopIntent = new Intent(TestActivity.this, TestService.class);
//                stopService(stopIntent);
//            }
//        });
//        btnBindService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("Ser1212", "BindService");
//                Intent bindIntent = new Intent(TestActivity.this,TestService.class);
//                bindService(bindIntent, connection, BIND_AUTO_CREATE);
//            }
//        });
//
//
//        btnUnbindService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("Ser1212", "unBindService");
//                unbindService(connection);
//            }
//        });
//
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                handler.removeMessages(updateProgress);
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                handler.sendEmptyMessageDelayed(updateProgress, 500);
//            }
//        });
//    }
//    private ServiceConnection connection= new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            //可以在Activity中根据具体的场景来调用MyBinder中的任何public方法，
//            // 即实现了Activity指挥Service干什么Service就去干什么的功能。
//            myBinder = (TestService.MyBinder) service;
//            myBinder.startDownload();
//            int ii=0;
//            ii=myBinder.getIi();
//            Log.d("Ser1212","activity:ii="+ii);
//            seekBar.setProgress(ii*20);
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            Log.d("Ser1212", "DicConnect");
//        }
//    };
//
//    public void testOnclick( ){
//        seekBarInt++;
//        new Thread(new Runnable(){
//            @Override
//            public void run(){
//                try{
//                    Thread.sleep(1000);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//                Message message = Message.obtain();
//                message.arg1 = seekBarInt;
//                message.what = 1;
//            }
//        }).start();
//    }
//    public void doSendMsg(){
//        try{
//            Thread.sleep(1000);
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
//        Message message = Message.obtain();
//        message.arg1 = seekBarInt;
//        message.what = 1;
//    }
//    Handler handler = new Handler(){
//        @Override
//        public  void handleMessage(Message msg){
//            super.handleMessage(msg);
//            if (msg.what==updateProgress){
//                msg=obtainMessage(updateProgress);
//                handler.sendMessageDelayed(msg,200);
//                seekBar.setProgress((seekBarInt++));
//            }
//        }
//    };
//
//}

{}//TestService
//package com.lc.musicplayer.test;
//
//import android.app.Activity;
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Binder;
//import android.os.IBinder;
//import android.os.Message;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.lc.musicplayer.MyApplication;
//import com.lc.musicplayer.R;
//import com.lc.musicplayer.tools.AudioUtils;
//import com.lc.musicplayer.tools.Player;
//import com.lc.musicplayer.tools.Song;
//
//import java.io.IOException;
//import java.util.List;
//
//public class TestService extends Service
//{
//    public SeekBar seekBar;
//    public List<Song> songList = AudioUtils.getSongs(MyApplication.getContext());
//    public Player player=new Player(songList);
//    private MyBinder myBinder = new MyBinder();
//    public Thread thread = null;
//    public Thread thread2 =null;
//    private boolean isStop=false;
//    public static  int ii;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Notification notification = new Notification(R.drawable.loading_03,
//                "有通知到来", System.currentTimeMillis());
//        Intent notificationIntent = new Intent(this, TestService.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//        //notification.s(this, "这是通知的标题", "这是通知的内容", pendingIntent);
//        startForeground(1, notification);
//        Log.d("Ser1212", "before");
//    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        thread2=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread2.start();
//        return super.onStartCommand(intent, flags, startId);
//    }
//    @Override
//    public void onDestroy() {
//        isStop =true;
//        super.onDestroy();Log.d("Ser1212","onDestroy");player.mediaPlayer.release();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return myBinder;
//    }
//    class MyBinder extends Binder{
//
//        public int getIi(){
//            return ii;
//        }
//        public void startDownload() {
//            Log.d("Ser1212", "Thread run first");
//            thread=new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    player.setUsingPositionId(7);
//                }
//            });
//            thread.start();
//            // 执行具体的下载任务
//        }
//        public int getProgress(){
//            Log.d("Ser1212", "getProgress() executed");
//            return 0;
//        }
//    }
//}
//
//
////{
////        停止一个started服务有两种方法：
////
////        （1）在外部使用stopService()
////
////        （2）在服务内部(onStartCommand方法内部)使用stopSelf()方法。
//// startId代表启动服务的次数,每次都会自动递增
////
{}//MusicListFragment
//    package com.lc.musicplayer.fragment;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.ContactsContract;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.SearchView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.lc.musicplayer.MainActivity;
//import com.lc.musicplayer.MyApplication;
//import com.lc.musicplayer.R;
//import com.lc.musicplayer.service.MusicService;
//import com.lc.musicplayer.test.TestActivity;
//import com.lc.musicplayer.tools.AudioUtils;
//import com.lc.musicplayer.tools.Data;
//import com.lc.musicplayer.tools.Listview_Adapter;
//import com.lc.musicplayer.tools.Listview_Adapter_Fragment;
//import com.lc.musicplayer.tools.Player;
//import com.lc.musicplayer.tools.Song;
//
//import java.util.List;
//
//    public class MusicListFragment extends Fragment {
//        private ListView listView;
//        private List<Song> songList = AudioUtils.getSongs(MyApplication.getContext());
//        private Listview_Adapter_Fragment listview_Adapter;
//        private Activity mActivity;
//        public int test=0;
//        public SearchView searchView;
//
//        private Player player;
//        @Override
//        public void onAttach(Context context){
//            super.onAttach(context);
//            mActivity = ( Activity ) context;
//            //((TestActivity)mActivity).tapThenPlaySong(songList.size()-2);
//        }
//        public static com.lc.musicplayer.fragment.MusicListFragment newInstance() {
//            com.lc.musicplayer.fragment.MusicListFragment mf = new com.lc.musicplayer.fragment.MusicListFragment();
//            return mf;
//        }
//
//        @Nullable
//        @Override
//        public View onCreateView(LayoutInflater inflater , @Nullable ViewGroup container, Bundle savedInstanceState){
//        View view= inflater.inflate(R.layout.music_list_in_fragment, container,false);
//        listView=view.findViewById(R.id.list_view2);
//        listItemDataPut();
//        handler.removeCallbacksAndMessages(null);
//        handler.sendEmptyMessageDelayed(Data.Player_Loading_Msg,200);
//        MusicService musicService =new MusicService();
//        player=musicService.myBinder.getPlayer();
//        Toast.makeText(MyApplication.getContext(),
//                "Player in Fragment! "+player,Toast.LENGTH_SHORT).show();
//        player.findPath();
//        initOnClick();
//            //Log.d("Ser1212","fragment : "+musicService) ;
//            //View view= inflater.inflate(R.layout.fragment_viewpager_layout, container,false);
//
//
//            return view;
//
//        }
//
//        private void listItemDataPut(){
//            songList = AudioUtils.getSongs(MyApplication.getContext());
//            listview_Adapter =new Listview_Adapter_Fragment(mActivity, songList,R.layout.fragment_list_item);
//            listView.setAdapter(listview_Adapter);
//        }
//        public ListView getListView(){
//            return this.listView;
//        }
//        public Listview_Adapter_Fragment getListview_Adapter(){
//            return this.listview_Adapter;
//        }
//        public void setSongList(List<Song> newSongList){
//            this.songList = newSongList;
//        }
//        public List<Song> getSongListFromService(){
//            return this.songList;
//        }
//        public Activity getMainActivity() {
//            return mActivity;
//        }
//        public void initOnClick() {
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(MyApplication.getContext(), "List1" + position, Toast.LENGTH_SHORT).show();
//                    player.firstClickListItem(position);
//                }
//            });
//        }
//        Handler handler = new Handler() {
//            @Override
//            public  void handleMessage(Message msg){
//                super.handleMessage(msg);
//                switch (msg.what){
//                    case Data.Player_Loading_Msg:{
//                        if (player==null){
//                            msg=obtainMessage(Data.Player_Loading_Msg);
//                            handler.sendMessageDelayed(msg,500);
//                        }
//                        else {
//                            handler.removeCallbacksAndMessages(null);
////                        Toast.makeText(MyApplication.getContext(),
////                                "Player ready!",Toast.LENGTH_SHORT).show();
//                            //Log.d("Ser1212", "after player not null");
//                        }
//                        break;
//                    }
//                    case Data.MainActivityInfoUpdate:
//                    case Data.PlayerActivityInfoUpdate:
//                    default: break;
//                }
//            }
//        };
//    }


}
