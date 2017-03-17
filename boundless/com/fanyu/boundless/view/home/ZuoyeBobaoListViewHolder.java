package com.fanyu.boundless.view.home;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AttEntitysa;
import com.fanyu.boundless.bean.home.Posthomeworkentity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.fanyu.boundless.widget.tagview.Tag;
import com.fanyu.boundless.widget.tagview.TagView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.xiaomi.mipush.sdk.Constants;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ZuoyeBobaoListViewHolder extends BaseViewHolder<Posthomeworkentity> {
    TextView banjiname = ((TextView) $(R.id.banjiname));
    TextView content = ((TextView) $(R.id.content));
    LinearLayout dongtai;
    TagView dx_gridView = ((TagView) $(R.id.dx_tagview));
    ImageView fabuTp = ((ImageView) $(R.id.fabu_tp));
    RecyclerView fujianView = ((RecyclerView) $(R.id.fujianView));
    RecyclerView gridView = ((RecyclerView) $(R.id.gridView));
    TextView hongdian = ((TextView) $(R.id.hongdian));
    TextView liuyancount = ((TextView) $(R.id.liuyancount));
    LinearLayout pinglunlayout;
    TextView publishTime = ((TextView) $(R.id.publish_time));
    SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(getContext());
    LinearLayout showLayout = ((LinearLayout) $(R.id.pinglunlayout));
    TextView showcount = ((TextView) $(R.id.showcount));
    TextView stucount = ((TextView) $(R.id.stucount));
    TextView tittle = ((TextView) $(R.id.tittle));
    ImageView userhead = ((ImageView) $(R.id.userhead));
    private String userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
    TextView username = ((TextView) $(R.id.username));
    LinearLayout zanlayout;

    public ZuoyeBobaoListViewHolder(ViewGroup itemView) {
        super(itemView, R.layout.adapter_zuoyebobaolist);
    }

    public void setData(Posthomeworkentity entity) {
        final Posthomeworkentity posthomeworkentity;
        Log.i("ViewHolder", "position" + getDataPosition());
        if (StringUtils.isEmpty(entity.getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(getContext(), this.userhead, entity.getUserimg(), new CropCircleTransformation(getContext()));
        }
        if (StringUtils.isEmpty(entity.getShowcount())) {
            this.showcount.setText(entity.getShowcount());
        }
        if (StringUtils.isEmpty(entity.getStucount())) {
            this.stucount.setText(entity.getStucount());
        }
        if (StringUtils.isEmpty(entity.getLiuyancount())) {
            this.liuyancount.setText(entity.getLiuyancount());
        }
        if (entity.getUserid().equals(this.userid) && entity.getIsread().equals("2")) {
            this.banjiname.setVisibility(8);
            this.hongdian.setVisibility(8);
            this.fabuTp.setVisibility(0);
            this.dx_gridView.setVisibility(0);
            this.username.setText("我");
            String[] sz = entity.getClassname().split(Constants.ACCEPT_TIME_SEPARATOR_SP);
            this.dx_gridView.clear();
            for (String ss : sz) {
                if (StringUtils.isEmpty(ss)) {
                    this.dx_gridView.add(new Tag(ss));
                }
            }
        } else {
            this.fabuTp.setVisibility(8);
            this.dx_gridView.setVisibility(8);
            if (StringUtils.isEmpty(entity.getClassname())) {
                this.banjiname.setVisibility(0);
                this.banjiname.setText(entity.getClassname());
            } else {
                this.banjiname.setVisibility(8);
            }
            if (StringUtils.isEmpty(entity.getNickname())) {
                this.username.setText(entity.getNickname());
            }
            if (entity.getIsread().equals("0")) {
                this.hongdian.setVisibility(0);
            } else {
                this.hongdian.setVisibility(8);
            }
        }
        if (StringUtils.isEmpty(entity.getCreatetime())) {
            this.publishTime.setText(StringUtils.datestring(entity.getCreatetime()));
        }
        if (StringUtils.isEmpty(entity.getHwdescribe())) {
            this.content.setVisibility(0);
            this.content.setText(entity.getHwdescribe());
        } else {
            this.content.setVisibility(8);
        }
        if (entity.getHwtype().equals("1")) {
            this.tittle.setText("主题：" + entity.getHwtittle());
        } else {
            this.tittle.setText(entity.getHwtittle());
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
            ZuoyeBobaoGridAdapter zuoyeBobaoGridAdapter = new ZuoyeBobaoGridAdapter(getContext(), R.layout.adapter_zuoyebobaogrid, list);
            posthomeworkentity = entity;
            zuoyeBobaoGridAdapter.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(ViewHolder arg0, int arg1) {
                    Intent intent = new Intent();
                    intent.putExtra("entity", posthomeworkentity);
                    intent.putExtra("position", ZuoyeBobaoListViewHolder.this.getDataPosition());
                    intent.putExtra("leixing", posthomeworkentity.getHwtype());
                    intent.setClass(ZuoyeBobaoListViewHolder.this.getContext(), ZuoyeBobaoListItemActivity.class);
                    ZuoyeBobaoListViewHolder.this.getContext().startActivity(intent);
                }
            });
            this.gridView.setAdapter(zuoyeBobaoGridAdapter);
            zuoyeBobaoGridAdapter.notifyDataSetChanged();
        }
        if (entity.getFile() == null || entity.getFile().size() == 0) {
            this.fujianView.setVisibility(8);
        } else {
            this.fujianView.setVisibility(0);
            this.fujianView.setLayoutManager(new FullyGridLayoutManager(getContext(), 1));
            CommonAdapter<AttEntitysa> fujianAdapter = new FuJianAdapter(getContext(), R.layout.adapter_fujian_view, entity.getFile());
            this.fujianView.setAdapter(fujianAdapter);
            fujianAdapter.notifyDataSetChanged();
        }
        posthomeworkentity = entity;
        this.showLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ZuoyeBobaoListViewHolder.this.getContext(), UnshowStuActivity.class);
                intent.putExtra("itemid", posthomeworkentity.getId());
                ZuoyeBobaoListViewHolder.this.getContext().startActivity(intent);
            }
        });
    }
}
