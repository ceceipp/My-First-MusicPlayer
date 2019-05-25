package com.lc.musicplayer.MainFragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.ListAdapter_Fragment;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.List;

public class DialogFgFromPlaylistFg extends DialogFragment {
    private View view;
    private TextView tvRenamePlaylist, tvDetails, tvRemoveFromList;
    private LinearLayout llDialog_edit;
    private LinearLayout llForNewListName;
    private Button btnForNewListName;
    private EditText etForNewListName;


    private LinearLayout  detailsFgLlBg;
    private TextView detailsTv1, detailsTv2, detailsTv3, detailsTv4, detailsTv5;
    private ImageView detailsIv0;

    private final String TAG = "Ser1212";

    private LinearLayout llForRename;
    private EditText etForRename;
    private Button btnForRename;

    private LinearLayout llDialog_edit_notPlaylist;
    private TextView tvDetails_notPlaylist;

    private SameStringIdList singleListFromFg;
    private List<SameStringIdList> sameStringIdListFromFg;
    private List<Song> oriSongListFromFg;
    private ListAdapter_Fragment  playlistAdapter;


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.playback_queue_dialog_fg_layout, container, false);
        llDialog_editInitFindId();
        detailsFgLlBgInitFindId();
        llForRenameInitFindId();
        llDialog_edit_notPlaylistInitFindId();
        tvRenamePlaylist.setText("   Rename Playlist");
        llDialog_editInitOnClick();
        return view;
    }
    public void llDialog_editInitFindId(){
        llDialog_edit = view.findViewById(R.id.llDialog_edit);
        tvRenamePlaylist = view.findViewById(R.id.tvRenamePlaylist);
        tvDetails = view.findViewById(R.id.tvDetails);
        tvRemoveFromList = view.findViewById(R.id.tvRemoveFromList);

        llForNewListName = view.findViewById(R.id.llForNewListName);
        btnForNewListName = view.findViewById(R.id.btnForNewListName);
        etForNewListName = view.findViewById(R.id.etForNewListName);
        //viewForAddToPlaylist = view.findViewById(R.id.viewForAddToPlaylist);
    }

    public void renameSingleListFromPlaylist(SameStringIdList singleList, List<SameStringIdList> oriSameStringIdList){
        //etForNewListName.setVisibility(View.VISIBLE);
        if (etForRename.getText()==null||etForRename.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "没有输入", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Player.stringDetect(etForRename.getText().toString())){
            Toast.makeText(getActivity(), "非法字符, 请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            String newName = etForRename.getText().toString();
            if (oriSameStringIdList==null||oriSameStringIdList.isEmpty()
                    ||singleList==null||singleList.getList()==null
                    ||singleList.getSameString()==null
                    ||singleList.getSameString().isEmpty()
                    ||singleList.getList().isEmpty()   )
                return;
            for (int i=0;i<oriSameStringIdList.size();i++){
                if (singleList.getSameString().equals( oriSameStringIdList.get(i).getSameString() )){
                    oriSameStringIdList.set(i, new SameStringIdList(newName, oriSameStringIdList.get(i).getList()) );
                    Saver.saveData("playlist", oriSameStringIdList, false);
                    Toast.makeText(getActivity(), "playlist 已保存", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(getActivity(), "操作错误", Toast.LENGTH_SHORT).show();
        }
    }
    public void llDialog_editInitOnClick(){
        btnForRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llForRename.getVisibility()== View.VISIBLE){
                    renameSingleListFromPlaylist(singleListFromFg, sameStringIdListFromFg);
                    dismiss();
                }
            }
        });
        tvRenamePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDialog_edit.setVisibility(View.GONE);
                llForRename.setVisibility(View.VISIBLE);
            }
        });
        tvDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDialog_edit.setVisibility(View.GONE);
                showDetailsFgLlBg(singleListFromFg, oriSongListFromFg);
            }
        });
        tvRemoveFromList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromPlaylist(singleListFromFg, sameStringIdListFromFg);
                dismiss();
            }
        });
        tvDetails_notPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDialog_edit_notPlaylist.setVisibility(View.GONE);
                showDetailsFgLlBg(singleListFromFg, oriSongListFromFg);
            }
        });
    }
    public void detailsFgLlBgInitFindId(){
        detailsFgLlBg = view.findViewById(R.id.detailsFgLlBg);
        detailsIv0 = view.findViewById(R.id.detailsIv0);
        detailsTv1 = view.findViewById(R.id.detailsTv1);
        detailsTv2 = view.findViewById(R.id.detailsTv2);
        detailsTv3 = view.findViewById(R.id.detailsTv3);
        detailsTv4 = view.findViewById(R.id.detailsTv4);
        detailsTv5 = view.findViewById(R.id.detailsTv5);
    }

    public void showLlDialog_edit(SameStringIdList singleList, List<SameStringIdList> oriPlaylist, List<Song> oriSongList, ListAdapter_Fragment playlistAdapter ){
        llDialog_edit.setVisibility(View.VISIBLE);
        setItemLongClickData(singleList, oriPlaylist, oriSongList, playlistAdapter);
    }
    public void showLlDialog_edit_notPlaylist(SameStringIdList singleList, List<SameStringIdList> oriPlaylist, List<Song> oriSongList, ListAdapter_Fragment playlistAdapter ){
        llDialog_edit_notPlaylist.setVisibility(View.VISIBLE);
        setItemLongClickData(singleList, oriPlaylist, oriSongList, playlistAdapter);
    }

    public void showDetailsFgLlBg(SameStringIdList sameStringIdList, List<Song> oriSongList) {
        if (sameStringIdList==null||sameStringIdList.getList()==null||sameStringIdList.getList().isEmpty()) {
            Toast.makeText(getActivity(), "空列表", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        List<Integer> singleList = sameStringIdList.getList();
        for (int i=0;i<singleList.size();i++){
            if (  oriSongList.get(  singleList.get(i)  ).getIsAlbumPicExist()  ){
                detailsIv0.setImageBitmap(Player.loadingCover(oriSongList.get(  singleList.get(i)  ).getPath()  ));
                detailsFgLlBg.setBackground( new  BitmapDrawable(getResources(), Player.blur(Saver.getLocalCache(" "+  oriSongList.get(  singleList.get(i)  ).getAlbum_Picture_Id()  )) ));
                break;
            }
            if (i==(singleList.size()-1)) {
                detailsIv0.setImageResource(Player.fileFormatDetect(oriSongList.get(singleList.get(i)).getPath()));
                detailsFgLlBg.setBackground(new BitmapDrawable(getResources(), Player.blur(Player.loadingCover(oriSongList.get( singleList.get(i) ).getPath()))));
            }
        }
        detailsTv1.setText(sameStringIdList.getSameString());
        String singer = oriSongList.get(singleList.get(0)).getSinger();
        detailsTv2.setText(singer+" ...");
        String album = oriSongList.get(singleList.get(0)).getAlbum();
        detailsTv3.setText(album+" ...");

        long fileSizeByte =0;
        for (int i=0; i<singleList.size();i++){
            fileSizeByte = fileSizeByte + oriSongList.get( singleList.get(i) ).getFileSize();
        }
        String fileSize = String.format("%.2f", ((float) fileSizeByte / (1024 * 1024)));
        String time = Player.allDurationTime(singleList, oriSongList);
        detailsTv5.setText( time + ", " + fileSize + "MB");

        detailsTv4.setText("~");
        detailsFgLlBg.setVisibility(View.VISIBLE);
    }
    private void removeFromPlaylist(SameStringIdList singleList, List<SameStringIdList> oriSameStringIdList){
        if (singleList==null||oriSameStringIdList==null||oriSameStringIdList.isEmpty()){
            Toast.makeText(getActivity(), "不存在此歌单, 无法删除",Toast.LENGTH_SHORT ).show();
            return;
        }
        String singleListName =  singleList.getSameString();
        for (int i=0;i<oriSameStringIdList.size();i++){
            if ( singleListName.equals(oriSameStringIdList.get(i).getSameString()) ){
                oriSameStringIdList.remove(i);
                Saver.saveData("playlist", oriSameStringIdList, false);
                Toast.makeText(getActivity(), "已删除: "+singleListName,Toast.LENGTH_SHORT ).show();
                //callBack.updatePlaylistFragment();
                playlistAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
    public void llForRenameInitFindId(){
        llForRename=view.findViewById(R.id.llForRename);
        etForRename=view.findViewById(R.id.eTForRename);
        btnForRename= view.findViewById(R.id.btnForRename);
    }
    public void setItemLongClickData(SameStringIdList singleList, List<SameStringIdList> oriSameStringIdList, List<Song> oriSongList,ListAdapter_Fragment oriPlaylistAdapter){
        singleListFromFg = singleList;
        oriSongListFromFg = oriSongList;
        sameStringIdListFromFg = oriSameStringIdList;
        playlistAdapter = oriPlaylistAdapter;
    }
    public void llDialog_edit_notPlaylistInitFindId(){
        llDialog_edit_notPlaylist=view.findViewById(R.id.llDialog_edit_notPlaylist);
        tvDetails_notPlaylist = view.findViewById(R.id.tvDetails_notPlaylist);
    }
}
