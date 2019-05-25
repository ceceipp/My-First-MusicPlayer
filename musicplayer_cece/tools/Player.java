package com.lc.musicplayer.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lc.musicplayer.MyApplication;
import com.lc.musicplayer.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *  @  songsPosition  是指传入的Songs数组中选中的序号
 *  @  usingPosition 是指正在播放的Position
 *  @ usingPositionList 正在使用的播放列表与传入的Songs数组的列表的映射
 *  @ oriUsingPositionList 是UsingPositionList设置前的上一个list, 也可以理解成
 *      外部传进来的映射list
 *  @ songIsSelectedList 是edit界面的List的item是否被选中的list
 *  @ sameStringSingleList 是sameStringSingleList传进来的数组,
 *      key序号是songList的排序, value是对应oriSongList的歌曲ID,
 *      可以根据ID重新设置usingPositionList
* */

public  class   Player {
    public  int order_Mode = Data.Order_Repeat_Playlist;
    private  List<Song> songs  ;
    private int usingPosition;
    private int usingPositionId;
    private List<Integer> usingPositionList = new ArrayList<>();
    private List<Integer> oriUsingPositionList = new ArrayList<>();
    public  MediaPlayer mediaPlayer = new MediaPlayer();
    public boolean musicInfoNeedUpdate=true;
    private List<Boolean> songIsSelectedList;
    private List<Integer> sameStringSingleList;


    public Player(){
        usingPosition=0;
        usingPositionId=0;
        musicInfoNeedUpdate=false;
    }
    /**
     * @param songs 就是输入的原始歌单(一定是全部歌曲的歌单,
     *              喜爱歌曲的歌单是要根据这个原始歌单来映射的)
     * **/
    public Player(List<Song> songs){
        if (songs==null){
            int i =0;
            this.songs=new ArrayList<>();
            oriUsingPositionList = usingPositionList;
            usingPositionId = 0;
            musicInfoNeedUpdate=true;
        }
        else if (songs.isEmpty()){
            return;
        }
        else {
            int i =0;
            this.songs=songs;
            for (i=0; i<this.songs.size(); i++)
                usingPositionList.add(i,i);
            //playSong(songs.get(usingPositionList.get(usingPosition)).getPath());
            oriUsingPositionList = usingPositionList;
            playSong(songs.get(0).getPath());
            usingPosition=0;
            usingPositionId =0;
            mediaPlayer.pause();
            musicInfoNeedUpdate=true;
        }
    }
    //没人用这个方法
    public Player(List<Song> songs, List<Integer> savedLastList, int savedLastPosition,int progressMSec){
        if (songs==null){
            int i =0;
            this.songs=new ArrayList<>();
            oriUsingPositionList = usingPositionList;
            usingPosition = 0;
            usingPositionId = 0;
            musicInfoNeedUpdate=true;
        }
        else if (songs!=null&&savedLastList!=null){
            int i =0;
            this.songs=songs;
            this.usingPositionList =savedLastList;
            oriUsingPositionList = usingPositionList;
            this.usingPosition = savedLastPosition;
            if (this.usingPosition < this.usingPositionList.size()){
                playSong(songs.get(this.usingPositionList.get( this.usingPosition)).getPath());
            }
            else {
                this.usingPosition = 0;
                playSong(songs.get(usingPositionList.get(0)).getPath());
            }
            mediaPlayer.pause();
            musicInfoNeedUpdate=true;
            if (progressMSec<=songs.get(this.usingPositionList.get(this.usingPosition)).getDurationMsec())
                seekTo(progressMSec);
        }
    }
    public void playSong(String path){
        if (path==null){
            //nextSong();
            Toast.makeText(MyApplication.getContext(),"No this song",Toast.LENGTH_LONG).show();
        }
        if (usingPosition<0)
            mediaPlayer.stop();
        //这个if是为了playbackQueue加的, 出错了先删掉他哈哈哈,
        // 如果不加cache.isEmpty判断, 则在第一次进入player会把当前usingList设置成空的list
//        if (playbackQueue.isEmpty()&&!cacheUsingPositionList.isEmpty()){
//            usingPositionList = cacheUsingPositionList;
//            usingPosition = cacheUsingPosition;
//            cacheUsingPositionList.clear();
//        }
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            usingPositionId = usingPositionList.get(usingPosition);
        }
        catch (IOException e){
            e.printStackTrace();nextSong();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                switch (order_Mode){
                    case Data.Order_Repeat_Track: mediaPlayer.start(); break;
                    case Data.Order_Repeat_Playlist: nextSong(); break;
                    case Data.Order_Shuffle_Playlist: nextSong(); break;
                    case Data.Order_One_Track: mediaPlayer.pause(); break;
                    case Data.Order_Random: usingPosition = randomNext(usingPositionList.size()); nextSong(); break;
                    default:break;
                }
            }
        });
        musicInfoNeedUpdate=true;
        //这个if也是为了playbackQueue加的, 出错了先删掉他哈哈哈
//        if (!playbackQueue.isEmpty()){
//            playbackQueue.remove(0);
//            usingPosition = -1;
//        }
    }
    //这个cacheBeforePlaybackQueue()也是为了playbackQueue加的, 出错了先删掉他哈哈哈
//    public void cacheBeforePlaybackQueue(){
//        cacheUsingPositionList = usingPositionList;
//        cacheUsingPosition = usingPosition;
//    }
    //这个 setPlaybackQueue()也是为了playbackQueue加的, 出错了先删掉他哈哈哈
//    public void setPlaybackQueue(List<Integer> playbackQueue){
//        this.playbackQueue = playbackQueue;
//        usingPositionList = playbackQueue;
//        usingPosition = -1;
//    }
    // 这个 addToPlaybackQueue()也是为了playbackQueue加的, 出错了先删掉他哈哈哈
//    public void addToPlaybackQueue(int oriSongListId){
//        playbackQueue.add(oriSongListId);
//        setPlaybackQueue(this.playbackQueue);
//    }

    public void startOrPause(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            musicInfoNeedUpdate=true;
        }
        else{
            if (mediaPlayer==null)
                //原来是 playSong(songs.get(0).getPath());
                playSong(songs.get(usingPositionList.get(0)).getPath());
            else {
                mediaPlayer.start();
                musicInfoNeedUpdate = true;
            }
        }
    }

    public void stop(){
        if (mediaPlayer!=null)
            mediaPlayer.stop();
        try{
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        musicInfoNeedUpdate=true;
    }

    public void nextSong(){
        if (order_Mode==Data.Order_Random)
            usingPosition = randomNext(usingPositionList.size());
        usingPosition++;
        if(mediaPlayer!=null && usingPosition < usingPositionList.size()) {
            mediaPlayer.stop();
            try{
                playSong(songs.get(usingPositionList.get(usingPosition)).getPath());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else if (usingPosition>=usingPositionList.size() && mediaPlayer!=null){
            mediaPlayer.stop();
            try{
                usingPosition=0;
                playSong(songs.get(usingPositionList.get(usingPosition)).getPath());
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
                usingPosition--;
                playSong(songs.get(usingPositionList.get(usingPosition)).getPath());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else if (usingPosition<=0 && mediaPlayer!=null){
            mediaPlayer.stop();
            try{
                usingPosition=usingPositionList.size()-1;
                playSong(songs.get(usingPositionList.get(usingPosition)).getPath());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void seekTo(int mSec){
        if (mediaPlayer!=null){
            try{
                mediaPlayer.seekTo(mSec);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        musicInfoNeedUpdate=true;
    }
    public int seekBarPercentage(){
        //假设seekBar长度为int200
        int percentage = 200*mediaPlayer.getCurrentPosition();
        return (int)  (    (float) percentage/songs.get(usingPositionList.get(usingPosition)).getDurationMsec()    ) ;
    }

    public void change_Order_Mode(){
        order_Mode++;
        order_Mode = order_Mode%5;
        if (order_Mode==Data.Order_Shuffle_Playlist){
             usingPositionList = getRandomList(usingPositionList.size(),oriUsingPositionList);
            //usingPosition=usingPositionList.indexOf(usingPosition)  ;
            if (usingPositionList.indexOf(getUsingPositionId())>=0)
                usingPosition = usingPositionList.indexOf(getUsingPositionId());
            else
                usingPosition = 0;
        }
        if (order_Mode!=Data.Order_Shuffle_Playlist && order_Mode!=Data.Order_Random){
            //usingPosition=getUsingPositionId()  ;
            usingPositionList = oriUsingPositionList;
            if (usingPositionList.indexOf(getUsingPositionId())>=0)
                usingPosition=usingPositionList.indexOf(getUsingPositionId());
            else
                usingPosition = 0;
        }
        musicInfoNeedUpdate=true;
    }
    public static int randomNext(int num){
        Random random = new Random();
        num = random.nextInt(num);
        return num;
    }
    public int getUsingPosition() {
        return usingPosition;
    }
    public int getCurrentMSec(){
        if (mediaPlayer!=null)
            return mediaPlayer.getCurrentPosition();
        return 0;
    }
    public void setUsingPositionList(List<Integer> usingPositionList) {
        oriUsingPositionList = usingPositionList;
//        if (order_Mode==Data.Order_Shuffle_Playlist)
//            this.usingPositionList= getRandomList(usingPositionList.size(), usingPositionList);
        this.usingPositionList = usingPositionList;
    }
    public List<Integer> getUsingPositionList() {
        return usingPositionList;
    }
    public int getUsingPositionId() {
        //return usingPositionList.get(usingPosition);
        //如果歌曲的ID改变了, 就不能用此方法了
        if (usingPosition>=usingPositionList.size()){
            return 0;
        }
        return usingPositionId;
    }
    /**
     * 此句同上面等价(在歌单导入无误的情况下),但会出现UI更新不正确,
     * 因为如果不执行setUsingPositionId(clickSongPosition)的话;
     * UI更新是根据usingPosition来决定的, 虽然下面的方法能播放歌曲
     * 但是UI还是原来的UI, 所以这个firstClickListItem函数我打算
     * 写多一个方法相同但加多一个参数的函数, 加的参数就是歌曲列表的参数
     * playSong(songs.get( clickSongPosition).getPath());
     * ==============================================================
     * if (songs==null||songs.isEmpty()||usingPositionList.isEmpty())
     *             return;
     *             是后来加的, 加的时候已经快完成这个作品了, 但是却发现没有好好考虑当一首歌都没有的情况,
     *             这给了我启发, 以后写的时候, 不要一上来就以常态来写程序, 而是一上来就要以程序处于
     *             极端情况的时候来写程序, 这样做就不用再在一大堆现成的程序中思考极端情况,
     *             遇到极端情况也更容易发现和处理(因为这个时候程序还很小, 耦合性很低, 容易处理)
     *
     * **/
    public void firstClickListItem(int clickSongId){
        setUsingPositionId(clickSongId);
        if (songs==null||songs.isEmpty()||usingPositionList.isEmpty())
            return;
        playSong(songs.get(usingPositionList.get(usingPosition)).getPath());
    }
    public void firstTapFromList(int clickSongPosition, List<Integer> list){
        setUsingPositionList( list );
        setUsingPositionId(clickSongPosition);
        playSong(songs.get(usingPositionList.get(usingPosition)).getPath());
    }
    /**usingPosition<0意味着不是从第一个Activity点击歌曲曲目进去的,所以不用执行firstClickListItem()
     * 原来是this.usingPosition=usingPositionList.indexOf(usingPosition);但是在换了歌曲曲单后会
     * 出现Id为1000的歌曲, 原来的曲单index为4, 新曲单长度为3,那么就会导致换成了新曲单后set1000,
     * 然后index得到4, 但是曲单只有3, 内存溢出
     **/
    public void setUsingPositionId(int songId) {
        if (songId<0) {
            this.usingPosition = usingPositionList.size() - 1;
            Toast.makeText(MyApplication.getContext(),
                    "No this song at playlist queue. <0 ",Toast.LENGTH_SHORT).show();
        }
        else{
            if ( usingPositionList.contains(songId) )
                this.usingPosition=usingPositionList.indexOf(songId);
            else {
                this.usingPosition = usingPositionList.size() - 1;
                Toast.makeText(MyApplication.getContext(),
                        "No this song at playlist queue. >0",Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void setSongs(List<Song> songs){
        this.songs =songs;
    }
    public List<Song> getSongs(){return this.songs;}

    public  static int fileFormatDetect(String string){
        Map<String, Integer> map = new HashMap<>();
        map.put(".mp3", R.drawable.mp3);
        map.put(".wav",R.drawable.wav);
        map.put(".m4a" , R.drawable.m4a);
        map.put(".ape", R.drawable.ape);
        map.put("flac", R.drawable.flac);
        string = string.toLowerCase();
        string =string.substring(string.length()-4);
        if (map.get(string)==null)
            return R.drawable.other;
        return map.get(string);
    }

    public  void findSongWithTitle(String string){
    for (int i=0;i<songs.size();i++){
        if(songs.get(i).getSong().contains(string)) {
            if (usingPositionList.contains(songs.get(i).getId()))
                firstClickListItem(songs.get(i).getId());
            else
                Toast.makeText(MyApplication.getContext(),"当前歌曲不在此播放歌单中",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    Toast.makeText(MyApplication.getContext(),
                "全部歌曲标题均不包含搜索的字段", Toast.LENGTH_SHORT).show();
}

/**
 * 为了提高复用性, 下列4个方法特意做成相似的样子, 以便搭配Adatper, 而且经过多次改良,
 * 这几个方法是始祖方法的1/5耗时哈哈哈
 * 简单检查了一下, 下面四个均可以得到一个isEmpty的数组. 所以基本不太担心一首歌都没有的手机
 * **/
    public static List<SameStringIdList> idToSameAlbumConvert(List<Song> songList){
        Set<String> stringSet =new android.support.v4.util.ArraySet<>();
        for (int i=0;i<songList.size();i++){
            stringSet.add(songList.get(i).getAlbum());
        }

        ArrayList<String> stringList = new ArrayList(stringSet);
        Collections.sort(stringList);
        ArrayList<SameStringIdList> sameStringIdLists=new ArrayList<>();
        for (int num=0; num<stringList.size(); num++){
            sameStringIdLists.add(new SameStringIdList(stringList.get(num)));
        }
        for ( int i =0;i<songList.size();i++){
            for ( int j = 0; j<stringList.size();j++){
                if ( stringList.get(j).contentEquals( songList.get(i).getAlbum()  ))
                { sameStringIdLists.get(j).getList().add(i);  break; }
            }
        }
        //Collections.sort(sameStringIdLists);
        stringSet = null;
        stringList=null;
        return sameStringIdLists;
    }
    public static List<SameStringIdList> idToSameSingerConvert(List<Song> songList){
        Set<String> stringSet =new android.support.v4.util.ArraySet<>();
        for (int i=0;i<songList.size();i++){
            stringSet.add(songList.get(i).getSinger());
        }

        ArrayList<String> stringList = new ArrayList(stringSet);
        Collections.sort(stringList);
        ArrayList<SameStringIdList> sameStringIdLists=new ArrayList<>();
        for (int num=0; num<stringList.size(); num++){
            sameStringIdLists.add(new SameStringIdList(stringList.get(num)));
        }
        for ( int i =0;i<songList.size();i++){
            for ( int j = 0; j<stringList.size();j++){
                if ( stringList.get(j).contentEquals( songList.get(i).getSinger()  ))
                { sameStringIdLists.get(j).getList().add(i);  break; }
            }
        }
        //Collections.sort(sameStringIdLists);
        stringSet = null;
        stringList=null;
        return sameStringIdLists;
    }
    /**
    * 注释掉的三句话是避免String中没有"/"导致出错, 除了注释掉的三个方法外,
     * 还有一个简单粗暴的方法, 直接在裁剪前直接加入4个"/",能运行
    * */
    public static List<SameStringIdList> idToSamePathConvert(List<Song> songList){
        ArrayList<String> arrayList=new ArrayList<>();
        for (int i =0; i<songList.size(); i++){
            //if (!songList.get(i).getPath().contains("/")){ arrayList.add(songList.get(i).getPath());       break; }
            String oriString = new String(songList.get(i).getPath());
             oriString = " //// "+oriString;
            int cutEnd = oriString.lastIndexOf("/");
            int cut = cutEnd;
                String stringCache =oriString.substring(0,cut);
            //if (!stringCache.contains("/")){ arrayList.add(songList.get(i).getPath());       break; }
                cut = stringCache.lastIndexOf("/");
                stringCache =oriString.substring(0,cut);
            int cutBegin = stringCache.lastIndexOf("/");
            //if (!stringCache.contains("/")){ arrayList.add(songList.get(i).getPath());       break; }
            String string=oriString.substring(cutBegin+1,cutEnd);
            arrayList.add(string);
        }

        Set<String> pathSet = new android.support.v4.util.ArraySet<>(arrayList);
        ArrayList<String> stringsList = new ArrayList(pathSet);

        Collections.sort(stringsList);
        ArrayList<SameStringIdList> sameStringIdLists=new ArrayList<>();
        for (int num=0; num<stringsList.size(); num++){
            sameStringIdLists.add(new SameStringIdList(stringsList.get(num)));
        }
        for ( int i =0;i<arrayList.size();i++){
            for ( int j = 0; j<stringsList.size();j++){
                if ( stringsList.get(j).contentEquals( arrayList.get(i) ))
                { sameStringIdLists.get(j).getList().add(i);  break; }
            }
        }
        //Collections.sort(sameStringIdLists);
        arrayList=null;
        stringsList=null;
        return sameStringIdLists;
    }
    public static void findPath(List<Song> songs){
        ArrayList<String> arrayList=new ArrayList<>();
        String string = null;
        int cut =0;
        for (int i =0; i<songs.size(); i++){
            if (!songs.get(i).getPath().contains("/"))
                break;
            cut = songs.get(i).getPath().lastIndexOf("/");
            string=songs.get(i).getPath().substring(0,cut);
            arrayList.add(string);
        }
        Set<String> pathSet = new android.support.v4.util.ArraySet<>(arrayList);
    }

    public static String allDurationTime( List<Integer> singleList,List<Song> oriList){
        long timeMSec = 0;
        for (int i=0; i < singleList.size(); i++  ){
            timeMSec = timeMSec  +
                    oriList.get(  (singleList.get(i))  ).getDurationMsec();
        }
        return AudioUtils.formatLongTime(timeMSec);
    }
    public static List<Song> singleListToSongList(List<Integer> singleList, List<Song> fullSongList){
        List<Song> songList = new ArrayList<>();
        if (singleList==null||singleList.isEmpty()||fullSongList==null||fullSongList.isEmpty())
            return songList;
        for (int i=0;i<singleList.size();i++) {
            songList.add(fullSongList.get( singleList.get(i) ));
        }
        return songList;
    }

    public static List<Integer>  sameStringListToList(List<SameStringIdList> sameStringIdLists,int position){
        ArrayList<Integer> singleList = new ArrayList<>();
        singleList = sameStringIdLists.get(position).getList();
        Collections.sort(singleList);
        return singleList;
    }
    public static List<Integer>  sameStringListToList( SameStringIdList  sameStringIdList){
        ArrayList<Integer> singleList = new ArrayList<>();
        singleList =sameStringIdList.getList();
        Collections.sort(singleList);
        return singleList;
    }

    public List<SameStringIdList> idStringSving(List<Song> songList) {
        //此方法是上面好几个方法的始祖, 也可以用, 但是比上面的几个方法要慢五倍
        List<SameStringIdList> forReturnList = new ArrayList<>();
        ArrayList<String> sameStringList = new ArrayList<>();
        ArrayList list=new ArrayList();
        for (  int i = 0; i < songList.size(); i++) {
            for (int  j = 0; j < songList.size(); j++) {
                if (songList.get(i).getAlbum().contentEquals(songList.get(j).getAlbum()))
                    list.add(j);
            }
            if (!sameStringList.contains(songList.get(i).getAlbum())){
                forReturnList.add(new SameStringIdList(songList.get(i).getAlbum(),list));
                sameStringList.add(songList.get(i).getAlbum());
            }
            list=new ArrayList();
        }
        sameStringList=null;list=null;
        return forReturnList;
    }
    /**Player大图片的加载方法, 一般只加载一张图片时采用**/
    public static Bitmap loadingCover(String musicFilePath) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(musicFilePath);
        byte[] picture = null;
        Bitmap bitmap = null;
        if (mediaMetadataRetriever.getEmbeddedPicture() != null) {
            picture = mediaMetadataRetriever.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        } else {
//            bitmap = BitmapFactory.decodeResource(
//                    MyApplication.getContext().getResources(), R.drawable.nonepic2);
            bitmap = BitmapFactory.decodeResource(
                    MyApplication.getContext().getResources(), Player.fileFormatDetect(musicFilePath));
        }
        return bitmap;
        //return compressImage(bitmap,100);
    }

    /**
     * 质量压缩方法
     * @param image
     * 有更好的方法在下面
     */
    public static Bitmap compressImage(Bitmap image,int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
//        int options = 90;
//        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
//        while (baos.toByteArray().length / 1024 > 100) {
//            // 重置baos即清空baos
//            baos.reset();
//            // 这里压缩options%，把压缩后的数据存放到baos中
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
//            // 每次都减少10
//            options -= 10;
//        }
//        // 把压缩后的数据baos存放到ByteArrayInputStream中

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    public void setSongIsSelectedList(List<Boolean> songIsSelectedList){
        if (this.songIsSelectedList==null){
            this.songIsSelectedList = new ArrayList<>();
            for (int i=0; i<songIsSelectedList.size();i++){
                this.songIsSelectedList.add(false);
            }
        }
        this.songIsSelectedList=songIsSelectedList;
    }
    public List<Boolean> getSongIsSelectedList(){
        return this.songIsSelectedList;
    }
    public void setSameStringSingleList(List<Integer> sameStringSingleList) {
        this.sameStringSingleList = sameStringSingleList;
    }
    public List<Integer> getSameStringSingleList() {
        return sameStringSingleList;
    }

    public static List<Integer> getOrderList(int num){
        List<Integer> list = new ArrayList<>();
        for (int i=0; i<num; i++)
            list.add(i,i);
        return list;
    }

    public void addSongIdToPlaybackQueue(int id){
        if (usingPositionList.contains(id)){
            int index = usingPositionList.indexOf(id);
            if ((usingPosition+1)<usingPositionList.size()){
                int cacheId = usingPositionList.get(usingPosition+1);
                usingPositionList.set(usingPosition+1, id);
                usingPositionList.set(index, cacheId);
            }
            //如果刚好光标在最后一首歌
            else {
                int cacheId = usingPositionList.get(0);
                usingPositionList.set(0, id);
                usingPositionList.set(index, cacheId);
            }
        }
        else {
            usingPositionList.add(usingPosition+1,id);
        }
    }
    public void addSongIdListToPlaybackQueue(List<Integer> addList){
        if (addList == null || addList.isEmpty()) {
            Toast.makeText(MyApplication.getContext(), "没有可加歌曲.", Toast.LENGTH_SHORT).show();
        }
        else {
            for (int i=0;i<addList.size();i++){
                Iterator<Integer> it = usingPositionList.iterator();
                while (it.hasNext()){
                    int num = it.next();
                    if (addList.get(i)==num){
                        it.remove();
                        break;
                    }
                }
            }
            int cacheUsingPosition = usingPositionList.indexOf(usingPositionId);
            usingPositionList.addAll(cacheUsingPosition+1 , addList);
        }
    }
    public static List<Integer> getRandomList(int num, List<Integer> oriList){
        //1.获取scope范围内的所有数值，并存到数组中
        int[] randomArray=new int[num];
        for(int i=0;i<randomArray.length;i++){
            randomArray[i]=i;
        }

        //2.从数组random中取数据，取过后的数改为-1
        int[] numArray=new int[num];//存储num个随机数
        int i=0;
        while(i<numArray.length){
            int index=(int)(Math.random()*num);
            if(randomArray[index]!=-1){
                numArray[i]=randomArray[index];
                randomArray[index]=-1;
                i++;
            }
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int j=0;j<num;j++)
            arrayList.add(oriList.get(numArray[j]));
        return arrayList;
    }
/***
 * 这个getRandomList只能乱序连续整数list,例如1,2,3; 如果时1,3,5, 这个list返回的就不是这三个数字了
 * 而是1,2,3的组合
 * **/
    public static List<Integer> getRandomList(int num){
        //1.获取scope范围内的所有数值，并存到数组中
        int[] randomArray=new int[num];
        for(int i=0;i<randomArray.length;i++){
            randomArray[i]=i;
        }

        //2.从数组random中取数据，取过后的数改为-1
        int[] numArray=new int[num];//存储num个随机数
        int i=0;
        while(i<numArray.length){
            int index=(int)(Math.random()*num);
            if(randomArray[index]!=-1){
                numArray[i]=randomArray[index];
                randomArray[index]=-1;
                i++;
            }
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int j=0;j<num;j++)
            arrayList.add(numArray[j]);
        return arrayList;
    }

    public static boolean checkEverSaveSongListIsEmpty(String string){
        return  Saver.readSongList(string)==null;
    }
    /**用于检查是否存在本地文件playlist**/
    public static List<Song> initSongList(String songListFileName){
        if (!checkEverSaveSongListIsEmpty(songListFileName))
            return   (List<Song>) Saver.readSongList(songListFileName);
        else
            return AudioUtils.getSongs(MyApplication.getContext());
    }
    /**用于检查是否存在本地playPic目录是否完全空的, 不是则返回false**/
    public static boolean checkEverInitPicCacheIsEmpty(){
        final String Local_Cache_Path =
                MyApplication.getContext().getExternalFilesDir(null).toString() + "cece" + "PicCache";
        File dir = new File(Local_Cache_Path);
        if (dir.exists()&&dir.listFiles().length>0)
            return false;
        else
            return true;
    }
    /**
     * @deprecated  使用 {@link #initAlbumPicBackground(List)}
     * 用来检查歌曲是否有专辑图片, 有则setAlbumPicExist=true, 无则setAlbumPicExist=false, 顺便把id也设成0
     * 但是这个方法不好, 所以不用, 而且后来检查发现这个方法逻辑写反了**/
    public static void initAlbumId(List<Song> songList){
        MediaMetadataRetriever mediaMetadataRetriever =new MediaMetadataRetriever();
        for (int i=0;i<songList.size();i++){
            mediaMetadataRetriever.setDataSource(songList.get(i).getPath());
            if (mediaMetadataRetriever.getEmbeddedPicture()==null){
                songList.get(i).setAlbum_Picture_Id(0);
                //这里写反了,估计是后来在改下面的方法看成这个方法误加的
                songList.get(i).setAlbumPicExist(true);
            }
        }
    }

    /**
     * @deprecated
     * 用来检查歌曲是否有专辑图片, 有则setAlbumPicExist=true, 无则setAlbumPicExist=false, 顺便把id也设成0
     * 这个是能用的, 但现在弃用**/
    public static void initAlbumId2(List<Song> songList){
        MediaMetadataRetriever mediaMetadataRetriever =new MediaMetadataRetriever();
        for (int i=0;i<songList.size();i++){
            mediaMetadataRetriever.setDataSource(songList.get(i).getPath());
            if (mediaMetadataRetriever.getEmbeddedPicture()!=null)
                songList.get(i).setAlbumPicExist(true);
            else{
                songList.get(i).setAlbumPicExist(false);
                songList.get(i).setAlbum_Picture_Id(0);
            }

        }
    }

/**
 * @deprecated
 * * 这个方法根据id来判断专辑图片, 不够完美,弃用
 * **/
    public static void initPicCache(List<Song> songList){
        Bitmap bitmap ;
        for (int i=0;i<songList.size();i++){
            if (songList.get(i).getAlbum_Picture_Id()!=0){
                bitmap =bitmapTo128N( loadingCover(songList.get(i).getPath()) );
                Saver.setLocalCachePath(" "+songList.get(i).getAlbum_Picture_Id(), bitmap, 100);
            }
        }
    }
    /**
     * @deprecated
     * 这个方法根据id和exist来判断专辑图片, 完美,启用
     * 不对, 这个逻辑稍微混乱, 暂时弃用**/
    public static void initPicCache2(List<Song> songList){
        Bitmap bitmap ;
        for (int i=0;i<songList.size();i++){
            if (songList.get(i).getIsAlbumPicExist()){
                bitmap =bitmapTo128N( loadingCover(songList.get(i).getPath()) );
                Saver.setLocalCachePath(" "+songList.get(i).getAlbum_Picture_Id(), bitmap, 100);
            }
        }
    }
    /**
     * @deprecated  使用 {@link #initAlbumPicBackground(List)}
     * 弃用了
     * **/
    public static void  initAlbumPicCache(final List<Song> songList){
        final List<Song> songList_final =songList;
        if (checkEverInitPicCacheIsEmpty()){
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    initAlbumId(songList_final);
                    initPicCache(songList_final);
                }
            });
            thread.start();
        }
    }
/**
 * @deprecated  使用 {@link #bitmapTo128N(Bitmap)}
 * 自己写的压缩图片方法, 粗暴简单, 但是因为只有整数运算, 不可避免的把一部分内容去掉了, 不用了**/
    public static Bitmap bitmapTo128(Bitmap bitmap1){
        int width, height, ratio;
        ratio= bitmap1.getHeight()<bitmap1.getWidth()? bitmap1.getHeight()/128: bitmap1.getWidth()/128;
        if (ratio>6){
            Bitmap bitmap2=Bitmap.createBitmap(128,128,Bitmap.Config.ARGB_8888);
            bitmap2.setWidth(128);
            bitmap2.setHeight(128);
            for (int i=0;i<128; i=i+1) {
                for (int j = 0; j < 128; j=j+1) {
                    bitmap2.setPixel(i,j,  bitmap1.getPixel(i*ratio,j*ratio)  );
                }
            }
            return bitmap2;
        }
        return bitmap1;
    }
/**自带的压缩图片方法, 很强大**/
    public static Bitmap bitmapTo128N(Bitmap bitmap){
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) 128) / src_w;
        float scale_h = ((float) 128) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap,0,0,src_w,src_h,matrix,true);
        return dstbmp;
    }

/**
 * @deprecated  使用 {@link #initAlbumPicBackground(List)}
 * * (这个用了很久都没问题, 但我还是决定换成另一种方式, 所以可能之后的adapter不适应下面的方法了)
 * 先把所有歌曲都检测一遍, 有专辑封面的设置成true, 没有的设置成false
 * 然后再根据true和false来判断需不需要缓存专辑封面
 * 然后根据是否存在AlbumID来把sameAlbumList中第一个albumIdExist为true的song的albumID提取出来
 * 然后跟他同一个album的但是没有专辑封面的song的albumID都设置成跟他一样的albumID
 * 注意, 没有封面的song其albumExist依旧是false
 * **/
    public static void cache(final List<Song> songList, final List<SameStringIdList> sameAlbumList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                initAlbumId2(songList);
                initPicCache2(songList);

                for (int i = 0; i<sameAlbumList.size();i++){
                    for (int j = 0;j<sameAlbumList.get(i).getList().size();j++){
                        if (songList.get(  (int)sameAlbumList.get(i).getList().get(j)).getIsAlbumPicExist()  ){
                            for (int k=0;k<sameAlbumList.get(i).getList().size();k++){
                                if ( !songList.get((int)sameAlbumList.get(i).getList().get(k)).getIsAlbumPicExist() ){
                                    int albumId = songList.get(  (int)sameAlbumList.get(i).getList().get(j)).getAlbum_Picture_Id();
                                    songList.get(  (int)sameAlbumList.get(i).getList().get(k)    ).setAlbum_Picture_Id(albumId);
                                }
                            }
                            break;
                        }
                    }
                }
                Saver.saveSongList("firstList",songList);
            }
        }).start();
    }

    /**
     * 此方法只检查有没有专辑封面, 有则true, 无则false, 而且保留原有的albumId, albumId对应缓存的Id,
     * 此方法有质量好的专辑封面可能会被同名专辑的质量差的专辑图片给覆盖掉, 但现在优先用这个方法, 因为逻辑简单
     * **/

    public static void initAlbumPicBackground(final List<Song> songList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap ;
                byte[] picture;
                MediaMetadataRetriever mediaMetadataRetriever =new MediaMetadataRetriever();
                for (int i=0;i<songList.size();i++){
                    mediaMetadataRetriever.setDataSource(songList.get(i).getPath());
                    if (mediaMetadataRetriever.getEmbeddedPicture()!=null) {
                        songList.get(i).setAlbumPicExist(true);
                        picture = mediaMetadataRetriever.getEmbeddedPicture();
                        bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                        bitmap = bitmapTo128N(bitmap);
                        Saver.setLocalCachePath(" "+songList.get(i).getAlbum_Picture_Id(), bitmap, 100);
                    }
                    else{
                        songList.get(i).setAlbumPicExist(false);
                    }
                }
                Saver.exchangeSongList("firstList", songList);
            }
        }).start();
    }



    public static List<SameStringIdList> initDefaultPlaylist(List<Song> oriSongList,List<SameStringIdList> playlists ){
        if (playlists==null)
            playlists=new ArrayList<>();
        playlists.add(new SameStringIdList("Pendulum"));
        playlists.add(new SameStringIdList("Nightwish"));
        playlists.add(new SameStringIdList("The Ting Tings"));
        playlists.add(new SameStringIdList("Ellie Goulding"));
        playlists.add(new SameStringIdList("All Item"));
        for (int i = 0;i<oriSongList.size();i++) {
            if (oriSongList.get(i).getSinger().contains("Pendulum"))
                playlists.get(0).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("Nightwish"))
                playlists.get(1).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("The Ting Tings"))
                playlists.get(2).getList().add(i);
            else if (oriSongList.get(i).getSinger().contains("Ellie Goulding"))
                playlists.get(3).getList().add(i);
            playlists.get(4).getList().add(i);
        }
        return playlists;
    }

    public static  Bitmap blur(Bitmap oriBitmap){
        if (oriBitmap == null)
            return null;
        Bitmap bitmap = Bitmap.createBitmap(oriBitmap.getWidth(), oriBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(MyApplication.getContext());
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allocationIn = Allocation.createFromBitmap(rs,oriBitmap);
        Allocation allocationOut = Allocation.createFromBitmap(rs, oriBitmap);
        blurScript.setRadius(25.0f);
        blurScript.setInput(allocationIn);
        blurScript.forEach(allocationOut);
        allocationOut.copyTo(bitmap);
        //回收原来的空间
        oriBitmap.recycle();
        rs.destroy();
        return bitmap;
    }

    public static boolean stringDetect(String string){
        //String regEx = "[\\S]{1,20}";
        String regEx= "[^\\n\\f\\r\\v]{1,20}";
        return string.matches(regEx);
    }

    /***
     *
     * @param newFavListName 是新歌单的名字
     * @param newFavList 是新歌单的IdList
     * @param dataFileName 是本地总歌单playlist的文件名.
     *
     * 若新建歌单的名字本来就有, 那就会直接覆盖旧歌单, 这里以后再改成加入到旧歌单
     * */
    public static  void SaveNewFavListInDataFile(Context context, String newFavListName, List<Integer> newFavList, String dataFileName){
        if (newFavListName==null||newFavListName.isEmpty()||newFavList==null||newFavList.isEmpty()||dataFileName
                ==null||dataFileName.isEmpty())         return;
        //这里要做个字符检测, 不能输入非法字符
        if (!Player.stringDetect(newFavListName)) return;
        else {
            List<SameStringIdList> oriPlaylist = (List<SameStringIdList>) Saver.readData(dataFileName);
            if (oriPlaylist!=null && !oriPlaylist.isEmpty()){
                for (int i=0;i< oriPlaylist.size();i++){
                    if (newFavListName.equals(oriPlaylist.get(i).getSameString())){
                        int indexOfFavListNameInPlaylist= i;
                        oriPlaylist.set(indexOfFavListNameInPlaylist, new SameStringIdList(newFavListName, (ArrayList) newFavList));
                        Saver.saveData(dataFileName, oriPlaylist, false);
                        Toast.makeText(context,  "已存在此歌单, 新歌单将覆盖旧歌单: "+newFavListName,Toast.LENGTH_SHORT).show();
                        return;
                    }
                }//后期可以加上一下功能:
                // 如果循环做完了都找不到这个列表, 则新建一个列表
                Toast.makeText(context,  "没有找到相应的歌单, 将新建歌单: "+newFavListName,Toast.LENGTH_SHORT).show();
                oriPlaylist.add(new SameStringIdList(newFavListName, (ArrayList) newFavList));
                Saver.saveData(dataFileName, oriPlaylist, false);
            }
        }
    }
    /***
     * @param selectList 是选中的要加入playlist的SongIdList
     * @param oriFavList 被加入的SongIdList, 就是说
     *                   把selectList加入到oriFavList
     *                   返回一个加入的oriFavList
     * */

    public static List<Integer> theListWhichAddSelectSongIdInNewPlaylist(
            List<Integer> selectList, List<Integer> oriFavList){
        if (selectList==null||selectList.isEmpty()||oriFavList==null)
            return null;
        oriFavList.addAll(selectList);
        return oriFavList;
    }

    public static List<Integer> getInverselySelectList(Context context,List<Integer> selectList, List<Integer> currentEditList){
        if (selectList==null||currentEditList==null||currentEditList.isEmpty()){
            Toast.makeText(context, "selectList null || currentEditList null isEmpty", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (selectList.isEmpty()){
            return currentEditList ;
        }
        List<Integer> oriPlaylistQueueIdList = new ArrayList<>();
        for (int i=0; i<currentEditList.size();i++)
            oriPlaylistQueueIdList.add(currentEditList.get(i));
        for (int i=0; i<selectList.size(); i++){
            if (oriPlaylistQueueIdList.contains(selectList.get(i))){
                oriPlaylistQueueIdList.remove(selectList.get(i));
            }
        }
        return oriPlaylistQueueIdList;
    }

    public static void setInverselySelect(List<Integer> currentEditList, List<Integer> selectList
            , ListView listView, TextView tvListMode, EditPageAdapter adapter ){
        if (listView==null||selectList==null)
            return;
        List<Integer> trueList = new ArrayList<>();
        for (int j=0;j<listView.getCheckedItemPositions().size();j++)
            if (listView.getCheckedItemPositions().get(listView.getCheckedItemPositions().keyAt(j)))
                trueList.add( listView.getCheckedItemPositions().keyAt(j) );
        for (int j = 0; j<currentEditList.size(); j++){
            listView.setItemChecked(j, true);
        }
        for (int k = 0; k< trueList.size(); k++){
            listView.setItemChecked(trueList.get(k), false);
        }
        tvListMode.setText((currentEditList.size()- selectList.size())+"/"+currentEditList.size());
        adapter.notifyDataSetChanged();
    }

    public static List<Integer> getSelectSongIdList(ListView listView,List<Integer> currentEditList, List<Song> song_list){
        List<Integer> mSelectList = new ArrayList<>();
        if (listView==null)
            return null;
        if (currentEditList==null||currentEditList.isEmpty())
            return mSelectList;
        if (listView.getCheckedItemPositions()==null || listView.getCheckedItemPositions().size()==0)
            return mSelectList;
        for (int j=0;j<listView.getCheckedItemPositions().size();j++)
            if (listView.getCheckedItemPositions().get(listView.getCheckedItemPositions().keyAt(j)))
                //mSelectList.add(   playlistQueue.get(listView.getCheckedItemPositions().keyAt(j)).getId()   );
                mSelectList.add(  song_list.get(currentEditList.get(listView.getCheckedItemPositions().keyAt(j))).getId()   );

        if (mSelectList.isEmpty()){
            return mSelectList;
        }
        else{
            return mSelectList;
        }
    }

    public static void exchangeFromPlayList(int whichPositionFromPlaylist, List<Integer> newCurrentEditList){
        List<SameStringIdList> newList =(List<SameStringIdList>) Saver.readData("playlist");
        String playlistName = newList.get(whichPositionFromPlaylist).getSameString();
        SameStringIdList newSingleList = new SameStringIdList(playlistName, (ArrayList) newCurrentEditList);
        newList.set(whichPositionFromPlaylist, newSingleList );
        Saver.saveData("playlist", newList, false);
    }


    public static List<SameStringIdList> getSearchResultListFromOriListSongTitle(String string, List<Song> songList){
        List<SameStringIdList> resultList = new ArrayList<>();
        for (int i=0; i<songList.size(); i++){
            if (songList.get(i).getSong().contains(string)){
                ArrayList<Integer> resultIdList = new ArrayList<>();
                resultIdList.add(i);
                resultList.add(new SameStringIdList(songList.get(i).getSong(), resultIdList));
            }
        }
        return resultList;
    }

    public static List<SameStringIdList> getSearchResultListFromOriListAlbum(String string, List<SameStringIdList> albumIdLists){
        List<SameStringIdList> resultList = new ArrayList<>();
        for (int i=0; i<albumIdLists.size(); i++){
            if (albumIdLists.get(i).getSameString().contains(string)){
                resultList.add(new SameStringIdList(albumIdLists.get(i).getSameString(), albumIdLists.get(i).getList()));
            }
        }
        return resultList;
    }
    public static List<SameStringIdList> getSearchResultListFromOriListSinger(String string, List<SameStringIdList> singerIdLists){
        List<SameStringIdList> resultList = new ArrayList<>();
        for (int i=0; i<singerIdLists.size(); i++){
            if (singerIdLists.get(i).getSameString().contains(string)){
                resultList.add(new SameStringIdList(singerIdLists.get(i).getSameString(), singerIdLists.get(i).getList()));
            }
        }
        return resultList;
    }
}

/***
 * //
 * //    1、从资源中获取Bitmap
 * //    Resources res = getResources();
 * //    Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.icon);
 * //
 * //            2、Bitmap → byte[]
 * //            Java代码  收藏代码
 * //public byte[] Bitmap2Bytes(Bitmap bm) {
 * //        ByteArrayOutputStream baos = new ByteArrayOutputStream();
 * //        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
 * //        return baos.toByteArray();
 * //        }
 * //        3、byte[] → Bitmap
 * //        Java代码  收藏代码
 * //public Bitmap Bytes2Bimap(byte[] b) {
 * //        if (b.length != 0) {
 * //        return BitmapFactory.decodeByteArray(b, 0, b.length);
 * //        } else {
 * //        return null;
 * //        }
 * //        }
 **/
