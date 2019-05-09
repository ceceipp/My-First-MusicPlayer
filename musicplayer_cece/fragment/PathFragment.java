package com.lc.musicplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lc.musicplayer.FragmentActivity;
import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.ListAdapter_Fragment;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;


public class PathFragment extends Fragment {
    private List<Song> oriSongList;
    private List<SameStringIdList> samePathList;

    private View view;
    private ListView listView;
    private FragmentActivity mActivity;
    private List<Integer> singleList;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
        oriSongList=mActivity.getOriSongList();
//        if (oriSongList==null)
//        oriSongList=(List<Song>) Saver.readSongList("firstList");
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        oriSongList=mActivity.getOriSongList();
        if (oriSongList==null)
            oriSongList=(List<Song>) Saver.readSongList("firstList");
        samePathList=Player.idToSamePathConvert(oriSongList);
        view = inflater.inflate(R.layout.pathlist_layout, container, false);

        listView = view.findViewById(R.id.pathList);
        ListAdapter_Fragment listAdapter_fragment =
                new  ListAdapter_Fragment(MyApplication.getContext(),
                        samePathList, oriSongList, Data.SamePathList, R.layout.same_string_item);
        listView.setAdapter(listAdapter_fragment);

        initOnClick();
        mActivity.setTitleLists(Data.SamePathList, "PathList:  "+samePathList.size());
        return view;
    }
    public static PathFragment newInstance() {
        PathFragment pf = new PathFragment();
        return pf;
    }
    private void initOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewPager viewPager = getActivity().findViewById(R.id.listViewPager);
                mActivity.setSingleListPosition(position);
                mActivity.setSingleList(  Player.sameStringListToList(samePathList.get(position))  );
                //大坑, 如果直接setCurrItem为4的话, 会因为这个PathFG(他自己位置是3)本来就是在4的
                // 左边, 因为这个时候已经预载了4的页面了, 所以不会重新进入选项实现重新的数据刷新
                viewPager.setCurrentItem(2,false);
                viewPager.setCurrentItem(4,false);
                mActivity.setLastPage(Data.SamePathList);

            }
        });
    }
}
