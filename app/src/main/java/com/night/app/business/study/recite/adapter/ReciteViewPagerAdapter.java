package com.night.app.business.study.recite.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.night.app.business.study.recite.fragment.ReciteFragment;
import com.night.model.wrapper.recite.ReciteWordWrapper;

import java.util.List;

public class ReciteViewPagerAdapter extends FragmentStatePagerAdapter {
    private ReciteFragment    mFragment = new ReciteFragment();

    private List<ReciteWordWrapper> mWrapperList;

    public ReciteViewPagerAdapter(FragmentManager fm, List<ReciteWordWrapper> list) {
        super(fm);
        mWrapperList = list;
    }

    @Override
    public Fragment getItem(int position) {
        mFragment = new ReciteFragment();
        mFragment.setReciteWordWrapper(mWrapperList.get(position));
        return mFragment;
    }

    @Override
    public int getCount() {
        if (mWrapperList != null)
            return mWrapperList.size();
        return 0;
    }





}
