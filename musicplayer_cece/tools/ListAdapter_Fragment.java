package com.lc.musicplayer.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lc.musicplayer.R;

import java.util.List;

public class ListAdapter_Fragment extends BaseAdapter {
    private Context context ;
    private int mResId = 0;
    private int whatList ;
    private List<Song> oriList;
    private List<SameStringIdList> sameList;
    public ListAdapter_Fragment(Context context, List<SameStringIdList> sameList
            , List<Song> oriList , int whatList, int mResId){
        this.context= context;
        this.sameList = sameList;
        this.whatList =whatList;
        this.mResId = mResId;
        this.oriList=oriList;
    }

    public void setSameList(List<SameStringIdList> sameList) {
        this.sameList = sameList;
    }

    @Override
    public int getCount() {
        if (sameList==null||sameList.isEmpty())
            return 0;
        return sameList.size();
    }
    @Override
    public Object getItem(int position) {
        if (sameList==null||sameList.isEmpty())
            return null;
        return sameList.get(position);
    }
    @Override
    public long getItemId(int position) {
        if (sameList==null||sameList.isEmpty())
            return 0;
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if (convertView == null){
            final LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(mResId,null);
            holder = new ViewHolder();
            holder.sameString =convertView.findViewById(R.id.songAlbum_sa);
            holder.songsCount = convertView.findViewById(R.id.songsCount_sa);
            holder.singer = convertView.findViewById(R.id.songSinger_sa);
            holder.allDuration = convertView.findViewById(R.id.duration_sa);
            holder.album_Picture_Id= convertView.findViewById(R.id.playerPic_sa);
            convertView.setTag(holder);
        }
        else 
            holder = (ViewHolder)convertView.getTag();

        holder.sameString.setText(sameList.get(position).getSameString());
        holder.songsCount.setText( sameList.get(position).getList().size()+" songs");

        if (whatList!=Data.SameSingerList){
            //if 不只有一个歌手
            if (sameList.get(position).getList().size()>1 )
                holder.singer.setText(
                        oriList.get( sameList.get(position).getStringOfSongId() ).getSinger()+" etc.." );
            else
                holder.singer.setText(
                        oriList.get( sameList.get(position).getStringOfSongId() ).getSinger()+" etc.."  );
        }
        else
            //Singer为sameString时, singer.setText应该显示album而不是singer
            holder.singer.setText(  oriList.get( sameList.get(position).getStringOfSongId() ).getAlbum() );

        holder.allDuration.setText( Player.allDurationTime(sameList.get(position).getList(), oriList) );

        for (int i=0;i<sameList.get(position).getList().size();i++){
            if (  oriList.get(  (int)sameList.get(position).getList().get(i)  ).getIsAlbumPicExist()  ){
                holder.album_Picture_Id.setImageBitmap(Saver.getLocalCache(" "+  oriList.get(  (int)sameList.get(position).getList().get(i)  ).getAlbum_Picture_Id()  ));
                break;
            }
            if (i==(sameList.get(position).getList().size()-1))
                holder.album_Picture_Id.setImageResource(  Player.fileFormatDetect(  oriList.get((int) sameList.get(position).getStringOfSongId()).getPath()  )  );
        }
//        if (oriList.get( sameList.get(position).getStringOfSongId() ).getAlbum_Picture_Id()==0)
//            holder.album_Picture_Id.setImageResource(
//                    Player.fileFormatDetect(
//                            oriList.get(  (int)(sameList.get(position).getStringOfSongId())  ).getPath()
//                ));
//        else {
//            for (int i=0 ; i< sameList.get(position).getList().size() ;i++)
//                if (  oriList.get(  (int)sameList.get(position).getList().get(i)  ).getAlbum_Picture_Id()!=0  ){
//                    holder.album_Picture_Id.setImageBitmap(
//                            Saver.getLocalCache(" "+oriList.get(
//                                    (int)sameList.get(position).getList().get(i)  ).getAlbum_Picture_Id() ));
//                    break;
//                }
//        }
        return convertView;
    }

    static class ViewHolder{
        TextView sameString;
        TextView songsCount ;
        TextView singer ;
        TextView allDuration;
        ImageView album_Picture_Id;
    }
}
