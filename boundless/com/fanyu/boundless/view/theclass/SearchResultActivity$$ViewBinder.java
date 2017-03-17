package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class SearchResultActivity$$ViewBinder<T extends SearchResultActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.classimg = (ImageView) finder.castView((View) finder.findRequiredView(source, R.id.classimg, "field 'classimg'"), R.id.classimg, "field 'classimg'");
        target.erweima = (ImageView) finder.castView((View) finder.findRequiredView(source, R.id.erweima, "field 'erweima'"), R.id.erweima, "field 'erweima'");
        target.classnumber = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classnumber, "field 'classnumber'"), R.id.classnumber, "field 'classnumber'");
        target.classname = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classname, "field 'classname'"), R.id.classname, "field 'classname'");
        target.classboss = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classboss, "field 'classboss'"), R.id.classboss, "field 'classboss'");
        target.schoolView = (View) finder.findRequiredView(source, R.id.school_view, "field 'schoolView'");
        target.schoolname = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.schoolname, "field 'schoolname'"), R.id.schoolname, "field 'schoolname'");
        target.schoolLayout = (RelativeLayout) finder.castView((View) finder.findRequiredView(source, R.id.school_layout, "field 'schoolLayout'"), R.id.school_layout, "field 'schoolLayout'");
        target.gradeView = (View) finder.findRequiredView(source, R.id.grade_view, "field 'gradeView'");
        target.classgrade = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.classgrade, "field 'classgrade'"), R.id.classgrade, "field 'classgrade'");
        target.classgradeLayout = (RelativeLayout) finder.castView((View) finder.findRequiredView(source, R.id.classgrade_layout, "field 'classgradeLayout'"), R.id.classgrade_layout, "field 'classgradeLayout'");
        target.teachercount = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.teachercount, "field 'teachercount'"), R.id.teachercount, "field 'teachercount'");
        target.stunumber = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.stunumber, "field 'stunumber'"), R.id.stunumber, "field 'stunumber'");
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.addclass, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.classimg = null;
        target.erweima = null;
        target.classnumber = null;
        target.classname = null;
        target.classboss = null;
        target.schoolView = null;
        target.schoolname = null;
        target.schoolLayout = null;
        target.gradeView = null;
        target.classgrade = null;
        target.classgradeLayout = null;
        target.teachercount = null;
        target.stunumber = null;
        target.messageTitle = null;
    }
}
