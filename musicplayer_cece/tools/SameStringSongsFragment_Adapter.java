package com.lc.musicplayer.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.R;

import java.util.List;

public class SameStringSongsFragment_Adapter extends BaseAdapter  {
    private Context context ;
    private int mResId ;
    private int whatList ;
    private List<Song> oriList;
    private List<Integer> sameStringSingleList;
    private Dialog ddd;
    public SameStringSongsFragment_Adapter(Context context, List<Integer> sameStringSingleList
            , List<Song> oriList , int whatList, int mResId){
        this.context= context;
        this.sameStringSingleList = sameStringSingleList;
        this.whatList =whatList;
        this.mResId = mResId;
        this.oriList=oriList;
    }

    @Override
    public int getCount() {
        return sameStringSingleList.size();
    }
    @Override
    public Object getItem(int position) {
        return sameStringSingleList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if (convertView==null){
            final LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView= inflater.inflate(mResId, null);
            holder = new ViewHolder();
            holder.songName = convertView.findViewById(R.id.songName);
            holder.songsAlbum =convertView.findViewById(R.id.songsAlbum);
            holder.songSinger = convertView.findViewById(R.id.songSinger);
            holder.duration = convertView.findViewById(R.id.duration);
            holder.playerPic= convertView.findViewById(R.id.playerPic);
            holder.dialog =  convertView.findViewById(R.id.dialogBtn);
            convertView.setTag(holder);
        }
        else holder = (ViewHolder)convertView.getTag();
        holder.songName.setText( oriList.get( sameStringSingleList.get(position) ).getSong() );
        holder.songsAlbum.setText(   oriList.get(  sameStringSingleList.get(position)  ).getAlbum());
        holder.songSinger.setText(  oriList.get( sameStringSingleList.get(position)).getSinger());
        holder.duration.setText(   oriList.get(  sameStringSingleList.get(position)).getDuration());

        if (  oriList.get(  sameStringSingleList.get(position)).getAlbum_Picture_Id()==0  )
            holder.playerPic.setImageResource(
                    Player.fileFormatdDetect(  oriList.get(sameStringSingleList.get(position)).getPath()));
        else
            holder.playerPic.setImageBitmap(
                    Saver.getLocalCache( " "+ oriList.get(sameStringSingleList.get(position)).getAlbum_Picture_Id()  ));

        initSetOnClick(holder.dialog, position);
        return convertView;
    }

    static class ViewHolder{
        TextView songName ;
        TextView songsAlbum;
        TextView songSinger ;
        TextView duration;
        ImageView playerPic;
        TextView dialog;
    }
    void initSetOnClick(TextView dialog, final int position){
        dialog.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()  {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(position+oriList.get(sameStringSingleList.get(position)).getSong()+" 选择你的操作");
                menu.add(0, 0, 0, "Add to playback queue");
                menu.add(0, 1, 0, "加入喜爱的歌单");
            }
        }  );
    }
}





















