package cn.finalteam.toolsfinal.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragmentList;
    private List<String> mTabList;

    public FragmentAdapter(FragmentManager fm, List<Fragment> list) {
        this(fm, list, null);
    }

    public FragmentAdapter(FragmentManager fm, List<Fragment> list, List<String> tabList) {
        super(fm);
        this.mFragmentList = list;
        this.mTabList = tabList;
    }

    public Fragment getItem(int position) {
        return (Fragment) this.mFragmentList.get(position);
    }

    public int getCount() {
        return this.mFragmentList.size();
    }

    public CharSequence getPageTitle(int position) {
        return (String) this.mTabList.get(position);
    }
}
