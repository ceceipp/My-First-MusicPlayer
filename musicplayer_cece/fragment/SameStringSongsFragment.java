package com.lc.musicplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lc.musicplayer.VpFragmentActivity;
import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.SameStringSongsFragment_Adapter;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;
/**
 * @position  是指前一个大目录点击的位置
 * @sameStringList  现在基本用不上了, 只是为了保证程序的健壮性.
 * 这个viewpager用这个fragment转屏时会出现mActivity的onCreate未完成便和取player的错误,
 * 因为转屏时不是按照默认方法来加载fg的, 而是fg被私自加载, 此时player不能通过mActivity获取
 * 所以打算不用viewpager了, 直接一个layout用多个fragment...这样稳一点...但这个程序已经写了很多了哈哈哈
 * **/
public class SameStringSongsFragment extends Fragment {
    private List<Song> oriSongList;
    private List<SameStringIdList> sameStringList;
    private List<Integer> singleList;
    private View view;
    private ListView listView;

    private VpFragmentActivity mActivity;
    private Player player;
    private int position;
    private SameStringSongsFragment_Adapter sAdapter;
    public static SameStringSongsFragment newInstance() {
        SameStringSongsFragment sssf = new SameStringSongsFragment();
        return sssf;
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (VpFragmentActivity) context;
        oriSongList=mActivity.getOriSongList();
        if (oriSongList==null)
            oriSongList=(List<Song>) Saver.readSongList("firstList");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        oriSongList=mActivity.getOriSongList();
        if (oriSongList==null)
            oriSongList=(List<Song>) Saver.readSongList("firstList");
        initData();
        view = inflater.inflate(R.layout.samestringsongs_layout, container, false);
        listView = view.findViewById(R.id.sameStringSongs);
        initFragment();
        initOnClick();
        mActivity.setTitleLists(4, "Item:  "+singleList.size());
        return view;
    }
    @Override
    public void onResume( ){
        super.onResume();
    }

    private void initData(){
        //player = mActivity.getPlayer();
        position = mActivity.getSingleListPosition();
        //这句注释是第五页返回的DATA, 如不写下面这句,
        // 应该用公有方法 getSingleList 提前 get 这个 FragmentActivity的singleList(从其他fragment获得的)
        // sameStringList子目录adapter(第五页)的默认DataList, 否则必然出错
        //而且如果不是点击前面四页进去的第五页, 会导致第5页加载空数据, 出错闪退
        if (mActivity.getSingleList()!=null){
                singleList = mActivity.getSingleList();
        }
        else{
            sameStringList =setDefaultSameStringList();
            singleList = Player.sameStringListToList(sameStringList, 0) ;
        }

    }
    private void initFragment(){
        listView = view.findViewById(R.id.sameStringSongs);
        SameStringSongsFragment_Adapter sAdapter=
                new SameStringSongsFragment_Adapter(MyApplication.getContext(),
                        singleList, oriSongList, Data.SameStringSingleList, R.layout.samestringsongs_item_layout);
        listView.setAdapter(sAdapter);
    }
    private void initOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                player = mActivity.getPlayer();
                player.setUsingPositionList(singleList);
                player.firstClickListItem(  singleList.get(position) );
            }
        });
    }

    private List<SameStringIdList> setDefaultSameStringList(){
        List<SameStringIdList> defaultPlaylists=new ArrayList<>();
        defaultPlaylists.add(new SameStringIdList("Pendulum"));
        defaultPlaylists.add(new SameStringIdList("Nightwish"));
        defaultPlaylists.add(new SameStringIdList("The Ting Tings"));
        defaultPlaylists.add(new SameStringIdList("Ellie Goulding"));
        for (int i = 0;i<oriSongList.size();i++) {
            if (oriSongList.get(i).getSinger().contains("Pendulum"))
                defaultPlaylists.get(0).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("Nightwish"))
                defaultPlaylists.get(1).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("The Ting Tings"))
                defaultPlaylists.get(2).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("Ellie Goulding"))
                defaultPlaylists.get(3).getList().add(i);
        }
        return defaultPlaylists;
    }
}