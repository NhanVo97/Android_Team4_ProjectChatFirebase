package com.example.Chat365.Adapter;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ArrayList<String> listTitle = new ArrayList<>();
    private FragmentManager fragmentManager;
    private Map<Integer,String> mFragmentTags = new HashMap<Integer,String>();
    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentManager=fm;
    }
    @Override
    public Fragment getItem(int position) {
      return fragmentArrayList.get(position);
    }
    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return fragmentManager.findFragmentByTag(tag);
    }
    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
    public void addFragment(Fragment f, String title)
    {
        fragmentArrayList.add(f);
        listTitle.add(title);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTitle.get(position);
    }
}
