package com.lc.musicplayer.tools;

import com.lc.musicplayer.test.TestActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class SameStringIdList implements Comparable<SameStringIdList> ,Serializable {
    private String string ;
    private ArrayList list =new ArrayList() ;
    public SameStringIdList(String string, ArrayList list){
        this.string = string;
        this.list =list;
    }
    public SameStringIdList(String string){
        //string = new String();
        this.string=string;
    }
    public SameStringIdList(){ }

    public int getStringOfSongId(){
        return  (int)list.get(0);
    }

    public void setString(String string){
        this.string = string;
    }
    public ArrayList getList(){
        return this.list;
    }
    public String getString(){
        return this.string;
    }
    public int compareTo(SameStringIdList s){

            if (null==this.string){
                return -1;
            }
            if (null==s.getString()){
                return 1;
            }
            if (this.string ==s.getString()){
                return 0;
            }
        return this.string.compareTo(s.getString());
    }

}
