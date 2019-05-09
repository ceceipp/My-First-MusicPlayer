package com.lc.musicplayer.tools;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 *  @  songsPosition  是指传入的Songs数组中选中的序号
 *  @  usingPosition 是指正在播放的Position
 *  @ usingPositionList 正在使用的播放列表与传入的Songs数组的列表的映射
* */

public class Player_Test {
    public   List<Song> songs;
    public int usingPosition=0;
    public List<Integer> usingPositionList = new ArrayList<>();
    public  MediaPlayer mediaPlayer = new MediaPlayer();
    //public Timer timer =null;


//    private Context context;
//    public Player(ArrayList<Song> songs, Context context){
//        songs = this.songs;
//        //context = this.context;
//        for (int i= 0;i<songs.size();i++)
//            usingPositionList.set( i, i );
//    }
    public Player_Test(List<Song> songs){
        int i =0;
        this.songs=songs;
        for (i=0; i<this.songs.size(); i++)
            usingPositionList.add(i,i);
        //usingPositionList.add(i,i);
        playSong(songs.get(0).getPath());
        mediaPlayer.pause();
        usingPosition=usingPositionList.get(4);
    }

    public void playSong(String path){
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
//ok
    public void startOrPause(){
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else{
            if (mediaPlayer==null)
                playSong(songs.get(0).getPath());
            else
                mediaPlayer.start();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
            }
        });
    }

    public void stop(){
        if (mediaPlayer!=null)
            mediaPlayer.stop();
        try{
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void nextSong(){
        if(mediaPlayer!=null && usingPosition < usingPositionList.size()) {
            mediaPlayer.stop();
            try{
                mediaPlayer.reset();
                playSong(songs.get(usingPositionList.get(usingPosition)).getPath());
                usingPosition++;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else if (usingPosition==songs.size() && mediaPlayer!=null){
            mediaPlayer.stop();
            try{
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songs.get(usingPositionList.get(0)).getPath());
                usingPosition=0;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void preSong(){
        if(mediaPlayer!=null && usingPosition>0) {
            mediaPlayer.stop();
            try{
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songs.get(usingPositionList.get(usingPosition-1)).getPath());
                usingPosition--;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else if (usingPosition==0 && mediaPlayer!=null){
            mediaPlayer.stop();
            try{
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songs.get(usingPositionList.size()-1).getPath());
                usingPosition=usingPositionList.size()-1;
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void seekTo(int mSec){
        if (mediaPlayer!=null)
            mediaPlayer.stop();
        try{
            mediaPlayer.prepare();
            mediaPlayer.seekTo(mSec);
            mediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public int seekBarPercentage(){
        float percentage =0;
        percentage =  (float) mediaPlayer.getCurrentPosition()/songs.get(usingPositionList.get(usingPosition)).getDurationMsec();
        //假设seekBar长度为int200
        percentage=percentage*200;
        return (int)percentage;
    }

//    public void setusingPosition(int min, int max){
//        Random random = new Random();
//        for (int i=0; i <usingPosition.size();i++) {
//            if (random.nextInt(usingPosition.size()))
//        }
//
//    }
}
