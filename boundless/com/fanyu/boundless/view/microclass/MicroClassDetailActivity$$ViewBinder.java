package com.fanyu.boundless.view.microclass;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.HVideoPlayer;

public class MicroClassDetailActivity$$ViewBinder<T extends MicroClassDetailActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.img_return, "field 'imgReturn' and method 'onClick'");
        target.imgReturn = (ImageView) finder.castView(view, R.id.img_return, "field 'imgReturn'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        target.surfaceView = (HVideoPlayer) finder.castView((View) finder.findRequiredView(source, R.id.surface_view, "field 'surfaceView'"), R.id.surface_view, "field 'surfaceView'");
        target.showWatchs = (ImageView) finder.castView((View) finder.findRequiredView(source, R.id.show_watchs, "field 'showWatchs'"), R.id.show_watchs, "field 'showWatchs'");
        target.showWatchnums = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.show_watchnums, "field 'showWatchnums'"), R.id.show_watchnums, "field 'showWatchnums'");
        view = (View) finder.findRequiredView(source, R.id.show_dianzan_img, "field 'showDianzanImg' and method 'onClick'");
        target.showDianzanImg = (TextView) finder.castView(view, R.id.show_dianzan_img, "field 'showDianzanImg'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.showDianzannums = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.show_dianzannums, "field 'showDianzannums'"), R.id.show_dianzannums, "field 'showDianzannums'");
        view = (View) finder.findRequiredView(source, R.id.show_cai_img, "field 'showCaiImg' and method 'onClick'");
        target.showCaiImg = (TextView) finder.castView(view, R.id.show_cai_img, "field 'showCaiImg'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.showCainums = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.show_cainums, "field 'showCainums'"), R.id.show_cainums, "field 'showCainums'");
        target.showPinglunImg = (ImageView) finder.castView((View) finder.findRequiredView(source, R.id.show_pinglun_img, "field 'showPinglunImg'"), R.id.show_pinglun_img, "field 'showPinglunImg'");
        target.showPinglunnums = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.show_pinglunnums, "field 'showPinglunnums'"), R.id.show_pinglunnums, "field 'showPinglunnums'");
        view = (View) finder.findRequiredView(source, R.id.llayout_show_pinglun_img, "field 'llayoutShowPinglunImg' and method 'onClick'");
        target.llayoutShowPinglunImg = (LinearLayout) finder.castView(view, R.id.llayout_show_pinglun_img, "field 'llayoutShowPinglunImg'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.showShipinjianjieTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.show_shipinjianjie_title, "field 'showShipinjianjieTitle'"), R.id.show_shipinjianjie_title, "field 'showShipinjianjieTitle'");
        target.showShipinjianjie = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.show_shipinjianjie, "field 'showShipinjianjie'"), R.id.show_shipinjianjie, "field 'showShipinjianjie'");
        target.zankai = (ImageView) finder.castView((View) finder.findRequiredView(source, R.id.zankai, "field 'zankai'"), R.id.zankai, "field 'zankai'");
        target.kaile = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.kaile, "field 'kaile'"), R.id.kaile, "field 'kaile'");
        view = (View) finder.findRequiredView(source, R.id.zhankai, "field 'zhankai' and method 'onClick'");
        target.zhankai = (LinearLayout) finder.castView(view, R.id.zhankai, "field 'zhankai'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.mylistivew = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.mylistivew, "field 'mylistivew'"), R.id.mylistivew, "field 'mylistivew'");
        target.commitnum = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.commitnum, "field 'commitnum'"), R.id.commitnum, "field 'commitnum'");
        view = (View) finder.findRequiredView(source, R.id.pinglun, "field 'pinglun' and method 'onClick'");
        target.pinglun = (TextView) finder.castView(view, R.id.pinglun, "field 'pinglun'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.llPinglunContent = (LinearLayout) finder.castView((View) finder.findRequiredView(source, R.id.ll_pinglunContent, "field 'llPinglunContent'"), R.id.ll_pinglunContent, "field 'llPinglunContent'");
        target.pllistivew = (RecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.pllistivew, "field 'pllistivew'"), R.id.pllistivew, "field 'pllistivew'");
        view = (View) finder.findRequiredView(source, R.id.clickmore, "field 'clickmore' and method 'onClick'");
        target.clickmore = (TextView) finder.castView(view, R.id.clickmore, "field 'clickmore'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.rlDd = (LinearLayout) finder.castView((View) finder.findRequiredView(source, R.id.rl_dd, "field 'rlDd'"), R.id.rl_dd, "field 'rlDd'");
        target.neirong = (LinearLayout) finder.castView((View) finder.findRequiredView(source, R.id.neirong, "field 'neirong'"), R.id.neirong, "field 'neirong'");
        target.svVideoscrollview = (ScrollView) finder.castView((View) finder.findRequiredView(source, R.id.sv_videoscrollview, "field 'svVideoscrollview'"), R.id.sv_videoscrollview, "field 'svVideoscrollview'");
        view = (View) finder.findRequiredView(source, R.id.continue_play, "field 'continuePlay' and method 'onClick'");
        target.continuePlay = (TextView) finder.castView(view, R.id.continue_play, "field 'continuePlay'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onClick(p0);
            }
        });
        target.llWifi = (LinearLayout) finder.castView((View) finder.findRequiredView(source, R.id.ll_wifi, "field 'llWifi'"), R.id.ll_wifi, "field 'llWifi'");
    }

    public void unbind(T target) {
        target.imgReturn = null;
        target.messageTitle = null;
        target.surfaceView = null;
        target.showWatchs = null;
        target.showWatchnums = null;
        target.showDianzanImg = null;
        target.showDianzannums = null;
        target.showCaiImg = null;
        target.showCainums = null;
        target.showPinglunImg = null;
        target.showPinglunnums = null;
        target.llayoutShowPinglunImg = null;
        target.showShipinjianjieTitle = null;
        target.showShipinjianjie = null;
        target.zankai = null;
        target.kaile = null;
        target.zhankai = null;
        target.mylistivew = null;
        target.commitnum = null;
        target.pinglun = null;
        target.llPinglunContent = null;
        target.pllistivew = null;
        target.clickmore = null;
        target.rlDd = null;
        target.neirong = null;
        target.svVideoscrollview = null;
        target.continuePlay = null;
        target.llWifi = null;
    }
}
