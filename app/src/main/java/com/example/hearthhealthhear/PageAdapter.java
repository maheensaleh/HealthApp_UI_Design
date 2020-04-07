package com.example.hearthhealthhear;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private int numoftabs ;

    public PageAdapter(FragmentManager fm, int numoftabs) {
        super(fm);
        this.numoftabs = numoftabs;
    }

    @Override
    public Fragment getItem(int position) {

        //here we write about our fragments/tabs

        switch (position){

            case 0:
                return new tab1();
            case 1:
                return new tab2();
            default:
                return null;
        }

    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return numoftabs;
    }
}
