package com.fanyu.boundless.view.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;

public class ArriveOrLeaveSchoolActivity$$ViewBinder<T extends ArriveOrLeaveSchoolActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.img_return, "field 'imgReturn' and method 'onClick'");
        target.imgReturn = (ImageView) finder.castView(view, R.id.img_return, "field 'imgReturn'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        view = (View) finder.findRequiredView(source, R.id.yijianget, "field 'yijianget' and method 'onClick'");
        target.yijianget = (LinearLayout) finder.castView(view, R.id.yijianget, "field 'yijianget'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        view = (View) finder.findRequiredView(source, R.id.yijianleave, "field 'yijianleave' and method 'onClick'");
        target.yijianleave = (LinearLayout) finder.castView(view, R.id.yijianleave, "field 'yijianleave'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        view = (View) finder.findRequiredView(source, R.id.getnotice, "field 'getnotice' and method 'onClick'");
        target.getnotice = (LinearLayout) finder.castView(view, R.id.getnotice, "field 'getnotice'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        view = (View) finder.findRequiredView(source, R.id.leavenotice, "field 'leavenotice' and method 'onClick'");
        target.leavenotice = (LinearLayout) finder.castView(view, R.id.leavenotice, "field 'leavenotice'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.addget = (LinearLayout) finder.castView((View) finder.findRequiredView(source, R.id.addget, "field 'addget'"), R.id.addget, "field 'addget'");
        target.classname = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classname, "field 'classname'"), R.id.classname, "field 'classname'");
        view = (View) finder.findRequiredView(source, R.id.selectclass, "field 'selectclass' and method 'onClick'");
        target.selectclass = (RelativeLayout) finder.castView(view, R.id.selectclass, "field 'selectclass'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.listview = (PullLoadMoreRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.listview, "field 'listview'"), R.id.listview, "field 'listview'");
    }

    public void unbind(T target) {
        target.imgReturn = null;
        target.messageTitle = null;
        target.yijianget = null;
        target.yijianleave = null;
        target.getnotice = null;
        target.leavenotice = null;
        target.addget = null;
        target.classname = null;
        target.selectclass = null;
        target.listview = null;
    }
}
