package com.fanyu.boundless.view.home;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AttEntitysa;
import com.fanyu.boundless.bean.home.DongTaiEntity;
import com.fanyu.boundless.bean.home.PraiseCancleApi;
import com.fanyu.boundless.bean.home.PraiseOrNoApi;
import com.fanyu.boundless.bean.home.PraiseSaveApi;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.presenter.home.PraisePresenter;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class DongtaiListViewHolder extends BaseViewHolder<DongTaiEntity> implements PraiseIView {
    TextView banjiname = ((TextView) $(R.id.banjiname));
    TextView biaoqian = ((TextView) $(R.id.biaoqian));
    RelativeLayout biaoqianlayout;
    TextView content = ((TextView) $(R.id.content));
    LinearLayout dongtai;
    private DongTaiEntity entitysingle;
    RecyclerView gridView = ((RecyclerView) $(R.id.gridView));
    ImageView imgPraise = ((ImageView) $(R.id.img_praise));
    private boolean isPraise = false;
    TextView more;
    ImageView pinglunbutton;
    TextView pingluncount = ((TextView) $(R.id.pingluncount));
    LinearLayout pinglunlayout;
    private PraisePresenter praisePresenter;
    TextView publishTime = ((TextView) $(R.id.publish_time));
    ImageView shareimg;
    LinearLayout sharelayout;
    ImageView touxiang = ((ImageView) $(R.id.mtxs));
    ImageView tupian;
    TextView username = ((TextView) $(R.id.username));
    TextView zancount = ((TextView) $(R.id.zancount));
    LinearLayout zanlayout = ((LinearLayout) $(R.id.zanlayout));
    LinearLayout zhengti;

    public DongtaiListViewHolder(ViewGroup itemView) {
        super(itemView, R.layout.adapter_dongtailist);
    }

    public void setData(DongTaiEntity entity) {
        Log.i("ViewHolder", "position" + getDataPosition());
        this.entitysingle = entity;
        this.praisePresenter = new PraisePresenter(getContext(), this);
        PraiseOrNoApi praiseOrNoApi = new PraiseOrNoApi();
        praiseOrNoApi.setItemtype("dongtai");
        praiseOrNoApi.setUserid(entity.getUserid());
        praiseOrNoApi.setQuestionid(entity.getId());
        this.praisePresenter.startPost((RxAppCompatActivity) getContext(), praiseOrNoApi);
        if (StringUtils.isEmpty(entity.getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(getContext(), this.touxiang, entity.getUserimg(), new CropCircleTransformation(getContext()));
        }
        if (StringUtils.isEmpty(entity.getUsername())) {
            this.username.setText(entity.getUsername());
        }
        if (StringUtils.isEmpty(entity.getClassname())) {
            this.banjiname.setText(entity.getClassname());
        }
        if (StringUtils.isEmpty(entity.getCreatetime())) {
            this.publishTime.setText(entity.getCreatetime());
        }
        if (StringUtils.isEmpty(entity.getContent())) {
            this.content.setVisibility(0);
            this.content.setText(entity.getContent());
        } else {
            this.content.setVisibility(8);
        }
        if (entity.getAtt() == null || entity.getAtt().size() == 0) {
            this.gridView.setVisibility(8);
        } else {
            this.gridView.setVisibility(0);
            List<AttEntitysa> list = new ArrayList();
            int row = 3;
            if (entity.getAtt().size() == 1) {
                row = 1;
                list.clear();
                list.addAll(entity.getAtt());
            } else if (entity.getAtt().size() == 4) {
                list.clear();
                int i = 0;
                while (i < entity.getAtt().size() + 2) {
                    if (i == 0 || i == 1) {
                        list.add(entity.getAtt().get(i));
                    } else if (i == 2 || i == 5) {
                        list.add(null);
                    } else if (i == 3 || i == 4) {
                        list.add(entity.getAtt().get(i - 1));
                    }
                    i++;
                }
            } else {
                list.clear();
                list.addAll(entity.getAtt());
            }
            this.gridView.setLayoutManager(new FullyGridLayoutManager(getContext(), row));
            CommonAdapter<AttEntitysa> dongTaiEntityCommonAdapter = new NewDongTaiGridAdapter(getContext(), R.layout.adapter_zuoyebobaogrid, list);
            this.gridView.setAdapter(dongTaiEntityCommonAdapter);
            dongTaiEntityCommonAdapter.notifyDataSetChanged();
        }
        this.biaoqian.setText(entity.getBiaoqian());
        if (!(entity.getDailreply() == null || entity.getDailreply().size() == 0)) {
            this.pingluncount.setText(entity.getDailreply().size() + "");
        }
        if (StringUtils.isEmpty(entity.getPraise())) {
            this.zancount.setText(entity.getPraise());
        }
        this.zanlayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (DongtaiListViewHolder.this.isPraise) {
                    DongtaiListViewHolder.this.isPraise = false;
                    DongtaiListViewHolder.this.imgPraise.setBackgroundResource(R.mipmap.zhao_zan_grey);
                    PraiseCancleApi praiseCancleApi = new PraiseCancleApi();
                    praiseCancleApi.setUserid(DongtaiListViewHolder.this.entitysingle.getUserid());
                    praiseCancleApi.setQuestionid(DongtaiListViewHolder.this.entitysingle.getId());
                    praiseCancleApi.setItemtype("dongtai");
                    DongtaiListViewHolder.this.praisePresenter.startPost((RxAppCompatActivity) DongtaiListViewHolder.this.getContext(), praiseCancleApi);
                } else {
                    DongtaiListViewHolder.this.isPraise = true;
                    DongtaiListViewHolder.this.imgPraise.setBackgroundResource(R.mipmap.zhao_zan_pre);
                    PraiseSaveApi praiseSaveApi = new PraiseSaveApi();
                    praiseSaveApi.setUserid(DongtaiListViewHolder.this.entitysingle.getUserid());
                    praiseSaveApi.setQuestionid(DongtaiListViewHolder.this.entitysingle.getId());
                    praiseSaveApi.setItemtype("dongtai");
                    DongtaiListViewHolder.this.praisePresenter.startPost((RxAppCompatActivity) DongtaiListViewHolder.this.getContext(), praiseSaveApi);
                }
                DongtaiListViewHolder.this.zanlayout.setClickable(false);
            }
        });
    }

    public void praiseSave(String result) {
        this.zancount.setText((Integer.valueOf(this.zancount.getText().toString()).intValue() + 1) + "");
        this.zanlayout.setClickable(true);
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
