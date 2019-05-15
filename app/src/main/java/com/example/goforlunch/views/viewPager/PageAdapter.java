package com.example.goforlunch.views.viewPager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.goforlunch.controler.fragments.ChatFragment;
import com.example.goforlunch.controler.fragments.MapFragment;
import com.example.goforlunch.controler.fragments.RestoListFragment;
import com.example.goforlunch.controler.fragments.WorkmatesFragment;

public class PageAdapter extends FragmentPagerAdapter {
    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MapFragment();
            case 1:
                return new RestoListFragment();
            case 2:
                return new WorkmatesFragment();
            case 3:
                return new ChatFragment();
                default:return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Map";
            case 1:
                return "List";
            case 2:
                return "Workers";
            case 3:
                return "Chat";

            default:
                return null;
        }
    }
}
