package com.lc.musicplayer.MainFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.lc.musicplayer.tools.SameStringSongsFragment_Adapter;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.io.Serializable;
import java.util.List;

public class MainSameStringItemFg extends Fragment {
    private List<Song> oriSongList ;
    private List<SameStringIdList> sameStringList ;
    private List<Integer> singleList;
    private SameStringSongsFragment_Adapter sAdapter;

    private View view;
    private ListView listView;
    private FragmentActivity mActivity;
    private int testInt=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.samestringsongs_layout, container, false);
        initListView();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.oriSongList = (List<Song>) getArguments().getSerializable("oriSongList");
            this.singleList = (List<Integer>) getArguments().getSerializable("singleList");
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

    public MainSameStringItemFg(){}
    public static MainSameStringItemFg newInstance(List<Song> oriSongList , List<Integer> singleList ) {
        MainSameStringItemFg mainSameStringItemFg = new MainSameStringItemFg();
        List<SameStringIdList> sameStringList = initDefaultAlbumList(oriSongList,null);
        if (oriSongList==null)
            oriSongList= (List<Song>) Saver.readSongList("firstList");
        if (singleList==null)
            singleList =Player.sameStringListToList(sameStringList.get(0));
        Bundle args = new Bundle();
        args.putSerializable("oriSongList",(Serializable) oriSongList);
        args.putSerializable("singleList",(Serializable) singleList);
        mainSameStringItemFg.setArguments(args);
        return mainSameStringItemFg;
    }

    public static List<SameStringIdList> initDefaultAlbumList(List<Song> oriSongList,List<SameStringIdList> sameStringList ){
        if (sameStringList==null)
            sameStringList=Player.idToSameAlbumConvert(oriSongList);
        return sameStringList;
    }

    private void initListView(){
        listView = view.findViewById(R.id.sameStringSongs);
        singleList =Player.sameStringListToList(Player.idToSameAlbumConvert(oriSongList),0);
        sAdapter= new SameStringSongsFragment_Adapter(MyApplication.getContext()
                        ,singleList, oriSongList,Data.SameStringSingleList, R.layout.samestringsongs_item_layout);
        listView.setAdapter(sAdapter);
    }
    private void initOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ViewPager viewPager = getActivity().findViewById(R.id.listViewPager);
//                mActivity.setSingleListPosition(position);
//                mActivity.setSingleList(  Player.sameStringListToList(sameStringList.get(position))  );
//                mActivity.setLastPage(Data.SamePlaylist);
//                viewPager.setCurrentItem(4,false);
            }
        });
    }
}
