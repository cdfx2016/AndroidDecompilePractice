package com.fanyu.boundless.widget.imagepager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import com.fanyu.boundless.R;
import java.util.ArrayList;

public class ImagePagerActivity extends FragmentActivity {
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    private static final String STATE_POSITION = "STATE_POSITION";
    private HackyViewPager mPager;
    private int pagerPosition;

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        public ArrayList<String> fileList;

        public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        public int getCount() {
            return this.fileList == null ? 0 : this.fileList.size();
        }

        public Fragment getItem(int position) {
            return ImageDetailFragment.newInstance((String) this.fileList.get(position));
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);
        this.pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        ArrayList<String> urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        this.mPager = (HackyViewPager) findViewById(R.id.pager);
        this.mPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager(), urls));
        this.mPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrollStateChanged(int arg0) {
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageSelected(int arg0) {
            }
        });
        if (savedInstanceState != null) {
            this.pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        this.mPager.setCurrentItem(this.pagerPosition);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, this.mPager.getCurrentItem());
    }
}
