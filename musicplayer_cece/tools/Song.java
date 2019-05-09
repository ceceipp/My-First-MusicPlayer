package com.lc.musicplayer.tools;

import java.io.Serializable;

public  class Song  implements Serializable  {
    private String singer    ;
    private String song  ;
    private String path    ;
    private String duration  ;
    private int durationMsec      ;
    private long fileSize     ;
    private int album_Picture_Id     ;
    private boolean fav   ;
    private  int id =0;
    private String album  ;
    boolean isAlbumPicExist ;
    public Song(String singer, String song, String path, String duration, long fileSize){
        this.singer = singer;
        this.song = song;
        this.path = path;
        this.duration = duration;
        this.fileSize = fileSize;
    }
    public Song(String singer, String song, String path, String duration, long fileSize, int album_Picture_Id){
        this.singer = singer;
        this.song = song;
        this.path = path;
        this.duration = duration;
        this.fileSize = fileSize;
        this.album_Picture_Id = album_Picture_Id;
    }
    public  Song(){
        this.singer ="Singer"  ;
        this.song ="Song" ;
        this.path  =  "StringPath" ;
        this.duration = "30:00 ";
        this.durationMsec =  3333 ;
        this.album_Picture_Id = 0 ;
        this.fav =false ;
        this. id =0;
        this.album = "ss" ;
        this.isAlbumPicExist =false;
    }

    public String getSinger(){
        return  singer;
    }
    public void setSinger(String singer){
        this.singer = singer;
    }

   final public String getSong(){
        return song;
    }
    public void setSong(String song){
        this.song = song;
    }

    public String getDuration(){
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDurationMsec(int durationMsec) {
        this.durationMsec = durationMsec;
    }
    public int getDurationMsec() {
        return durationMsec;
    }

    public String getPath(){
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public long getFileSize(){
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getAlbum_Picture_Id(){
        return album_Picture_Id;
    }
    public void setAlbum_Picture_Id(int album_Picture_Id){
        this.album_Picture_Id = album_Picture_Id;
    }

    public boolean isFav() {
        return fav;
    }
    public void setFav(boolean fav) {
        this.fav = fav;
    }
    public void changeFav(){
        this.fav = !this.fav;
    }

    //public Bitmap getMusicAlbumPictureBitmap(){return MusicAlbumPictureBitmap;}
   // public void setMusicAlbumPictureBitmap(Bitmap bitmap){this.MusicAlbumPictureBitmap = bitmap;}

    public int getId(){return  id;}
    public void  setId(int id){
        this.id = id;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
    public String getAlbum() {
        return album;
    }

    public void setAlbumPicExist(boolean albumPicExist) {
        isAlbumPicExist = albumPicExist;
    }

    public boolean getIsAlbumPicExist() {
        return isAlbumPicExist;
    }
}
