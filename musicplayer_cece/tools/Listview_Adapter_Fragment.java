package com.lc.musicplayer.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lc.musicplayer.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listview_Adapter_Fragment extends BaseAdapter {
    private Context context= null;
    private List<Song> list = null;
    private int mResId = 0;


    public Listview_Adapter_Fragment(Context context, List<Song> list, int resId) {
        this.context = context;
        this.list = list;
        //mResId means item's R.layout.id
        this.mResId = resId;
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
            holder.song = (TextView) convertView.findViewById(R.id.item_music_song_fragment);
            holder.singer = (TextView)convertView.findViewById(R.id.item_music_singer_fragment);
            holder.duration = (TextView)convertView.findViewById(R.id.item_music_duration_fragment);
            holder.album_Picture_Id = (ImageView) convertView.findViewById(R.id.album_Picture_Id_fragment);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();
        holder.song.setText(list.get(position).getSong());
        holder.singer.setText(list.get(position).getSinger());
        holder.duration.setText(list.get(position).getDuration()+" ");
        holder.album_Picture_Id.setImageResource(Player.fileFormatdDetect(list.get(position).getPath()));
        return convertView;
    }

//以下方法可以加载专辑封面, 但内存消耗极大, 所以不用
//    private static  Bitmap loadingCover(String musicFilePath) {
//        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
//        mediaMetadataRetriever.setDataSource(musicFilePath);
//        byte[] picture =null;
//        if (mediaMetadataRetriever.getEmbeddedPicture()!=null)
//            picture = mediaMetadataRetriever.getEmbeddedPicture();
//        else{
//            mediaMetadataRetriever.setDataSource("/storage/emulated/0/_outside/Music_unn/using/21st Century Girl.mp3");
//            picture = mediaMetadataRetriever.getEmbeddedPicture();
//
//        }
//            Bitmap  bitmap = BitmapFactory.decodeByteArray(picture,0,picture.length);
//        return bitmap;
//    }

//    private void play(String path) {
//        //播放之前要先把音频文件重置
//        try {
//            mediaPlayer.reset();
//            //调用方法传进去要播放的音频路径
//            mediaPlayer.setDataSource(path);
//            //异步准备音频资源
//            mediaPlayer.prepareAsync();
//            //调用mediaPlayer的监听方法，音频准备完毕会响应此方法
//            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mediaPlayer.start();//开始音频
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    static class ViewHolder{
        TextView song ;
        TextView singer ;
        TextView duration ;
        ImageView album_Picture_Id;
    }
}

