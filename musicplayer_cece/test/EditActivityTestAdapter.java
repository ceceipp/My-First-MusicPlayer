package com.lc.musicplayer.test;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.Data;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.Saver;
import com.lc.musicplayer.tools.Song;

import java.util.List;

public class EditActivityTestAdapter extends BaseAdapter {
    private Context context= null;
    private List<Song> list = null;
    private int mResId = 0;
    private Player player;
    private List<Boolean> selectList;
    private int mode ;
    private ListView listView;
    private boolean isModeSelect ;
    /**
     * 不同的构造器分别有不同的mode, mode=0就是最原始的适配器, mode =1的是多选适配器**/
    public EditActivityTestAdapter(Context context, List<Song> list, int resId, Player player, ListView listView,boolean isModeSelect) {
        this.context = context;
        this.list = list;
        //mResId means item's R.layout.id
        this.mResId = resId;
        this.player = player;
        this.mode =0;
        this.listView = listView;
        this.isModeSelect = isModeSelect;
    }
    public EditActivityTestAdapter(Context context, List<Song> list, int resId, Player player, List<Boolean> selectList) {
        this.context = context;
        this.list = list;
        //mResId means item's R.layout.id
        this.mResId = resId;
        this.player = player;
        //this.selectList = selectList;
        this.mode = 1;
    }

    public void setList(List<Song> list) {
        this.list = list;
    }

    public void setModeSelect(boolean b){
        this.isModeSelect = b;
    }
    @Override
    public int getCount() {
        if (list==null||list.isEmpty())
            return 0;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list==null||list.isEmpty())
            return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (list==null||list.isEmpty())
            return 0;
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
        if (list==null||list.isEmpty())
            return null;
        ViewHolder holder;
        final int mPosition = position;
        if(convertView ==null){
            final LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(mResId,null);
            holder = new ViewHolder();
            holder.song = (TextView) convertView.findViewById(R.id.item_music_song);
            holder.singer = (TextView)convertView.findViewById(R.id.item_music_singer);
            holder.duration = (TextView)convertView.findViewById(R.id.item_music_duration);
            holder.album_Picture_Id = (ImageView) convertView.findViewById(R.id.album_Picture_Id);
            holder.fav_Btn = (ImageView) convertView.findViewById(R.id.item_music_fav);
            holder.playlistBackground = (LinearLayout) convertView.findViewById(R.id.playlistBackground);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();
        holder.song.setText(list.get(position).getSong());
        holder.singer.setText(list.get(position).getSinger());
        holder.duration.setText(list.get(position).getDuration()+" ");

        if(  list.get(position).getAlbum_Picture_Id()==0)
            holder.album_Picture_Id.setImageResource( Player.fileFormatDetect(list.get(position).getPath()));
        else
            holder.album_Picture_Id.setImageBitmap(Saver.getLocalCache(" "+list.get(position).getAlbum_Picture_Id() ));

        holder.fav_Btn.setBackgroundResource(list.get(position).isFav()?Data.Fav_Song:Data.Not_Fav_Song);
        holder.fav_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(mPosition).changeFav();
                v.setBackgroundResource(list.get(mPosition).isFav()?Data.Fav_Song:Data.Not_Fav_Song);
            }
        });
        if (isModeSelect) {
            if (listView.isItemChecked(position))
                holder.playlistBackground.setBackgroundColor(Color.rgb(200,240,195));
            else
                holder.playlistBackground.setBackgroundColor(Color.rgb(0xff,0xcb,0x91));
        }
        else {
            if (list.get(position).getId()==player.getUsingPositionId())
                holder.playlistBackground.setBackgroundColor(Color.rgb(250,240,195));
            else
                holder.playlistBackground.setBackgroundColor(Color.rgb(0xff,0xcb,0x91));
        }
        return convertView;
    }

    static class ViewHolder{
        TextView song ;
        TextView singer ;
        TextView duration ;
        ImageView album_Picture_Id;
        ImageView fav_Btn;
        LinearLayout playlistBackground;
    }
}

