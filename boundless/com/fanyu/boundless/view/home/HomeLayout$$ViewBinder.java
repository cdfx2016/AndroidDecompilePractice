package com.fanyu.boundless.view.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.jude.rollviewpager.RollPagerView;

public class HomeLayout$$ViewBinder<T extends HomeLayout> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.fabu, "field 'fabu' and method 'onClick'");
        target.fabu = (LinearLayout) finder.castView(view, R.id.fabu, "field 'fabu'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.zuoyecount = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.zuoyecount, "field 'zuoyecount'"), R.id.zuoyecount, "field 'zuoyecount'");
        target.huodongcount = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.huodongcount, "field 'huodongcount'"), R.id.huodongcount, "field 'huodongcount'");
        target.noticecount = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.noticecount, "field 'noticecount'"), R.id.noticecount, "field 'noticecount'");
        target.count = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.count, "field 'count'"), R.id.count, "field 'count'");
        target.getleavecount = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.getleavecount, "field 'getleavecount'"), R.id.getleavecount, "field 'getleavecount'");
        target.viewpager = (RollPagerView) finder.castView((View) finder.findRequiredView(source, R.id.viewpager, "field 'viewpager'"), R.id.viewpager, "field 'viewpager'");
        target.gridView = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.gridView, "field 'gridView'"), R.id.gridView, "field 'gridView'");
        ((View) finder.findRequiredView(source, R.id.ll_btn_zuoye, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.ll_btn_huodong, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.ll_btn_daoli, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.ll_btn_tongzhi, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.ll_btn_dongtai, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.shuzi, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.yingyu, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.fabu = null;
        target.zuoyecount = null;
        target.huodongcount = null;
        target.noticecount = null;
        target.count = null;
        target.getleavecount = null;
        target.viewpager = null;
        target.gridView = null;
    }
}
