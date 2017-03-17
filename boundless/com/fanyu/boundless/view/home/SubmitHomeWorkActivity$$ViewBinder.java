package com.fanyu.boundless.view.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.jude.easyrecyclerview.EasyRecyclerView;

public class SubmitHomeWorkActivity$$ViewBinder<T extends SubmitHomeWorkActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.wancheng, "field 'wancheng' and method 'onClick'");
        target.wancheng = (TextView) finder.castView(view, R.id.wancheng, "field 'wancheng'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.recyclerView = (EasyRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.recyclerView, "field 'recyclerView'"), R.id.recyclerView, "field 'recyclerView'");
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        target.quxiaoMore = (LinearLayout) finder.castView((View) finder.findRequiredView(source, R.id.quxiao_more, "field 'quxiaoMore'"), R.id.quxiao_more, "field 'quxiaoMore'");
        target.framelayout = (RelativeLayout) finder.castView((View) finder.findRequiredView(source, R.id.framelayout, "field 'framelayout'"), R.id.framelayout, "field 'framelayout'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.wancheng = null;
        target.recyclerView = null;
        target.messageTitle = null;
        target.quxiaoMore = null;
        target.framelayout = null;
    }
}
