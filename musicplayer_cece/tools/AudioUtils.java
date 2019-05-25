package com.lc.musicplayer.tools;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.lc.musicplayer.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class  AudioUtils  {
    public  static  List<Song> getSongs(Context context){
        ArrayList<Song> songs ;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        //如果此处为NULL, 则返回所有值, 非常低效, 而下面这样写, 可以指定返回的值
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.ALBUM
                },
//                 这一句好像是数据库的查找方式,
//                我猜是这样的, MIMETYPE ==audio/x-mpeg or MIMETYPE ==audio/x-wav, 就是找这两个格式的文件
//                那个问号"?"是在那里占着位置的, 如果你的"audio/x-mpeg", "audio/x-wav"
//                改写成kk, agg, 那么就变成"MIMETYPE ==kk or MIMETYPE ==gg",
//                就是说找的是kk和gg文件，还有,audio/x-wav这类是专有名词，要上网查询
//                注意【MediaStore.Audio.Media.MIME_TYPE + "=? or "+ MediaStore.Audio.Media.MIME_TYPE + "=?",】
//              中的 "=? or "里面的or后面必须有空格，否则就变成了“where is kk orwhere is gg”,数据库识别不了这语句
//              这次搞明白了Cursor的用法，搞明白了很多细节的东西
//                MediaStore.Audio.Media.MIME_TYPE + "=? or " +MediaStore.Audio.Media.MIME_TYPE + "=?",
//                new String[] { "audio/mpeg" ,"audio/x-wav"},
                null, null,
                MediaStore.Audio.Media.IS_MUSIC);

        songs = new ArrayList<Song>();
        int id = 0;
        if (cursor.moveToFirst()){
            do {
                Song   song = new Song();
                //这个cursor就是说有2000多行的5列数组， 每moveToNext，就是move去下一行
                // 就是说2000首歌， 每首歌有5个参数[歌名，歌手...等5个参数]， 每次move就是下一首歌
                //下面的数字对应上面那5个String的数组位置, 从0开始.
                song.setSong(cursor.getString(0));
                song.setSinger(cursor.getString(2));
                 song.setDuration(formatTime(cursor.getInt(1)));
                 song.setDurationMsec(cursor.getInt(1));
                 song.setFileSize(cursor.getLong(3));
                 song.setPath(cursor.getString(4));
                 song.setAlbum_Picture_Id((int)cursor.getLong(5));
                 song.setAlbum(cursor.getString(6));
                 song.setId(id);
                 //song.setMusicAlbumPictureBitmap(loadingCover("/storage/emulated/0/_outside/Music_unn/using/21st Century Girl.mp3"));
                // /storage/emulated/0/_outside/Music_unn/using/21st Century Girl.mp3
                 if (cursor.getLong(3)>800*1024){
                     songs.add(song);
                     id++;
                 }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return songs;
    }

    public static Bundle getBundle(Context context, Bundle bundle){
        ArrayList<Song> songs ;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        //如果此处为NULL, 则返回所有值, 非常低效, 而下面这样写, 可以指定返回的值
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID
                },
//                 这一句好像是数据库的查找方式,
//                我猜是这样的, MIMETYPE ==audio/x-mpeg or MIMETYPE ==audio/x-wav, 就是找这两个格式的文件
//                那个问号"?"是在那里占着位置的, 如果你的"audio/x-mpeg", "audio/x-wav"
//                改写成kk, agg, 那么就变成"MIMETYPE ==kk or MIMETYPE ==gg",
//                就是说找的是kk和gg文件，还有,audio/x-wav这类是专有名词，要上网查询
//                注意【MediaStore.Audio.Media.MIME_TYPE + "=? or "+ MediaStore.Audio.Media.MIME_TYPE + "=?",】
//              中的 "=? or "里面的or后面必须有空格，否则就变成了“where is kk orwhere is gg”,数据库识别不了这语句
//              这次搞明白了Cursor的用法，搞明白了很多细节的东西
//                MediaStore.Audio.Media.MIME_TYPE + "=? or " +MediaStore.Audio.Media.MIME_TYPE + "=?",
//                new String[] { "audio/mpeg" ,"audio/x-wav"},
                null, null,
                MediaStore.Audio.Media.IS_MUSIC);

        songs = new ArrayList<Song>();
        int id = 0;
        if (cursor.moveToFirst()){
            do {
                Song   song = new Song();
                //这个cursor就是说有2000多行的5列数组， 每moveToNext，就是move去下一行
                // 就是说2000首歌， 每首歌有5个参数[歌名，歌手...等5个参数]， 每次move就是下一首歌
                //下面的数字对应上面那5个String的数组位置, 从0开始.
                song.setSong(cursor.getString(0));
                song.setSinger(cursor.getString(2));
                song.setDuration(formatTime(cursor.getInt(1)));
                song.setFileSize(cursor.getLong(3));
                song.setPath(cursor.getString(4));
                song.setId(id++);
                //song.setMusicAlbumPictureBitmap(loadingCover("/storage/emulated/0/_outside/Music_unn/using/21st Century Girl.mp3"));
                // /storage/emulated/0/_outside/Music_unn/using/21st Century Girl.mp3
                if (cursor.getLong(3)>800*1024){
                    songs.add(song);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        bundle.putSerializable("Using", songs);
        return bundle;
    }

    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }

    public static String formatLongTime(long time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }



}
