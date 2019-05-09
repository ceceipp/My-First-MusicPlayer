package com.lc.musicplayer.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.lc.musicplayer.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class Saver {
    private final String Local_Cache_Path =
            MyApplication.getContext().getExternalFilesDir(null).toString() + "cece";
    public static void setLocalCachePath(String fileName, Bitmap bitmap, int compressRadio) {
        final String Local_Cache_Path =
                MyApplication.getContext().getExternalFilesDir(null).toString() + "cece";
        File dir = new File(Local_Cache_Path+"PicCache");
        File singleFile = new File(Local_Cache_Path+"PicCache"+"/"+fileName);
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();
        if(singleFile.exists()){
            return;
        }
        try {
            File cacheFile = new File(dir, fileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, compressRadio, new FileOutputStream(cacheFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getLocalCache(String fileName) {
        final String Local_Cache_Path =
                MyApplication.getContext().getExternalFilesDir(null).toString() + "cece";
        try {
            File cacheFile = new File(Local_Cache_Path+"PicCache", fileName);
            if (cacheFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveSongList(String fileName, List<Song> songList){
        final String Local_Cache_Path =
                MyApplication.getContext().getExternalFilesDir(null).toString() + "ceceSongList";
        File dir = new File(Local_Cache_Path);
        File singleFile = new File(Local_Cache_Path+"/"+fileName);
        if (!dir.exists() || !dir.isDirectory())
            dir.mkdirs();
        if(singleFile.exists()){
            return;
        }
        try {
            File songListFile = new File(dir, fileName);
             ObjectOutputStream oos =new ObjectOutputStream(new FileOutputStream(songListFile));
             oos.writeObject(songList);
             oos.close();
            //byte[] bytes = new Byte(songList);
        }catch (Exception e){e.printStackTrace();}

    }
    public static Object readSongList(String songListFileName){
        final String Local_Cache_Path =
                MyApplication.getContext().getExternalFilesDir(null).toString() + "ceceSongList";
        try {
            File songListFile = new File(Local_Cache_Path, songListFileName);
            if (songListFile.exists()){
                ObjectInputStream oip = new ObjectInputStream(new FileInputStream(songListFile));
                Object songList = oip.readObject();
                oip.close();
                return songList;
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MyApplication.getContext(),"Err at readSongList",Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}