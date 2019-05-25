package com.lc.musicplayer.tools;

import java.io.Serializable;
import java.util.ArrayList;

public class SameStringIdList implements Comparable<SameStringIdList> ,Serializable {
    private String string ;
    private ArrayList<Integer> list =new ArrayList<>() ;
    public SameStringIdList(String string, ArrayList list){
        this.string = string;
        this.list = list;
    }
    public SameStringIdList(String string){
        //string = new String();
        this.string=string;
    }
    public SameStringIdList(){ }

    public int getStringOfSongId(){
        if (list==null||list.isEmpty())
            return 0;
        return  list.get(0);
    }

    public void setString(String string){
        this.string = string;
    }
    public ArrayList<Integer> getList(){
        return this.list;
    }
    public void setList(ArrayList<Integer> newList){
        this.list = newList;
    }
    public String getSameString(){
        return this.string;
    }
    public int compareTo(SameStringIdList s){

            if (null==this.string){
                return -1;
            }
            if (null==s.getSameString()){
                return 1;
            }
            if (this.string ==s.getSameString()){
                return 0;
            }
        return this.string.compareTo(s.getSameString());
    }

}
