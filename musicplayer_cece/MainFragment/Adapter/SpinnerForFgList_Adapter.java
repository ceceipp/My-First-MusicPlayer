package com.lc.musicplayer.MainFragment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lc.musicplayer.R;
import com.lc.musicplayer.tools.SameStringIdList;

import java.util.List;

public class SpinnerForFgList_Adapter  extends BaseAdapter {
    private List<String> titles;
    private List<List<SameStringIdList>> sameStringIdListssss;
    private Context context;

    public SpinnerForFgList_Adapter(Context context,List<String> titles){
        this.titles = titles;
        this.context = context;
    }
    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {
        return titles.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            final LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.spinnerforfglist_layout,null);
            holder = new ViewHolder();
            holder.sameString = convertView.findViewById(R.id.spinnerForFgList_sameString);
            convertView.setTag(holder);
        }
        else
            holder =(ViewHolder) convertView.getTag();
        holder.sameString.setText(titles.get(position));
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    static class ViewHolder{
        TextView sameString;
    }
}
