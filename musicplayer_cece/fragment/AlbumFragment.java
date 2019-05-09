package com.lc.musicplayer.fragment;

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
import com.lc.musicplayer.tools.AudioUtils;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.ListAdapter_Fragment;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.List;

public class AlbumFragment extends Fragment {
    private List<Song> oriSongList;
    private List<SameStringIdList> sameAlbumList;
    private ListView listView;
    private View view;
    private FragmentActivity mActivity;
    private List<Integer> singleList;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
        oriSongList=mActivity.getOriSongList();
//        if (oriSongList==null)
//            oriSongList=(List<Song>) Saver.readSongList("firstList");
    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        oriSongList=mActivity.getOriSongList();
        if (oriSongList==null)
            oriSongList=(List<Song>) Saver.readSongList("firstList");
        sameAlbumList=Player.idToSameAlbumConvert(oriSongList);

        view =inflater.inflate(R.layout.albumlist_layout,container,false);

        listView = view.findViewById(R.id.albumList);
        ListAdapter_Fragment listAdapter_fragment =
                new  ListAdapter_Fragment(MyApplication.getContext(),
                        sameAlbumList, oriSongList, Data.SameAlbumList, R.layout.same_string_item);
        listView.setAdapter(listAdapter_fragment);

        initOnClick();
        mActivity.setTitleLists(Data.SameAlbumList, "AlbumList:  "+sameAlbumList.size());
        return view;
    }
    public static AlbumFragment newInstance() {
        AlbumFragment af = new AlbumFragment();
        return af;
    }

    private void initOnClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "ok " + position, Toast.LENGTH_SHORT).show();
                ViewPager viewPager = getActivity().findViewById(R.id.listViewPager);
                mActivity.setSingleListPosition(position);
                singleList=Player.sameStringListToList(sameAlbumList.get(position));
                mActivity.setSingleList(singleList);
                viewPager.setCurrentItem(4, false);
                mActivity.setLastPage(Data.SameAlbumList);

            }
        });
    }
}
