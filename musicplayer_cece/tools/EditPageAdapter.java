package com.lc.musicplayer.tools;

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

import java.util.List;

public class EditPageAdapter extends BaseAdapter {
    private Context context= null;
    private List<Integer> list = null;
    private List<Song> oriList;
    private int mResId = 0;
    private Player player;
    private List<Boolean> selectList;
    private int mode ;
    private ListView listView;
    private boolean isModeSelect ;
    /**
     * 不同的构造器分别有不同的mode, mode=0就是最原始的适配器, mode =1的是多选适配器**/
    public EditPageAdapter(Context context, List<Integer> list ,List<Song> oriList, int resId, Player player, ListView listView,boolean isModeSelect) {
        this.context = context;
        this.list = list;
        this.oriList = oriList;
        //mResId means item's R.layout.id
        this.mResId = resId;
        this.player = player;
        this.mode =0;
        this.listView = listView;
        this.isModeSelect = isModeSelect;
    }


    public void setList(List<Integer> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (list==null||list.isEmpty())
            return null;
        ViewHolder holder;
        if(convertView ==null){
            final LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(mResId,null);
            holder = new ViewHolder();
            holder.song = (TextView) convertView.findViewById(R.id.item_music_song);
            holder.singer = (TextView)convertView.findViewById(R.id.item_music_singer);
            holder.duration = (TextView)convertView.findViewById(R.id.item_music_duration);
            holder.album_Picture_Id = (ImageView) convertView.findViewById(R.id.album_Picture_Id);
            //holder.fav_Btn = (ImageView) convertView.findViewById(R.id.item_music_fav);
            holder.playlistBackground = (LinearLayout) convertView.findViewById(R.id.playlistBackground);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();
        //holder.song.setText(list.get(position).getSong());
        holder.song.setText(  oriList.get(list.get(position)).getSong()  );
        holder.singer.setText(  oriList.get(list.get(position)).getSinger()+" - "+oriList.get(list.get(position)).getAlbum()  );
        //holder.singer.setText(list.get(position).getSinger());
        holder.duration.setText(  oriList.get(list.get(position)).getDuration()  );
        //holder.duration.setText(list.get(position).getDuration()+" ");

        if (oriList.get(list.get(position)).getIsAlbumPicExist())
            holder.album_Picture_Id.setImageBitmap(Saver.getLocalCache(" "+oriList.get(list.get(position)).getAlbum_Picture_Id() ));
        else
            holder.album_Picture_Id.setImageResource( Player.fileFormatDetect(oriList.get(list.get(position)).getPath()));

        if (isModeSelect) {
            if (listView.isItemChecked(position))
                holder.playlistBackground.setBackgroundColor(Color.rgb(200,240,195));
            else
                holder.playlistBackground.setBackgroundColor(Color.rgb(0xff,0xcb,0x91));
        }
        else {
            if (player!=null){
                //这个写得好,,, 不用担心player那么多乱七八糟的事情, 不用取判断有没有在播放~
                if (oriList.get(list.get(position)).getId()==player.getUsingPositionId())
                    holder.playlistBackground.setBackgroundColor(Color.rgb(250,240,195));
                else
                    holder.playlistBackground.setBackgroundColor(Color.alpha(0));
            }
            else holder.playlistBackground.setBackgroundColor(Color.alpha(0));
        }
        return convertView;
    }

    static class ViewHolder{
        TextView song ;
        TextView singer ;
        TextView duration ;
        ImageView album_Picture_Id;
        //ImageView fav_Btn;
        LinearLayout playlistBackground;
    }
}

