package com.jude.rollviewpager.adapter;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import com.jude.rollviewpager.HintView;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.RollPagerView.HintViewDelegate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class LoopPagerAdapter extends PagerAdapter {
    private ArrayList<View> mViewList = new ArrayList();
    private RollPagerView mViewPager;

    private class LoopHintViewDelegate implements HintViewDelegate {
        private LoopHintViewDelegate() {
        }

        public void setCurrentPosition(int position, HintView hintView) {
            if (hintView != null && LoopPagerAdapter.this.getRealCount() > 0) {
                hintView.setCurrent(position % LoopPagerAdapter.this.getRealCount());
            }
        }

        public void initView(int length, int gravity, HintView hintView) {
            if (hintView != null) {
                hintView.initView(LoopPagerAdapter.this.getRealCount(), gravity);
            }
        }
    }

    public abstract int getRealCount();

    public abstract View getView(ViewGroup viewGroup, int i);

    public void notifyDataSetChanged() {
        this.mViewList.clear();
        initPosition();
        super.notifyDataSetChanged();
    }

    public int getItemPosition(Object object) {
        return -2;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        initPosition();
    }

    private void initPosition() {
        if (this.mViewPager.getViewPager().getCurrentItem() == 0 && getRealCount() > 0) {
            setCurrent(1073741823 - (1073741823 % getRealCount()));
        }
    }

    private void setCurrent(int index) {
        try {
            Field field = ViewPager.class.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.set(this.mViewPager.getViewPager(), Integer.valueOf(index));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }

    public LoopPagerAdapter(RollPagerView viewPager) {
        this.mViewPager = viewPager;
        viewPager.setHintViewDelegate(new LoopHintViewDelegate());
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = findViewByPosition(container, position % getRealCount());
        container.addView(itemView);
        return itemView;
    }

    private View findViewByPosition(ViewGroup container, int position) {
        View view;
        Iterator it = this.mViewList.iterator();
        while (it.hasNext()) {
            view = (View) it.next();
            if (((Integer) view.getTag()).intValue() == position && view.getParent() == null) {
                return view;
            }
        }
        view = getView(container, position);
        view.setTag(Integer.valueOf(position));
        this.mViewList.add(view);
        return view;
    }

    @Deprecated
    public final int getCount() {
        return getRealCount() <= 0 ? getRealCount() : ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    }
}
