package com.lc.musicplayer.tools;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listview_Adapter_Tools_Fragment extends BaseAdapter {
    private Context context= null;
    private List<Song> list = null;
    private int mResId = 0;


    public Listview_Adapter_Tools_Fragment(Context context, List<Song> list, int resId) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final int mPosition = position;
        if(convertView ==null){
            final LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(mResId,null);
            holder = new ViewHolder();
            holder.fav_Btn=convertView.findViewById(R.id.item_music_fav);
            holder.tool1 = convertView.findViewById(R.id.item_music_tool_1);
            holder.tool2 = convertView.findViewById(R.id.item_music_tool_2);
            holder.tool3 = convertView.findViewById(R.id.item_music_tool_3);
            holder.tool4 = convertView.findViewById(R.id.item_music_tool_4);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();
        holder.fav_Btn.setBackgroundResource(list.get(position).isFav()?Data.Fav_Song:Data.Not_Fav_Song);

        holder.tool1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"tool1"+position,Toast.LENGTH_SHORT).show();
            }
        });
        holder.tool2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"tool2"+position,Toast.LENGTH_SHORT).show();
            }
        });
        holder.tool3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"tool3"+position,Toast.LENGTH_SHORT).show();
            }
        });
        holder.tool4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"tool4"+position,Toast.LENGTH_SHORT).show();
            }
        });
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
        ImageView fav_Btn;
        ImageView tool1;
        ImageView tool2;
        ImageView tool3;
        ImageView tool4;
    }
}

