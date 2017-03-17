package com.fanyu.boundless.view.main;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.CustomViewPager;

public class MainAcitivity$$ViewBinder<T extends MainAcitivity> implements ViewBinder<T> {
    public void bind(Finder finder, T target, Object source) {
        target.mainTabGroup = (RadioGroup) finder.castView((View) finder.findRequiredView(source, R.id.rg_tab, "field 'mainTabGroup'"), R.id.rg_tab, "field 'mainTabGroup'");
        target.vpViewpage = (CustomViewPager) finder.castView((View) finder.findRequiredView(source, R.id.vp_viewpage, "field 'vpViewpage'"), R.id.vp_viewpage, "field 'vpViewpage'");
        target.homePaopao = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.home_paopao, "field 'homePaopao'"), R.id.home_paopao, "field 'homePaopao'");
        target.classPaopao = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.class_paopao, "field 'classPaopao'"), R.id.class_paopao, "field 'classPaopao'");
    }

    public void unbind(T target) {
        target.mainTabGroup = null;
        target.vpViewpage = null;
        target.homePaopao = null;
        target.classPaopao = null;
    }
}
