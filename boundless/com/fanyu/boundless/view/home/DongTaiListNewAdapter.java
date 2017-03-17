package com.fanyu.boundless.view.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AttEntitysa;
import com.fanyu.boundless.bean.home.DongTaiEntity;
import com.fanyu.boundless.bean.home.PraiseCancleApi;
import com.fanyu.boundless.bean.home.PraiseOrNoApi;
import com.fanyu.boundless.bean.home.PraiseSaveApi;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.presenter.home.PraisePresenter;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DongTaiListNewAdapter extends CommonAdapter<DongTaiEntity> implements PraiseIView {
    RecyclerView gridView;
    ImageView imgPraise;
    private boolean isPraise = false;
    private PraisePresenter praisePresenter;
    TextView zancount;
    LinearLayout zanlayout;

    public DongTaiListNewAdapter(Context context, int layoutId, List<DongTaiEntity> datas) {
        super(context, layoutId, datas);
    }

    public void convert(ViewHolder holder, DongTaiEntity dongTaiEntity, final int position) {
        this.praisePresenter = new PraisePresenter(this.mContext, this);
        PraiseOrNoApi praiseOrNoApi = new PraiseOrNoApi();
        praiseOrNoApi.setItemtype("dongtai");
        praiseOrNoApi.setUserid(((DongTaiEntity) this.mDatas.get(position)).getUserid());
        praiseOrNoApi.setQuestionid(((DongTaiEntity) this.mDatas.get(position)).getId());
        this.praisePresenter.startPost((RxAppCompatActivity) this.mContext, praiseOrNoApi);
        this.zanlayout = (LinearLayout) holder.getView(R.id.zanlayout);
        this.imgPraise = (ImageView) holder.getView(R.id.img_praise);
        this.gridView = (RecyclerView) holder.getView(R.id.gridView);
        this.zancount = (TextView) holder.getView(R.id.zancount);
        if (StringUtils.isEmpty(((DongTaiEntity) this.mDatas.get(position)).getUserimg())) {
            holder.setImageUrl(R.id.mtxs, ((DongTaiEntity) this.mDatas.get(position)).getUserimg());
        }
        if (StringUtils.isEmpty(((DongTaiEntity) this.mDatas.get(position)).getUsername())) {
            holder.setText(R.id.username, ((DongTaiEntity) this.mDatas.get(position)).getUsername());
        }
        if (StringUtils.isEmpty(((DongTaiEntity) this.mDatas.get(position)).getClassname())) {
            holder.setText(R.id.banjiname, ((DongTaiEntity) this.mDatas.get(position)).getClassname());
        }
        if (StringUtils.isEmpty(((DongTaiEntity) this.mDatas.get(position)).getCreatetime())) {
            holder.setText(R.id.publish_time, ((DongTaiEntity) this.mDatas.get(position)).getCreatetime());
        }
        TextView content = (TextView) holder.getView(R.id.content);
        if (StringUtils.isEmpty(((DongTaiEntity) this.mDatas.get(position)).getContent())) {
            content.setVisibility(0);
            holder.setText(R.id.content, ((DongTaiEntity) this.mDatas.get(position)).getContent());
        } else {
            content.setVisibility(8);
        }
        if (((DongTaiEntity) this.mDatas.get(position)).getAtt() == null || ((DongTaiEntity) this.mDatas.get(position)).getAtt().size() == 0) {
            this.gridView.setVisibility(8);
        } else {
            this.gridView.setVisibility(0);
            List<AttEntitysa> list = new ArrayList();
            int row = 3;
            if (((DongTaiEntity) this.mDatas.get(position)).getAtt().size() == 1) {
                row = 1;
                list.clear();
                list.addAll(((DongTaiEntity) this.mDatas.get(position)).getAtt());
            } else if (((DongTaiEntity) this.mDatas.get(position)).getAtt().size() == 4) {
                list.clear();
                int i = 0;
                while (i < ((DongTaiEntity) this.mDatas.get(position)).getAtt().size() + 2) {
                    if (i == 0 || i == 1) {
                        list.add(((DongTaiEntity) this.mDatas.get(position)).getAtt().get(i));
                    } else if (i == 2 || i == 5) {
                        list.add(null);
                    } else if (i == 3 || i == 4) {
                        list.add(((DongTaiEntity) this.mDatas.get(position)).getAtt().get(i - 1));
                    }
                    i++;
                }
            } else {
                list.clear();
                list.addAll(((DongTaiEntity) this.mDatas.get(position)).getAtt());
            }
            this.gridView.setLayoutManager(new FullyGridLayoutManager(this.mContext, row));
            CommonAdapter<AttEntitysa> dongTaiEntityCommonAdapter = new NewDongTaiGridAdapter(this.mContext, R.layout.adapter_zuoyebobaogrid, list);
            this.gridView.setAdapter(dongTaiEntityCommonAdapter);
            dongTaiEntityCommonAdapter.notifyDataSetChanged();
        }
        holder.setText(R.id.biaoqian, ((DongTaiEntity) this.mDatas.get(position)).getBiaoqian());
        if (!(((DongTaiEntity) this.mDatas.get(position)).getDailreply() == null || ((DongTaiEntity) this.mDatas.get(position)).getDailreply().size() == 0)) {
            holder.setText(R.id.pingluncount, ((DongTaiEntity) this.mDatas.get(position)).getDailreply().size() + "");
        }
        if (StringUtils.isEmpty(((DongTaiEntity) this.mDatas.get(position)).getPraise())) {
            holder.setText(R.id.zancount, ((DongTaiEntity) this.mDatas.get(position)).getPraise());
        }
        this.zanlayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (DongTaiListNewAdapter.this.isPraise) {
                    DongTaiListNewAdapter.this.isPraise = false;
                    DongTaiListNewAdapter.this.imgPraise.setBackgroundResource(R.mipmap.zhao_zan_grey);
                    int count = Integer.valueOf(DongTaiListNewAdapter.this.zancount.getText().toString()).intValue();
                    if (count > 1) {
                        DongTaiListNewAdapter.this.zancount.setText((count - 1) + "");
                    } else {
                        DongTaiListNewAdapter.this.zancount.setText("0");
                    }
                    PraiseCancleApi praiseCancleApi = new PraiseCancleApi();
                    praiseCancleApi.setUserid(((DongTaiEntity) DongTaiListNewAdapter.this.mDatas.get(position)).getUserid());
                    praiseCancleApi.setQuestionid(((DongTaiEntity) DongTaiListNewAdapter.this.mDatas.get(position)).getId());
                    praiseCancleApi.setItemtype("dongtai");
                    DongTaiListNewAdapter.this.praisePresenter.startPost((RxAppCompatActivity) DongTaiListNewAdapter.this.mContext, praiseCancleApi);
                    return;
                }
                DongTaiListNewAdapter.this.isPraise = true;
                DongTaiListNewAdapter.this.imgPraise.setBackgroundResource(R.mipmap.zhao_zan_pre);
                DongTaiListNewAdapter.this.zancount.setText((Integer.valueOf(DongTaiListNewAdapter.this.zancount.getText().toString()).intValue() + 1) + "");
                PraiseSaveApi praiseSaveApi = new PraiseSaveApi();
                praiseSaveApi.setUserid(((DongTaiEntity) DongTaiListNewAdapter.this.mDatas.get(position)).getUserid());
                praiseSaveApi.setQuestionid(((DongTaiEntity) DongTaiListNewAdapter.this.mDatas.get(position)).getId());
                praiseSaveApi.setItemtype("dongtai");
                DongTaiListNewAdapter.this.praisePresenter.startPost((RxAppCompatActivity) DongTaiListNewAdapter.this.mContext, praiseSaveApi);
            }
        });
        ((LinearLayout) holder.getView(R.id.item_dongtai_ll)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("id", ((DongTaiEntity) DongTaiListNewAdapter.this.mDatas.get(position)).getId());
                intent.putExtra("entity", (Serializable) DongTaiListNewAdapter.this.mDatas.get(position));
                intent.setClass(DongTaiListNewAdapter.this.mContext, DongtaiListItemActivity.class);
                DongTaiListNewAdapter.this.mContext.startActivity(intent);
            }
        });
    }

    public void praiseSave(String result) {
    }

    public void praiseCancel(String result) {
        int count = Integer.valueOf(this.zancount.getText().toString()).intValue();
        if (count > 1) {
            this.zancount.setText((count - 1) + "");
        } else {
            this.zancount.setText("0");
        }
        this.zanlayout.setClickable(true);
    }

    public void praiseIsOrNo(String result) {
        if (StringUtils.isEmpty(result)) {
            this.isPraise = true;
        } else {
            this.isPraise = false;
        }
        if (this.isPraise) {
            this.imgPraise.setBackgroundResource(R.mipmap.zhao_zan_pre);
        } else {
            this.imgPraise.setBackgroundResource(R.mipmap.zhao_zan_grey);
        }
    }
}
