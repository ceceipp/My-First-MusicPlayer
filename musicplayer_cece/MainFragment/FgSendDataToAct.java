package com.lc.musicplayer.MainFragment;

import android.widget.ListView;

import java.util.List;

public interface FgSendDataToAct {
    public void sendSameString(String string, int sameStringItemsCount);
    public void sendSingleList(List<Integer> singleList, String sameString);
    public void sendPlayerListAndItem(List<Integer> singleList, int position);
    public void sendSameStringListListView(ListView listView);
    public void sendSingleListListView(ListView listView);
    public void sendItemPositionAndFromTop(int position, int fromTop);
    public void sendPlaylistClickPosition(int position);
}
