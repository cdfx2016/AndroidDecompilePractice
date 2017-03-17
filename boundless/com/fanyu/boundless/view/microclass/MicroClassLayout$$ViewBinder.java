package com.fanyu.boundless.view.microclass;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.horizontalscrollmenu.HorizontalScrollMenu;

public class MicroClassLayout$$ViewBinder<T extends MicroClassLayout> implements ViewBinder<T> {
    public void bind(Finder finder, T target, Object source) {
        target.imgReturn = (ImageView) finder.castView((View) finder.findRequiredView(source, R.id.img_return, "field 'imgReturn'"), R.id.img_return, "field 'imgReturn'");
        target.messageTitle = (TextView) finder.castView((View) finder.findRequiredView(source, R.id.message_title, "field 'messageTitle'"), R.id.message_title, "field 'messageTitle'");
        target.hsmContainer = (HorizontalScrollMenu) finder.castView((View) finder.findRequiredView(source, R.id.hsm_container, "field 'hsmContainer'"), R.id.hsm_container, "field 'hsmContainer'");
    }

    public void unbind(T target) {
        target.imgReturn = null;
        target.messageTitle = null;
        target.hsmContainer = null;
    }
}
