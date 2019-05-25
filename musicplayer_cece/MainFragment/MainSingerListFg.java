package com.lc.musicplayer.MainFragment;

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
import com.lc.musicplayer.tools.ListAdapter_Fragment;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.List;

public class MainSingerListFg extends Fragment {
    private List<Song> oriSongList ;
    private List<SameStringIdList> singerList ;
    private ListAdapter_Fragment listAdapter_fragment;
    private View view;
    private ListView listView;
    private VpFragmentActivity mActivity;
    private List<Integer> singleList;
    private int testInt=0;
    private FgSendDataToAct myFg;
    private DialogFgFromPlaylistFg dialogFgFromPlaylistFg;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        myFg = (FgSendDataToAct)context;
        oriSongList = (List<Song>) Saver.readSongList("firstList");
        if (Saver.readData("singerList")!= null){
            singerList =(List<SameStringIdList>) Saver.readData("singerList");
        }
        else {
            singerList = Player.idToSameSingerConvert(oriSongList);
            Saver.saveData("singerList", singerList, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.singerlist_layout, container, false);
        initListView();
        initOnClick();
        dialogFgFromPlaylistFg = new DialogFgFromPlaylistFg();
        return view;
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

    public MainSingerListFg(){}
    public static MainSingerListFg newInstance( ) {
        MainSingerListFg mainSingerListFg = new MainSingerListFg();
        return mainSingerListFg;
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
                singleList = Player.sameStringListToList(singerList.get(position));
                myFg.sendSameString("歌手: "+ singerList.get(position).getSameString(),singleList.size());
                myFg.sendSingleList(singleList, "歌手: "+ singerList.get(position).getSameString());
                //( (MainFragmentActivity) getActivity()).showSingleListFg(singleList);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogFgFromPlaylistFg(singerList.get(position), oriSongList);
                return true;
            }
        });
    }
    private void showDialogFgFromPlaylistFg(SameStringIdList singleList, List<Song> oriSongList){
        dialogFgFromPlaylistFg.showNow(getActivity().getSupportFragmentManager(), "dialogFgFromSingerListFg");
        dialogFgFromPlaylistFg.showLlDialog_edit_notPlaylist(singleList, singerList, oriSongList,  listAdapter_fragment );
        //dialogFgFromPlaylistFg.setNotPlaylistMode();
        //dialogFgFromPlaylistFg.showDetailsFgLlBg(singleList, oriSongList);
        //myFg.sendSameStringListListView(listView);
    }
}