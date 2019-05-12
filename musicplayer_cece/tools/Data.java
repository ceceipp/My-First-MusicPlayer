package com.lc.musicplayer.tools;

import android.util.ArrayMap;

import com.lc.musicplayer.PlayerActivity;
import com.lc.musicplayer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Data {
    public static final int Order_Repeat_Playlist = 0;
    public static final int Order_Repeat_Track = 1;
    public static final int Order_Shuffle_Playlist = 2;
    public static final int Order_Random = 3;
    public static final int Order_One_Track = 4;
    public static final List<String> Order_Mode =  Arrays.asList("Repeat Playlist","Repeat Track","Shuffle Playlist","Random","One Track");

    public static final int Fav_Song = R.drawable.fav;
    public static final int Not_Fav_Song =R.drawable.not_fav;
    public static final int Player_Loading_Msg = 1;
    public static final int MainActivityInfoUpdate=2;
    public static final int PlayerActivityInfoUpdate=3;
    public static final int MainFragmentActivityInfoUpdate=4;
    public static final int FragmentActivityInfoUpdate=5;
    public static final int SamePlaylist =0;
    public static final int SameAlbumList =1;
    public static final int SameSingerList=2;
    public static final int SamePathList =3;
    public static final int SameStringSingleList =4;
    public static final int SameStringSingleListEdit =5 ;
    public static final int MainFragmentActivity=0;
    public static final int MainActivity=1;
    public static final int PlayerActivity=2;
    public static final int FragmentActivity=3;

    //public static final List<String> Order_Mode =   List.of("1", "2");
    // Java9才支持上面的方法
}
