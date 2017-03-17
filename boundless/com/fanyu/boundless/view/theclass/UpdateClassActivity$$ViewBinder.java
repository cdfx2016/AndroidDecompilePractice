package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class UpdateClassActivity$$ViewBinder<T extends UpdateClassActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        View view = (View) finder.findRequiredView(source, R.id.classimg, "field 'classimg' and method 'onClick'");
        target.classimg = (ImageView) finder.castView(view, R.id.classimg, "field 'classimg'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.classnumber = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classnumber, "field 'classnumber'"), R.id.classnumber, "field 'classnumber'");
        target.classname = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.classname, "field 'classname'"), R.id.classname, "field 'classname'");
        target.schoolname = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.schoolname, "field 'schoolname'"), R.id.schoolname, "field 'schoolname'");
        target.classgrade = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classgrade, "field 'classgrade'"), R.id.classgrade, "field 'classgrade'");
        target.grade = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.grade, "field 'grade'"), R.id.grade, "field 'grade'");
        target.ban = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.ban, "field 'ban'"), R.id.ban, "field 'ban'");
        view = (View) finder.findRequiredView(source, R.id.selectgrade, "field 'selectgrade' and method 'onClick'");
        target.selectgrade = (RelativeLayout) finder.castView(view, R.id.selectgrade, "field 'selectgrade'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        view = (View) finder.findRequiredView(source, R.id.selectban, "field 'selectban' and method 'onClick'");
        target.selectban = (RelativeLayout) finder.castView(view, R.id.selectban, "field 'selectban'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.wancheng, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.messageTitle = null;
        target.classimg = null;
        target.classnumber = null;
        target.classname = null;
        target.schoolname = null;
        target.classgrade = null;
        target.grade = null;
        target.ban = null;
        target.selectgrade = null;
        target.selectban = null;
    }
}
