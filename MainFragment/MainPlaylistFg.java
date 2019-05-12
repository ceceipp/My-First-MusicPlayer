package com.lc.musicplayer.MainFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.ListAdapter_Fragment;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainPlaylistFg extends Fragment{
    private List<Song> oriSongList ;
    private List<SameStringIdList> playlist ;
    private ListAdapter_Fragment listAdapter_fragment;
    private View view;
    private ListView listView;
    private List<Integer> singleList;
    private int testInt=0;
    private FgSendDataToAct myFg;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myFg = (FgSendDataToAct)context;
        oriSongList = (List<Song>) Saver.readSongList("firstList");
        playlist =(List<SameStringIdList>) Saver.readData("playlist");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.playlist_layout, container, false);
        initListView();
        initOnClick();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public MainPlaylistFg(){}
    public static MainPlaylistFg newInstance() {
        MainPlaylistFg mpFg = new MainPlaylistFg();
//        if (oriSongList==null)
//            oriSongList= (List<Song>) Saver.readSongList("firstList");
//        if (playlist==null)
//            playlist = initDefaultPlaylist(oriSongList, playlist);
//        Bundle args = new Bundle();
//        args.putSerializable("oriSongList",(Serializable) oriSongList);
//        args.putSerializable("playlist",(Serializable) playlist);
//        mpFg.setArguments(args);
        return mpFg;
    }

    private void initListView(){
        listView = view.findViewById(R.id.playlist);
        listAdapter_fragment=
                new ListAdapter_Fragment(MyApplication.getContext()
                        ,playlist, oriSongList,Data.SamePlaylist, R.layout.same_string_item);
        listView.setAdapter(listAdapter_fragment);
    }
    private void initOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                singleList = Player.sameStringListToList(playlist.get(position));
                myFg.sendSameString("歌单: "+playlist.get(position).getString(),singleList.size());
                myFg.sendSingleList(singleList);
                //( (MainFragmentActivity) getActivity()).showSingleListFg(singleList);
            }
        });
    }
}



