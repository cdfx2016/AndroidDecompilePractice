package com.fanyu.boundless.view.microclass;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;
import com.fanyu.boundless.R;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;

public class MicroClassSonLayout$$ViewBinder<T extends MicroClassSonLayout> implements ViewBinder<T> {
    public void bind(Finder finder, T target, Object source) {
        target.pullRecycleview = (PullLoadMoreRecyclerView) finder.castView((View) finder.findRequiredView(source, R.id.pull_recycleview, "field 'pullRecycleview'"), R.id.pull_recycleview, "field 'pullRecycleview'");
    }

    public void unbind(T target) {
        target.pullRecycleview = null;
    }
}
