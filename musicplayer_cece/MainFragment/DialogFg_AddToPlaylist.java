package com.lc.musicplayer.MainFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.EditActivity;
import com.lc.musicplayer.MainFragment.Adapter.DialogFg_AddToPlaylist_ListViewAdapter;
import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;

public class DialogFg_AddToPlaylist extends DialogFragment {
    private View view;
    private TextView tvAddToPlaylist, tvAddToQueue, tvDetails, tvRemoveFromList;
    private LinearLayout llDialog_edit;

    public interface CallBackAboutDialogFg_AddToPlaylistToEditActivity{
        public void getTheSelectStringAndOriFavList(String theSelectString, List<Integer> oriFavList);
        public void setANewList(String newListName);
    }

    //private RecyclerView viewForAddToPlaylist;
    private LinearLayout llForNewListName;
    private Button btnForNewListName;
    private EditText etForNewListName;
    private ListView viewForAddToPlaylist;
    private DialogFg_AddToPlaylist_ListViewAdapter adapter;

    private List<SameStringIdList> sameStringIdLists;
    private CallBackAboutDialogFg_AddToPlaylistToEditActivity callBack;
    private final String TAG = "Ser1212";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callBack = (CallBackAboutDialogFg_AddToPlaylistToEditActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.playback_queue_dialog_fg_layout, container, false);
        addNewListInitFindId();
        //每个SameStringIdList 有一个SameString和一个List<Integer>.
        sameStringIdLists = new ArrayList<>();
        if (Saver.readData("playlist")!=null)
            sameStringIdLists = (List<SameStringIdList>) Saver.readData("playlist") ;
        else sameStringIdLists= Player.initDefaultPlaylist((List<Song>) Saver.readSongList("firstList"), null);

        adapter = new DialogFg_AddToPlaylist_ListViewAdapter(getActivity(), sameStringIdLists, R.layout.playback_queue_dialog_fg_item_layout);
        viewForAddToPlaylist.setAdapter(adapter);
        viewForAddToPlaylist.setDividerHeight(2);
        addNewListInitOnClick();
        return view;
    }
    public void addNewListInitFindId(){
        llForNewListName = view.findViewById(R.id.llForNewListName);
        btnForNewListName = view.findViewById(R.id.btnForNewListName);
        etForNewListName = view.findViewById(R.id.etForNewListName);
        viewForAddToPlaylist = view.findViewById(R.id.viewForAddToPlaylist);
    }
    public void addNewListInitOnClick(){
        btnForNewListName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etForNewListName.getText()==null||etForNewListName.getText().toString().isEmpty())
                    Toast.makeText(getActivity(),"没有输入", Toast.LENGTH_SHORT).show();
                else {
                    callBack.setANewList( etForNewListName.getText().toString() );
                    dismiss();
                }
            }
        });
        viewForAddToPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callBack.getTheSelectStringAndOriFavList(
                        sameStringIdLists.get(position).getSameString(), sameStringIdLists.get(position).getList()  );
            }
        });
    }

    public void showViewForAddToPlaylist(){
        llForNewListName.setVisibility(View.VISIBLE);
    }
}
