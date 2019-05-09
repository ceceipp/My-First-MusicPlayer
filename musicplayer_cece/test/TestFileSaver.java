package com.lc.musicplayer.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Message;

import com.lc.musicplayer.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestFileSaver  {
    private static final String Local_Cache_Path = MyApplication.getContext().getExternalFilesDir(null).toString();
           // Environment.getExternalStorageDirectory().getAbsolutePath()+"/xxxx_cache";
    public void setLocalCachePath(String url, Bitmap bitmap){
        File dir =  new File(Local_Cache_Path);
        if(!dir.exists()||!dir.isDirectory())
            dir.mkdirs();
        try {
            String fileName = Md5encode(url);
            File cacheFile = new File(dir, fileName);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(cacheFile));
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getLoaclCache(String url){
        try {
            File cacheFile = new File(Local_Cache_Path, Md5encode(url));
            if (cacheFile.exists()){
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
                return bitmap;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }

    public static String Md5encode(String string){
        MessageDigest md5s;
        byte[] hash;
        StringBuilder hex;
        try {
            md5s = MessageDigest.getInstance("MD5");
            hash=md5s.digest(string.getBytes("UTF-8"));
            hex = new StringBuilder(hash.length *2);
            for (byte b:hash){
                if ((b&0xff)<0x10){
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b&0xff));
            }
            return hex.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return "error";
    }
}

