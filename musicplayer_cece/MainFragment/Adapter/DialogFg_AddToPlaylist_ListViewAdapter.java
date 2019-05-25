package com.lc.musicplayer.MainFragment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.SameStringIdList;

import java.util.List;

public class DialogFg_AddToPlaylist_ListViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<SameStringIdList> mSameStringIdLists;
    private int mResId;

    public DialogFg_AddToPlaylist_ListViewAdapter(Context context, List<SameStringIdList> sameStringIdLists, int resId){
        mContext = context;
        mResId = resId;
        mSameStringIdLists =sameStringIdLists;
    }

    @Override
    public Object getItem(int position) {
        if (mSameStringIdLists==null||mSameStringIdLists.isEmpty())
            return null;
        else {
            return mSameStringIdLists.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (mSameStringIdLists==null||mSameStringIdLists.isEmpty())
        return 0;
        else {
            return position;
        }
    }

    @Override
    public int getCount() {
        if (mSameStringIdLists==null||mSameStringIdLists.isEmpty())
            return 0;
        else {
            return mSameStringIdLists.size();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mSameStringIdLists==null||mSameStringIdLists.isEmpty())
            return null;
        else {
            ViewHolder holder;
            if (convertView==null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(mResId, null);
                holder = new ViewHolder();
                holder.playlistTitle = convertView.findViewById(R.id.playlistTitle);
                holder.songsCount = convertView.findViewById(R.id. songsCount);
                convertView.setTag(holder);
            }
            else holder = (ViewHolder) convertView.getTag();
            holder.playlistTitle.setText("   "+mSameStringIdLists.get(position).getSameString());
            holder.songsCount.setText(" "+mSameStringIdLists.get(position).getList().size()+" ");
            return convertView;
        }
    }
    static class ViewHolder{
        TextView playlistTitle, songsCount;
    }
}
