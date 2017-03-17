package com.fanyu.boundless.view.theclass;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class PublishJobActivity$$ViewBinder<T extends PublishJobActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        target.edittittle = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.edittittle, "field 'edittittle'"), R.id.edittittle, "field 'edittittle'");
        target.editdescribe = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editdescribe, "field 'editdescribe'"), R.id.editdescribe, "field 'editdescribe'");
        target.zuoyeview = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.zuoyeview, "field 'zuoyeview'"), R.id.zuoyeview, "field 'zuoyeview'");
        View view = (View) finder.findRequiredView(source, R.id.xiugai, "field 'xiugai' and method 'onClick'");
        target.xiugai = (TextView) finder.castView(view, R.id.xiugai, "field 'xiugai'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.banji = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.banji, "field 'banji'"), R.id.banji, "field 'banji'");
        target.gvGridPickerView = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.gvGridPickerView, "field 'gvGridPickerView'"), R.id.gvGridPickerView, "field 'gvGridPickerView'");
        target.xiaozu = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.xiaozu, "field 'xiaozu'"), R.id.xiaozu, "field 'xiaozu'");
        target.zuGridPickerView = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.zuGridPickerView, "field 'zuGridPickerView'"), R.id.zuGridPickerView, "field 'zuGridPickerView'");
        target.xiaozulayout = (RelativeLayout) finder.castView((View) finder.findRequiredView(source, R.id.xiaozulayout, "field 'xiaozulayout'"), R.id.xiaozulayout, "field 'xiaozulayout'");
        target.geren = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.geren, "field 'geren'"), R.id.geren, "field 'geren'");
        target.gerenGridPickerView = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.gerenGridPickerView, "field 'gerenGridPickerView'"), R.id.gerenGridPickerView, "field 'gerenGridPickerView'");
        target.waibu = (LinearLayout) finder.castView((View) finder.findRequiredView(source, R.id.waibu, "field 'waibu'"), R.id.waibu, "field 'waibu'");
        target.banjilayout = (RelativeLayout) finder.castView((View) finder.findRequiredView(source, R.id.banjilayout, "field 'banjilayout'"), R.id.banjilayout, "field 'banjilayout'");
        target.biaoti = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.biaoti, "field 'biaoti'"), R.id.biaoti, "field 'biaoti'");
        target.gerenlayout = (RelativeLayout) finder.castView((View) finder.findRequiredView(source, R.id.gerenlayout, "field 'gerenlayout'"), R.id.gerenlayout, "field 'gerenlayout'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.fabu, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.messageTitle = null;
        target.edittittle = null;
        target.editdescribe = null;
        target.zuoyeview = null;
        target.xiugai = null;
        target.banji = null;
        target.gvGridPickerView = null;
        target.xiaozu = null;
        target.zuGridPickerView = null;
        target.xiaozulayout = null;
        target.geren = null;
        target.gerenGridPickerView = null;
        target.waibu = null;
        target.banjilayout = null;
        target.biaoti = null;
        target.gerenlayout = null;
    }
}
