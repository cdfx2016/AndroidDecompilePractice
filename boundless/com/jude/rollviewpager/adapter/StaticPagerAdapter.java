package com.jude.rollviewpager.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class StaticPagerAdapter extends PagerAdapter {
    private ArrayList<View> mViewList = new ArrayList();

    public abstract View getView(ViewGroup viewGroup, int i);

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void notifyDataSetChanged() {
        this.mViewList.clear();
        super.notifyDataSetChanged();
    }

    public int getItemPosition(Object object) {
        return -2;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = findViewByPosition(container, position);
        container.addView(itemView);
        onBind(itemView, position);
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

    public void onBind(View view, int position) {
    }
}
