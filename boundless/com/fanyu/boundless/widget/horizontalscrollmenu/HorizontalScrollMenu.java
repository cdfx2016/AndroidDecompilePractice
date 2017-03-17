package com.fanyu.boundless.widget.horizontalscrollmenu;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.fanyu.boundless.R;
import java.util.ArrayList;
import java.util.List;

public class HorizontalScrollMenu extends LinearLayout {
    private HorizontalScrollView hsv_menu;
    private BaseAdapter mAdapter;
    private int mBackgroundResId;
    private Context mContext;
    private OnCheckedChangeListener mItemListener;
    private List<String> mItems;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private OnPageChangeListener mPageListener;
    private List<View> mPagers;
    private boolean mSwiped;
    private boolean[] mVisitStatus;
    private List<RadioButton> rb_items;
    private RadioGroup rg_items;
    private MyViewPager vp_content;

    static class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mViews;

        public MyViewPagerAdapter(List<View> views) {
            this.mViews = views;
        }

        public int getCount() {
            return this.mViews.size();
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) this.mViews.get(position));
        }

        public Object instantiateItem(ViewGroup container, int position) {
            container.addView((View) this.mViews.get(position));
            return this.mViews.get(position);
        }
    }

    public HorizontalScrollMenu(Context context) {
        this(context, null);
    }

    public HorizontalScrollMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.rb_items = new ArrayList();
        this.mPaddingLeft = 40;
        this.mPaddingTop = 20;
        this.mPaddingRight = 40;
        this.mPaddingBottom = 20;
        this.mSwiped = true;
        this.mItemListener = new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton btn = (RadioButton) group.findViewById(checkedId);
                HorizontalScrollMenu.this.setMenuItemsNullBackground();
                btn.setBackgroundResource(HorizontalScrollMenu.this.mBackgroundResId);
                btn.setPadding(HorizontalScrollMenu.this.mPaddingLeft, HorizontalScrollMenu.this.mPaddingTop, HorizontalScrollMenu.this.mPaddingRight, HorizontalScrollMenu.this.mPaddingBottom);
                int position = 0;
                for (int i = 0; i < HorizontalScrollMenu.this.rb_items.size(); i++) {
                    if (HorizontalScrollMenu.this.rb_items.get(i) == btn) {
                        position = i;
                    }
                }
                HorizontalScrollMenu.this.vp_content.setCurrentItem(position, HorizontalScrollMenu.this.mSwiped);
                HorizontalScrollMenu.this.moveItemToCenter(btn);
                HorizontalScrollMenu.this.mAdapter.onPageChanged(position, HorizontalScrollMenu.this.mVisitStatus[position]);
                HorizontalScrollMenu.this.mVisitStatus[position] = true;
            }
        };
        this.mPageListener = new OnPageChangeListener() {
            public void onPageSelected(int arg0) {
                ((RadioButton) HorizontalScrollMenu.this.rb_items.get(arg0)).setChecked(true);
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        };
        this.mContext = context;
        View v = LayoutInflater.from(context).inflate(R.layout.horizontal_scroll_menu, this, true);
        this.rg_items = (RadioGroup) v.findViewById(R.id.rg_items);
        this.vp_content = (MyViewPager) v.findViewById(R.id.vp_content);
        this.hsv_menu = (HorizontalScrollView) v.findViewById(R.id.hsv_menu);
        this.mBackgroundResId = R.drawable.tiao;
    }

    public void setAdapter(BaseAdapter adapter) {
        if (adapter != null) {
            adapter.setHorizontalScrollMenu(this);
            this.mAdapter = adapter;
            initView(adapter);
        }
    }

    private void initView(BaseAdapter adapter) {
        if (adapter != null) {
            this.mItems = this.mAdapter.getMenuItems();
            this.mVisitStatus = new boolean[this.mItems.size()];
            initMenuItems(this.mItems);
            this.mPagers = this.mAdapter.getContentViews();
            initContentViews(this.mPagers);
        }
    }

    public void notifyDataSetChanged(BaseAdapter adapter) {
        this.rg_items.removeAllViews();
        this.rb_items.clear();
        initView(adapter);
    }

    private void initMenuItems(List<String> items) {
        if (items != null && items.size() != 0) {
            this.rg_items.setOnCheckedChangeListener(this.mItemListener);
            for (String str : items) {
                RadioButton rb_item = (RadioButton) LayoutInflater.from(this.mContext).inflate(R.layout.menu_item, null);
                rb_item.setText(str);
                rb_item.setGravity(17);
                rb_item.setPadding(this.mPaddingLeft, this.mPaddingTop, this.mPaddingRight, this.mPaddingBottom);
                this.rg_items.addView(rb_item);
                this.rb_items.add(rb_item);
            }
            ((RadioButton) this.rb_items.get(0)).setChecked(true);
        }
    }

    private void initContentViews(List<View> contentViews) {
        if (contentViews != null && contentViews.size() != 0) {
            this.vp_content.setAdapter(new MyViewPagerAdapter(contentViews));
            this.vp_content.setOnPageChangeListener(this.mPageListener);
        }
    }

    public void setCheckedBackground(int resId) {
        this.mBackgroundResId = resId;
    }

    private void moveItemToCenter(RadioButton rb) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int[] locations = new int[2];
        rb.getLocationInWindow(locations);
        this.hsv_menu.smoothScrollBy((locations[0] + (rb.getWidth() / 2)) - (screenWidth / 2), 0);
    }

    private void setMenuItemsNullBackground() {
        if (this.rg_items != null) {
            for (int i = 0; i < this.rg_items.getChildCount(); i++) {
                this.rg_items.getChildAt(i).setBackgroundResource(17170445);
            }
        }
    }

    public void setMenuItemPaddingLeft(int paddingLeft) {
        this.mPaddingLeft = paddingLeft;
    }

    public void setMenuItemPaddingTop(int paddingTop) {
        this.mPaddingTop = paddingTop;
    }

    public void setMenuItemPaddingRight(int paddingRight) {
        this.mPaddingRight = paddingRight;
    }

    public void setMenuItemPaddingBottom(int paddingBottom) {
        this.mPaddingBottom = paddingBottom;
    }

    public void setSwiped(boolean swiped) {
        this.mSwiped = swiped;
        this.vp_content.setSwiped(swiped);
    }
}
