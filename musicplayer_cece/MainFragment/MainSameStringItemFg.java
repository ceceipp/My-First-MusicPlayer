package com.lc.musicplayer.MainFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    private FgSendDataToAct myFg;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myFg = (FgSendDataToAct) getActivity();
        oriSongList = (List<Song>) Saver.readSongList("firstList");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            this.singleList = (List<Integer>) getArguments().getSerializable("singleList");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.samestringsongs_layout, container, false);
        listView = view.findViewById(R.id.sameStringSongs);
        myFg.sendSingleListListView(listView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        if (listView!=null&&this.isVisible())
            myFg.sendItemPositionAndFromTop(
                    listView.getFirstVisiblePosition(),
                    listView.getChildAt(0).getTop() );
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

    public MainSameStringItemFg(){}
    public static MainSameStringItemFg newInstance( ) {
        MainSameStringItemFg mainSameStringItemFg = new MainSameStringItemFg();
        return mainSameStringItemFg;
    }
    public static MainSameStringItemFg newInstance( List<Integer> singleList) {
        MainSameStringItemFg mainSameStringItemFg = new MainSameStringItemFg();
        if (singleList==null)
            singleList= ((List<SameStringIdList>) Saver.readData("playlist")).get(0).getList();
        Bundle bundle =new Bundle();
        bundle.putSerializable("singleList", (Serializable)singleList);
        mainSameStringItemFg.setArguments(bundle);
        return mainSameStringItemFg;
    }

    public void initListView(List<Integer> singleList){
        listView = view.findViewById(R.id.sameStringSongs);
        sAdapter= new SameStringSongsFragment_Adapter(MyApplication.getContext()
                        ,singleList, oriSongList,Data.SameStringSingleList, R.layout.samestringsongs_item_layout);
        listView.setAdapter(sAdapter);
    }
    private void initOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myFg.sendPlayerListAndItem(singleList, position);
                //一定要先换列表再放歌, 不然有可能usingPositionId++了, 再用新的列表, 如果列表歌曲数目不够, 就会超出范围...
//                ( (MainFragmentActivity) getActivity()).getPlayer().setUsingPositionList(singleList);
//                ( (MainFragmentActivity) getActivity()).getPlayer().firstClickListItem(singleList.get(position));
            }
        });
    }
}
