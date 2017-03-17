package com.fanyu.boundless.view.theclass;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.MyExpandableListView;

public class UpdateStudentActivity$$ViewBinder<T extends UpdateStudentActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.img_return, "field 'imgReturn' and method 'onClick'");
        target.imgReturn = (ImageView) finder.castView(view, R.id.img_return, "field 'imgReturn'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        view = (View) finder.findRequiredView(source, R.id.queding, "field 'queding' and method 'onClick'");
        target.queding = (TextView) finder.castView(view, R.id.queding, "field 'queding'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.gvGridPickerView = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.gvGridPickerView, "field 'gvGridPickerView'"), R.id.gvGridPickerView, "field 'gvGridPickerView'");
        target.zuGridPickerView = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.zuGridPickerView, "field 'zuGridPickerView'"), R.id.zuGridPickerView, "field 'zuGridPickerView'");
        target.expandlist = (MyExpandableListView) finder.castView((View) finder.findRequiredView(source, R.id.expandlist, "field 'expandlist'"), R.id.expandlist, "field 'expandlist'");
    }

    public void unbind(T target) {
        target.imgReturn = null;
        target.messageTitle = null;
        target.queding = null;
        target.gvGridPickerView = null;
        target.zuGridPickerView = null;
        target.expandlist = null;
    }
}
