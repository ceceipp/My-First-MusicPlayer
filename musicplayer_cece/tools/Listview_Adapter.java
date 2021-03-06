package com.lc.musicplayer.tools;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lc.musicplayer.R;

import java.util.List;

public class Listview_Adapter extends BaseAdapter {
    private Context context= null;
    private List<Song> list = null;
    private int mResId = 0;
    private Player player;
    private List<Boolean> selectList;
    private int mode ;
    /**
     * 不同的构造器分别有不同的mode, mode=0就是最原始的适配器, mode =1的是多选适配器**/
    public Listview_Adapter(Context context, List<Song> list, int resId,Player player) {
        this.context = context;
        this.list = list;
        //mResId means item's R.layout.id
        this.mResId = resId;
        this.player = player;
        this.mode =0;
    }
    public Listview_Adapter(Context context, List<Song> list, int resId,Player player, List<Boolean> selectList) {
        this.context = context;
        this.list = list;
        //mResId means item's R.layout.id
        this.mResId = resId;
        this.player = player;
        this.selectList = selectList;
        this.mode = 1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
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

        if(  list.get(position).getIsAlbumPicExist())
            holder.album_Picture_Id.setImageBitmap(Saver.getLocalCache(" "+list.get(position).getAlbum_Picture_Id() ));
      else
            holder.album_Picture_Id.setImageResource( Player.fileFormatDetect(list.get(position).getPath()));

        holder.fav_Btn.setBackgroundResource(list.get(position).isFav()?Data.Fav_Song:Data.Not_Fav_Song);
        holder.fav_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(mPosition).changeFav();
                v.setBackgroundResource(list.get(mPosition).isFav()?Data.Fav_Song:Data.Not_Fav_Song);
            }
        });
        switch (mode){
            case 0:{
                if (list.get(position).getId()==player.getUsingPositionId())
                    holder.playlistBackground.setBackgroundColor(Color.rgb(250,240,195));
                else
                    holder.playlistBackground.setBackgroundColor(Color.alpha(0));
                break;
            }
            case 1:{
                if (selectList.get(position))
                    holder.playlistBackground.setBackgroundColor(Color.rgb(250,240,195));
                else
                    holder.playlistBackground.setBackgroundColor(Color.alpha(0));
                break;
            }
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

