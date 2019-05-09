package com.lc.musicplayer.MainFragment;

import android.content.Context;
import android.os.Bundle;
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

public class MainSingerListFg extends Fragment {
    private List<Song> oriSongList ;
    private List<SameStringIdList> singerList ;
    private ListAdapter_Fragment listAdapter_fragment;
    private View view;
    private ListView listView;
    private FragmentActivity mActivity;
    private List<Integer> singleList;
    private int testInt=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.singerlist_layout, container, false);
        initListView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.oriSongList = (List<Song>) getArguments().getSerializable("oriSongList");
            this.singerList = (List<SameStringIdList>) getArguments().getSerializable("singerList");
        }
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    public MainSingerListFg(){}
    public static MainSingerListFg newInstance(List<Song> oriSongList , List<SameStringIdList> singerList ) {
        MainSingerListFg mainSingerListFg = new MainSingerListFg();
        if (oriSongList==null)
            oriSongList= (List<Song>) Saver.readSongList("firstList");
        if (singerList==null)
            singerList = initDefaultsSingerList(oriSongList, singerList);
        Bundle args = new Bundle();
        args.putSerializable("oriSongList",(Serializable) oriSongList);
        args.putSerializable("singerList",(Serializable) singerList);
        mainSingerListFg.setArguments(args);
        return mainSingerListFg;
    }

    public static List<SameStringIdList> initDefaultsSingerList(List<Song> oriSongList,List<SameStringIdList> singerList ){
        if (singerList==null)
            singerList=Player.idToSameSingerConvert(oriSongList);
        return singerList;
    }

    private void initListView(){
        listView = view.findViewById(R.id.singerList);
        listAdapter_fragment=
                new ListAdapter_Fragment(MyApplication.getContext()
                        ,singerList, oriSongList,Data.SameSingerList, R.layout.same_string_item);
        listView.setAdapter(listAdapter_fragment);
    }
    private void initOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ViewPager viewPager = getActivity().findViewById(R.id.listViewPager);
//                mActivity.setSingleListPosition(position);
//                mActivity.setSingleList(  Player.sameStringListToList(singerList.get(position))  );
//                mActivity.setLastPage(Data.SamePlaylist);
//                viewPager.setCurrentItem(4,false);
            }
        });
    }
}