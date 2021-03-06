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

import com.lc.musicplayer.FragmentActivity;
import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.ListAdapter_Fragment;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.io.Serializable;
import java.util.List;

public class MainAlbumListFg extends Fragment {
    private List<Song> oriSongList ;
    private List<SameStringIdList> albumList ;
    private ListAdapter_Fragment listAdapter_fragment;
    private View view;
    private ListView listView;
    private FragmentActivity mActivity;
    private List<Integer> singleList;
    private int testInt=0;
    private FgSendDataToAct myFg;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myFg = (FgSendDataToAct)context;
        oriSongList = (List<Song>) Saver.readSongList("firstList");
        albumList =(List<SameStringIdList>) Saver.readData("albumList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.albumlist_layout, container, false);
        initListView();
        initOnClick();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            this.oriSongList = (List<Song>) getArguments().getSerializable("oriSongList");
//            this.albumList = (List<SameStringIdList>) getArguments().getSerializable("albumList");
//        }
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

    public MainAlbumListFg(){}
    public static MainAlbumListFg newInstance( ) {
        MainAlbumListFg mainAlbumListFg = new MainAlbumListFg();
//        if (oriSongList==null)
//            oriSongList= (List<Song>) Saver.readSongList("firstList");
//        if (albumList==null)
//            albumList = initDefaultAlbumList(oriSongList, albumList);
//        Bundle args = new Bundle();
//        args.putSerializable("oriSongList",(Serializable) oriSongList);
//        args.putSerializable("albumList",(Serializable) albumList);
//        mainAlbumListFg.setArguments(args);
        return mainAlbumListFg;
    }

    private void initListView(){
        listView = view.findViewById(R.id.albumList);
        listAdapter_fragment=
                new ListAdapter_Fragment(MyApplication.getContext()
                        ,albumList, oriSongList,Data.SameAlbumList, R.layout.same_string_item);
        listView.setAdapter(listAdapter_fragment);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void initOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                singleList = Player.sameStringListToList(albumList.get(position));
                myFg.sendSameString("专辑: "+albumList.get(position).getString(), singleList.size());
                myFg.sendSingleList(singleList);
                //( (MainFragmentActivity) getActivity()).showSingleListFg(singleList);
            }
        });
    }
}