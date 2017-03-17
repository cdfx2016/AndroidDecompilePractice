package com.fanyu.boundless.view.microclass;

import android.content.Context;
import android.content.Intent;
import butterknife.Bind;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.microclass.VideoalbumApi;
import com.fanyu.boundless.bean.microclass.VideoalbumEntity;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.presenter.microclass.MicroClassSonPresenter;
import com.fanyu.boundless.view.base.BaseLlayout;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView.PullLoadMoreListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MicroClassSonLayout extends BaseLlayout<MicroClassSonPresenter> implements IMicroClassSonView, PullLoadMoreListener {
    private MicroClassSonAdapter adapter;
    private int page = 1;
    @Bind({2131624490})
    PullLoadMoreRecyclerView pullRecycleview;
    private String typeid = "";
    private VideoalbumApi videoalbum;
    private List<VideoalbumEntity> wekelist;

    class MicroClassListener implements OnItemClickListener {
        MicroClassListener() {
        }

        public void onItemClick(ViewHolder arg0, int arg1) {
            VideoalbumEntity entity = (VideoalbumEntity) MicroClassSonLayout.this.wekelist.get(arg1);
            Intent intent = new Intent(MicroClassSonLayout.this.mContext, MicroClassDetailActivity.class);
            intent.putExtra("tittle", entity.getName());
            intent.putExtra("content", entity.getRemark());
            intent.putExtra("pid", entity.getId());
            MicroClassSonLayout.this.mContext.startActivity(intent);
        }
    }

    public MicroClassSonLayout(Context context) {
        super(context);
    }

    protected int getLayoutId() {
        return R.layout.layout_weke_video;
    }

    protected void initPresenter() {
        this.mPresenter = new MicroClassSonPresenter(this.mContext, this);
    }

    protected void init() {
        this.wekelist = new ArrayList(0);
        this.adapter = new MicroClassSonAdapter(this.mContext, R.layout.adapter_weke, this.wekelist);
        this.adapter.setOnItemClickListener(new MicroClassListener());
        this.pullRecycleview.setGridLayout(3);
        this.pullRecycleview.setAdapter(this.adapter);
        this.pullRecycleview.setOnPullLoadMoreListener(this);
        this.videoalbum = new VideoalbumApi();
        this.videoalbum.setPage(this.page + "");
        this.videoalbum.setPagesize("10");
    }

    public void setText(String text) {
        if (text != null) {
            this.typeid = text;
        }
        this.videoalbum.setTypeid(this.typeid);
        onRefresh();
    }

    public void getVideoList(List<VideoalbumEntity> result, int state) {
        onSuccess(result, state);
    }

    public void onSuccess(List<VideoalbumEntity> result, int State) {
        if (State == 1) {
            this.wekelist.clear();
            this.wekelist.addAll(result);
            this.adapter.notifyDataSetChanged();
            this.pullRecycleview.setPullLoadMoreCompleted();
        } else if (State == 2) {
            this.wekelist.addAll(result);
            this.adapter.notifyDataSetChanged();
            this.pullRecycleview.setPullLoadMoreCompleted();
        }
    }

    public void onRefresh() {
        this.page = 1;
        ((MicroClassSonPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.videoalbum, 1);
    }

    public void onLoadMore() {
        this.page++;
        ((MicroClassSonPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.videoalbum, 2);
    }

    public void loadFailure(String errorMsg) {
        super.loadFailure(errorMsg);
        showTip(errorMsg);
        this.pullRecycleview.setPullLoadMoreCompleted();
    }
}
