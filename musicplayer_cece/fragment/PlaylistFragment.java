package com.lc.musicplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lc.musicplayer.FragmentActivity;
import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;
import com.lc.musicplayer.service.MusicService;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.ListAdapter_Fragment;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * 每次刷到这个fragment之前都会在后台先缓存, 所以有个大坑
 * 那就是每次都要完整跑一遍这个fragment, 导致你如果在声明的地方
 * new一个playlists, 那么下次进来还是用回原来的list导致错误;
 * **/

public class PlaylistFragment  extends Fragment {
    private List<Song> oriSongList ;
    private List<SameStringIdList> playlists ;
    private ListAdapter_Fragment listAdapter_fragment;

    private View view;
    private ListView listView;
    private FragmentActivity mActivity;
    private List<Integer> singleList;
    private int testInt=0;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
        oriSongList=mActivity.getOriSongList();
        Log.d("Ser1212","onAttach");
//        if (oriSongList==null)
//            oriSongList=(List<Song>) Saver.readSongList("firstList");
    }

    public int getSameStringCount(){return playlists.size();}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        oriSongList=mActivity.getOriSongList();
        if (oriSongList==null)
            oriSongList=(List<Song>) Saver.readSongList("firstList");
        initData();
        view = inflater.inflate(R.layout.playlist_layout, container, false);
        initFragment();
        initOnClick();
        //这里为什么不能用呢, 转屏的时候, onCreateView比 fgAct的onCreate更早执行(或者同步),
        // 而fgAct的fragment配置是放在handler里面的...
        mActivity.setTitleLists(Data.SamePlaylist, "Playlist:  "+playlists.size());
        Log.d("Ser1212","onCreateView");

        return view;
    }

    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public static PlaylistFragment newInstance() {
        PlaylistFragment pf = new PlaylistFragment();
        return pf;
    }
    private void initData(){
        playlists=new ArrayList<>();
        //其实下面这句用法不够好, 因为这句应该用在onAttach中 , 这样用其实算内存泄露....
        //只不过误打误撞中了
        //oriSongList=mActivity.getOriSongList(0);
        playlists.add(new SameStringIdList("Pendulum"));
        playlists.add(new SameStringIdList("Nightwish"));
        playlists.add(new SameStringIdList("The Ting Tings"));
        playlists.add(new SameStringIdList("Ellie Goulding"));
        playlists.add(new SameStringIdList("All Item"));
        for (int i = 0;i<oriSongList.size();i++) {
            if (oriSongList.get(i).getSinger().contains("Pendulum"))
                playlists.get(0).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("Nightwish"))
                playlists.get(1).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("The Ting Tings"))
                playlists.get(2).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("Ellie Goulding"))
                playlists.get(3).getList().add(i);
            playlists.get(4).getList().add(i);
        }
    }
    private void initFragment(){
        listView = view.findViewById(R.id.playlist);
        listAdapter_fragment=
                new ListAdapter_Fragment(MyApplication.getContext()
                        ,playlists, oriSongList,Data.SamePlaylist, R.layout.same_string_item);
        listView.setAdapter(listAdapter_fragment);
    }
    private void initOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewPager viewPager = getActivity().findViewById(R.id.listViewPager);
                mActivity.setSingleListPosition(position);
                mActivity.setSingleList(  Player.sameStringListToList(playlists.get(position))  );
                mActivity.setLastPage(Data.SamePlaylist);
                viewPager.setCurrentItem(4,false);
            }
        });
    }
}
