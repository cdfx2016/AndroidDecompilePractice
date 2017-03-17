package com.fanyu.boundless.view.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class GetNoticeActivity$$ViewBinder<T extends GetNoticeActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        View view = (View) finder.findRequiredView(source, R.id.fasong, "field 'fasong' and method 'onClick'");
        target.fasong = (TextView) finder.castView(view, R.id.fasong, "field 'fasong'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick();
            }
        });
        target.shuoming = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.shuoming, "field 'shuoming'"), R.id.shuoming, "field 'shuoming'");
        target.editreason = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editreason, "field 'editreason'"), R.id.editreason, "field 'editreason'");
        target.tishi = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.tishi, "field 'tishi'"), R.id.tishi, "field 'tishi'");
        target.zuid = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.zuid, "field 'zuid'"), R.id.zuid, "field 'zuid'");
        target.xiaozu = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.xiaozu, "field 'xiaozu'"), R.id.xiaozu, "field 'xiaozu'");
        target.zuGridPickerView = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.zuGridPickerView, "field 'zuGridPickerView'"), R.id.zuGridPickerView, "field 'zuGridPickerView'");
        target.selectren = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.selectren, "field 'selectren'"), R.id.selectren, "field 'selectren'");
        target.gvGridPickerView = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.gvGridPickerView, "field 'gvGridPickerView'"), R.id.gvGridPickerView, "field 'gvGridPickerView'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onclick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onclick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.messageTitle = null;
        target.fasong = null;
        target.shuoming = null;
        target.editreason = null;
        target.tishi = null;
        target.zuid = null;
        target.xiaozu = null;
        target.zuGridPickerView = null;
        target.selectren = null;
        target.gvGridPickerView = null;
    }
}
