package com.fanyu.boundless.view.home;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import java.util.List;

public class ImageLoopAdapter extends LoopPagerAdapter {
    private List<Integer> imagelist;

    public ImageLoopAdapter(RollPagerView viewPager, List<Integer> imagelist) {
        super(viewPager);
        this.imagelist = imagelist;
    }

    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setScaleType(ScaleType.CENTER_CROP);
        view.setLayoutParams(new LayoutParams(-1, -1));
        view.setImageResource(((Integer) this.imagelist.get(position)).intValue());
        return view;
    }

    public int getRealCount() {
        return this.imagelist.size();
    }
}
