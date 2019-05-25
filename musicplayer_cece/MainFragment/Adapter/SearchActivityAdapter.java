package com.lc.musicplayer.MainFragment.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.Player;
import com.lc.musicplayer.tools.SameStringIdList;
import com.lc.musicplayer.tools.Song;

import java.util.ArrayList;
import java.util.List;
/**
 * 输入的3个list一个都不能是null;
 * **/
public class SearchActivityAdapter extends BaseAdapter {
    private List<SameStringIdList> sameTitleList, sameAlbumList, sameSingerList, allList;
    private int sameTitleListCount, sameAlbumListCount, sameSingerListCount;
    private Context context;
    private List<Song> oriSongList;
    int resId;
    public SearchActivityAdapter(
            List<SameStringIdList> sameTitleListFromSearch,
            List<SameStringIdList> sameAlbumListFromSearch,
            List<SameStringIdList> sameSingerListFromSearch,
            List<Song> oriSongListFromSearch, Context context, int resIdFromSearch){
        sameTitleList = sameTitleListFromSearch;
        sameAlbumList = sameAlbumListFromSearch;
        sameSingerList = sameSingerListFromSearch;
        sameTitleListCount = sameTitleList.size();
        sameAlbumListCount = sameAlbumList.size();
        sameSingerListCount = sameSingerList.size();
        allList = new ArrayList<>();
        allList.addAll(sameTitleList);
        allList.addAll(sameAlbumList);
        allList.addAll(sameSingerList);
        this.context = context;
        oriSongList = oriSongListFromSearch;
        resId = resIdFromSearch;
    }

    public void setSame3List(
            List<SameStringIdList> sameTitleListFromSearch,
            List<SameStringIdList> sameAlbumListFromSearch, List<SameStringIdList> sameSingerListFromSearch) {
        sameTitleList = sameTitleListFromSearch;
        sameAlbumList = sameAlbumListFromSearch;
        sameSingerList = sameSingerListFromSearch;
        sameTitleListCount = sameTitleList.size();
        sameAlbumListCount = sameAlbumList.size();
        sameSingerListCount = sameSingerList.size();
        allList.clear();
        allList.addAll(sameTitleList);
        allList.addAll(sameAlbumList);
        allList.addAll(sameSingerList);
    }

    @Override
    public int getCount() {
        return (sameTitleListCount + sameAlbumListCount + sameSingerListCount);
    }
    @Override
    public long getItemId(int position) {
        if (getCount()==0)
            return 0;
        else
            return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder holder;
         if (convertView==null){
             final LayoutInflater inflater = LayoutInflater.from(context);
             convertView = inflater.inflate(resId, null);
             holder = new ViewHolder();
             holder.im_SearchActItem = convertView.findViewById(R.id.im_SearchActItem);
             holder.tvTitle_SearchActItem = convertView.findViewById(R.id.tvTitle_SearchActItem);
             holder.tvCounts_SearchActItem = convertView.findViewById(R.id.tvCounts_SearchActItem);
             holder.tvFrom_SearchActItem = convertView.findViewById(R.id.tvFrom_SearchActItem);
             convertView.setTag(holder);
         }
         else
             holder = (ViewHolder) convertView.getTag();

        holder.im_SearchActItem.setImageResource(Player.fileFormatDetect(oriSongList.get(allList.get(position).getStringOfSongId()).getPath()));
        holder.tvTitle_SearchActItem.setText( allList.get(position).getSameString() );
        if ( allList.get(position).getList()!=null && !allList.get(position).getList().isEmpty() )
            holder.tvCounts_SearchActItem.setText(" "+allList.get(position).getList().size());
        else
            holder.tvCounts_SearchActItem.setText(" 0 ");
        if (position<sameTitleListCount&&!sameTitleList.isEmpty()) {
            holder.tvFrom_SearchActItem.setText("歌名");
         }
         else if (sameTitleListCount<=position && position<sameTitleListCount+sameAlbumListCount){
            holder.tvFrom_SearchActItem.setText("专辑");
        }
        else if ( (sameTitleListCount+sameAlbumListCount) <= position && position<sameTitleListCount+sameAlbumListCount+sameSingerListCount){
            holder.tvFrom_SearchActItem.setText("歌手");
        }
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        if (getCount()==0)
            return null;
        return allList.get(position);
    }

    static class ViewHolder{
        ImageView im_SearchActItem;
        TextView tvTitle_SearchActItem, tvCounts_SearchActItem, tvFrom_SearchActItem;
    }
}
