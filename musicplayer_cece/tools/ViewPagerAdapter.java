package com.lc.musicplayer.tools;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;


import com.lc.musicplayer.fragment.AlbumFragment;
import com.lc.musicplayer.fragment.PathFragment;
import com.lc.musicplayer.fragment.PlaylistFragment;
import com.lc.musicplayer.fragment.SameStringSongsFragment;
import com.lc.musicplayer.fragment.SameStringSongsFragment_Edit;
import com.lc.musicplayer.fragment.SingerFragment;

import java.util.ArrayList;
import java.util.List;
//原来是extends FragmentPagerAdapter的, 代码一点都没变
public class ViewPagerAdapter extends FragmentStatePagerAdapter  {
    private List<String> titleLists ;
    private PlaylistFragment playlistFragment;
    private AlbumFragment  albumFragment;
    private SingerFragment singerFragment;
    private PathFragment pathFragment;
    private SameStringSongsFragment sameSingleFragment;
    private SameStringSongsFragment_Edit sameStringSongsFragment_edit;

    public  ViewPagerAdapter(FragmentManager fragmentManager ,
             List<String> titleLists){
        super(fragmentManager);
        playlistFragment = PlaylistFragment.newInstance();
        albumFragment = AlbumFragment.newInstance();
        singerFragment = SingerFragment.newInstance();
        pathFragment = PathFragment.newInstance();
        sameSingleFragment= SameStringSongsFragment.newInstance();
        sameStringSongsFragment_edit=SameStringSongsFragment_Edit.newInstance();
        this.titleLists = titleLists;
    }

    @Override
    public int getCount(){
        return (titleLists==null) ? 0:titleLists.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        return  super.instantiateItem(container,position);
    }

    @Override
    public Fragment getItem(int position){
        Fragment fragment = null;
        switch (position){
            case 0:{    fragment = playlistFragment; break;                          }
            case 1:{    fragment = albumFragment;break;                              }
            case 2:{    fragment = singerFragment;break;                             }
            case 3:{    fragment = pathFragment;break;                                }
            case 4:{    fragment =sameSingleFragment;break;                      }
            case 5:{    fragment =sameStringSongsFragment_edit ;break;  }
            default:break;
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        super.destroyItem(container,position,object);
    }

    @Override
    public CharSequence getPageTitle(int position){
        return (titleLists==null && position<titleLists.size() )
                ? null : titleLists.get(position);
    }
}
