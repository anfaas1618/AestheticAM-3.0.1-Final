package com.mibtech.aesthetic_am.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mibtech.aesthetic_am.fragment.SliderItemFragment;

public class SliderPagerAdapter extends FragmentPagerAdapter {
    public SliderPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return SliderItemFragment.newInstance(position);
    }

    // size is hardcoded
    @Override
    public int getCount() {
        return 3;
    }
}