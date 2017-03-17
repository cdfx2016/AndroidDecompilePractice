package com.fanyu.boundless.view.theclass;

import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.TagView;

public class JoinTeacherActivity$$ViewBinder<T extends JoinTeacherActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.tagview = (TagView) finder.castView((View) finder.findRequiredView(source, R.id.tagview, "field 'tagview'"), R.id.tagview, "field 'tagview'");
        target.editname = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editname, "field 'editname'"), R.id.editname, "field 'editname'");
        target.editbeizhu = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.editbeizhu, "field 'editbeizhu'"), R.id.editbeizhu, "field 'editbeizhu'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.submit, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.tagview = null;
        target.editname = null;
        target.editbeizhu = null;
    }
}
