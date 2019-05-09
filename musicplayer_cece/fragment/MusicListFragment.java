package com.lc.musicplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.FragmentActivity;
import com.lc.musicplayer.MainActivity;
import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.test.TestActivity;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Listview_Adapter;
import com.lc.musicplayer.tools.Listview_Adapter_Fragment;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Song;
import com.lc.musicplayer.tools.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MusicListFragment extends Fragment {
    private ListView listView;
    private List<Song> songList;
    private Listview_Adapter_Fragment listview_Adapter;
    private FragmentActivity mActivity;
    public  Player player;

    public ViewPager viewPager ;
    public ViewPagerAdapter viewPagerAdapter;
    public PagerTabStrip pagerTabStrip;
    public List<View> pageLists = new ArrayList<>();
    public List<String> titleLists = new ArrayList<>();

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
        songList=mActivity.getOriSongList();
    }
    public static MusicListFragment newInstance() {
        MusicListFragment mf = new MusicListFragment();
        return mf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater , @Nullable ViewGroup container, Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.music_list_in_fragment, container,false);
        listView=view.findViewById(R.id.list_view2);
        listItemDataPut();
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(Data.Player_Loading_Msg,200);
        MusicService musicService =new MusicService();
        player=musicService.myBinder.getPlayer();
        //player.findPath();
        initOnClick();

        return view;

    }

    private void listItemDataPut(){
        songList = mActivity.getOriSongList();
        listview_Adapter =new Listview_Adapter_Fragment(mActivity, songList,R.layout.fragment_list_item);
        listView.setAdapter(listview_Adapter);
    }
    public ListView getListView(){
        return this.listView;
    }
    public Listview_Adapter_Fragment getListview_Adapter(){
        return this.listview_Adapter;
    }
    public void setSongList(List<Song> newSongList){
        this.songList = newSongList;
    }
    public List<Song> getSongList(){
        return this.songList;
    }
    public Activity getMainActivity() {
        return mActivity;
    }
    public void initOnClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MyApplication.getContext(), "List1" + position, Toast.LENGTH_SHORT).show();
                player.firstClickListItem(position);
            }
        });
    }
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
//                        Toast.makeText(MyApplication.getContext(),
//                                "Player ready!",Toast.LENGTH_SHORT).show();
                        //Log.d("Ser1212", "after player not null");
                    }
                    break;
                }
                case Data.MainActivityInfoUpdate:
                case Data.PlayerActivityInfoUpdate:
                default: break;
            }
        }
    };
}

