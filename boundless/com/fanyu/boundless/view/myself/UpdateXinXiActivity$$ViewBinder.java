package com.fanyu.boundless.view.myself;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;

public class UpdateXinXiActivity$$ViewBinder<T extends UpdateXinXiActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.touxiang, "field 'touxiang' and method 'onClick'");
        target.touxiang = (ImageView) finder.castView(view, R.id.touxiang, "field 'touxiang'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.txtLoginname = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.txt_loginname, "field 'txtLoginname'"), R.id.txt_loginname, "field 'txtLoginname'");
        target.txtUsername = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.txt_username, "field 'txtUsername'"), R.id.txt_username, "field 'txtUsername'");
        target.txtUsersex = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.txt_usersex, "field 'txtUsersex'"), R.id.txt_usersex, "field 'txtUsersex'");
        target.txtUserage = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.txt_userage, "field 'txtUserage'"), R.id.txt_userage, "field 'txtUserage'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.rl_usersex, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.rl_userage, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.rl_userpwd, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        ((View) finder.findRequiredView(source, R.id.rl_username, "method 'onClick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.touxiang = null;
        target.txtLoginname = null;
        target.txtUsername = null;
        target.txtUsersex = null;
        target.txtUserage = null;
    }
}
