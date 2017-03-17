package com.fanyu.boundless.view.home;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import butterknife.internal.DebouncingOnClickListener;
import com.fanyu.boundless.R;
import com.jude.easyrecyclerview.EasyRecyclerView;

public class DongtaiListItemActivity$$ViewBinder<T extends DongtaiListItemActivity> implements ViewBinder<T> {
    public void bind(Finder finder, final T target, Object source) {
        target.recyclerView = (EasyRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.recyclerView, "field 'recyclerView'"), R.id.recyclerView, "field 'recyclerView'");
        target.chatContent = (EditText) finder.castView((View) finder.findRequiredView(source, R.id.chat_content, "field 'chatContent'"), R.id.chat_content, "field 'chatContent'");
        View view = (View) finder.findRequiredView(source, R.id.chat_sendbtn, "field 'chatSendbtn' and method 'onclick'");
        target.chatSendbtn = (Button) finder.castView(view, R.id.chat_sendbtn, "field 'chatSendbtn'");
        view.setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onclick(p0);
            }
        });
        target.waibu = (RelativeLayout) finder.castView((View) finder.findRequiredView(source, R.id.waibu, "field 'waibu'"), R.id.waibu, "field 'waibu'");
        ((View) finder.findRequiredView(source, R.id.img_return, "method 'onclick'")).setOnClickListener(new DebouncingOnClickListener() {
            public void doClick(View p0) {
                target.onclick(p0);
            }
        });
    }

    public void unbind(T target) {
        target.recyclerView = null;
        target.chatContent = null;
        target.chatSendbtn = null;
        target.waibu = null;
    }
}
