package com.fanyu.boundless.view.myself;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class AboutUsActivity$$ViewBinder<T extends AboutUsActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.banbenhao = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.banbenhao, "field 'banbenhao'"), R.id.banbenhao, "field 'banbenhao'");
        target.banben = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.banben, "field 'banben'"), R.id.banben, "field 'banben'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.rela_erweima, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.banbenhao = null;
        target.banben = null;
    }
}
