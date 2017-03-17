package com.fanyu.boundless.view.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AttEntitysa;
import com.fanyu.boundless.bean.home.Posthomeworkentity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import com.fanyu.boundless.widget.tagview.Tag;
import com.fanyu.boundless.widget.tagview.TagView;
import com.xiaomi.mipush.sdk.Constants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ZuoYeBoBaoNewAdapter extends CommonAdapter<Posthomeworkentity> {
    private String leixing;
    private String userid;

    public ZuoYeBoBaoNewAdapter(Context context, int layoutId, List<Posthomeworkentity> datas, String userid, String leixing) {
        super(context, layoutId, datas);
        this.userid = userid;
        this.leixing = leixing;
    }

    public void convert(ViewHolder holder, Posthomeworkentity posthomeworkentity, int position) {
        final int i;
        ImageView userhead = (ImageView) holder.getView(R.id.userhead);
        if (StringUtils.isEmpty(((Posthomeworkentity) this.mDatas.get(position)).getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(this.mContext, userhead, posthomeworkentity.getUserimg(), new CropCircleTransformation(this.mContext));
        }
        if (StringUtils.isEmpty(((Posthomeworkentity) this.mDatas.get(position)).getShowcount())) {
            holder.setText(R.id.showcount, ((Posthomeworkentity) this.mDatas.get(position)).getShowcount());
        }
        if (StringUtils.isEmpty(((Posthomeworkentity) this.mDatas.get(position)).getStucount())) {
            holder.setText(R.id.stucount, ((Posthomeworkentity) this.mDatas.get(position)).getStucount());
        }
        if (StringUtils.isEmpty(((Posthomeworkentity) this.mDatas.get(position)).getLiuyancount())) {
            holder.setText(R.id.liuyancount, ((Posthomeworkentity) this.mDatas.get(position)).getLiuyancount());
        }
        TextView banjiname = (TextView) holder.getView(R.id.banjiname);
        TextView hongdian = (TextView) holder.getView(R.id.hongdian);
        ImageView fabuTp = (ImageView) holder.getView(R.id.fabu_tp);
        TagView dx_gridView = (TagView) holder.getView(R.id.dx_tagview);
        if (((Posthomeworkentity) this.mDatas.get(position)).getUserid().equals(this.userid) && ((Posthomeworkentity) this.mDatas.get(position)).getIsread().equals("2")) {
            banjiname.setVisibility(8);
            hongdian.setVisibility(8);
            fabuTp.setVisibility(0);
            dx_gridView.setVisibility(0);
            holder.setText(R.id.username, "我");
            String[] sz = ((Posthomeworkentity) this.mDatas.get(position)).getClassname().split(Constants.ACCEPT_TIME_SEPARATOR_SP);
            dx_gridView.clear();
            for (String ss : sz) {
                if (StringUtils.isEmpty(ss)) {
                    dx_gridView.add(new Tag(ss));
                }
            }
        } else {
            fabuTp.setVisibility(8);
            dx_gridView.setVisibility(8);
            if (StringUtils.isEmpty(((Posthomeworkentity) this.mDatas.get(position)).getClassname())) {
                banjiname.setVisibility(0);
                holder.setText(R.id.banjiname, ((Posthomeworkentity) this.mDatas.get(position)).getClassname());
            } else {
                banjiname.setVisibility(8);
            }
            if (StringUtils.isEmpty(((Posthomeworkentity) this.mDatas.get(position)).getNickname())) {
                holder.setText(R.id.username, ((Posthomeworkentity) this.mDatas.get(position)).getNickname());
            }
            if (((Posthomeworkentity) this.mDatas.get(position)).getIsread().equals("0")) {
                hongdian.setVisibility(0);
            } else {
                hongdian.setVisibility(8);
            }
        }
        if (StringUtils.isEmpty(((Posthomeworkentity) this.mDatas.get(position)).getCreatetime())) {
            holder.setText(R.id.publish_time, StringUtils.datestring(((Posthomeworkentity) this.mDatas.get(position)).getCreatetime()));
        }
        TextView content = (TextView) holder.getView(R.id.content);
        if (StringUtils.isEmpty(((Posthomeworkentity) this.mDatas.get(position)).getHwdescribe())) {
            content.setVisibility(0);
            holder.setText(R.id.content, ((Posthomeworkentity) this.mDatas.get(position)).getHwdescribe());
        } else {
            content.setVisibility(8);
        }
        if (((Posthomeworkentity) this.mDatas.get(position)).getHwtype().equals("1")) {
            holder.setText(R.id.tittle, "主题：" + ((Posthomeworkentity) this.mDatas.get(position)).getHwtittle());
        } else {
            holder.setText(R.id.tittle, ((Posthomeworkentity) this.mDatas.get(position)).getHwtittle());
        }
        RecyclerView gridView = (RecyclerView) holder.getView(R.id.gridView);
        if (((Posthomeworkentity) this.mDatas.get(position)).getAtt() == null || ((Posthomeworkentity) this.mDatas.get(position)).getAtt().size() == 0) {
            gridView.setVisibility(8);
        } else {
            gridView.setVisibility(0);
            List<AttEntitysa> list = new ArrayList();
            int row = 3;
            if (((Posthomeworkentity) this.mDatas.get(position)).getAtt().size() == 1) {
                row = 1;
                list.clear();
                list.addAll(((Posthomeworkentity) this.mDatas.get(position)).getAtt());
            } else if (((Posthomeworkentity) this.mDatas.get(position)).getAtt().size() == 4) {
                list.clear();
                int i2 = 0;
                while (i2 < ((Posthomeworkentity) this.mDatas.get(position)).getAtt().size() + 2) {
                    if (i2 == 0 || i2 == 1) {
                        list.add(((Posthomeworkentity) this.mDatas.get(position)).getAtt().get(i2));
                    } else if (i2 == 2 || i2 == 5) {
                        list.add(null);
                    } else if (i2 == 3 || i2 == 4) {
                        list.add(((Posthomeworkentity) this.mDatas.get(position)).getAtt().get(i2 - 1));
                    }
                    i2++;
                }
            } else {
                list.clear();
                list.addAll(((Posthomeworkentity) this.mDatas.get(position)).getAtt());
            }
            gridView.setLayoutManager(new FullyGridLayoutManager(this.mContext, row));
            Adapter zuoyeBobaoGridAdapter = new ZuoyeBobaoGridAdapter(this.mContext, R.layout.adapter_zuoyebobaogrid, list);
            i = position;
            zuoyeBobaoGridAdapter.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(ViewHolder arg0, int arg1) {
                    Intent intent = new Intent();
                    intent.putExtra("entity", (Serializable) ZuoYeBoBaoNewAdapter.this.mDatas.get(i));
                    intent.putExtra("position", i);
                    intent.putExtra("leixing", ((Posthomeworkentity) ZuoYeBoBaoNewAdapter.this.mDatas.get(i)).getHwtype());
                    intent.setClass(ZuoYeBoBaoNewAdapter.this.mContext, ZuoyeBobaoListItemActivity.class);
                    ZuoYeBoBaoNewAdapter.this.mContext.startActivity(intent);
                }
            });
            gridView.setAdapter(zuoyeBobaoGridAdapter);
            zuoyeBobaoGridAdapter.notifyDataSetChanged();
        }
        RecyclerView fujianView = (RecyclerView) holder.getView(R.id.fujianView);
        if (((Posthomeworkentity) this.mDatas.get(position)).getFile() == null || ((Posthomeworkentity) this.mDatas.get(position)).getFile().size() == 0) {
            fujianView.setVisibility(8);
        } else {
            fujianView.setVisibility(0);
            fujianView.setLayoutManager(new FullyGridLayoutManager(this.mContext, 1));
            CommonAdapter<AttEntitysa> fujianAdapter = new FuJianAdapter(this.mContext, R.layout.adapter_fujian_view, ((Posthomeworkentity) this.mDatas.get(position)).getFile());
            fujianView.setAdapter(fujianAdapter);
            fujianAdapter.notifyDataSetChanged();
        }
        i = position;
        ((LinearLayout) holder.getView(R.id.pinglunlayout)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ZuoYeBoBaoNewAdapter.this.mContext, UnshowStuActivity.class);
                intent.putExtra("itemid", ((Posthomeworkentity) ZuoYeBoBaoNewAdapter.this.mDatas.get(i)).getId());
                ZuoYeBoBaoNewAdapter.this.mContext.startActivity(intent);
            }
        });
        i = position;
        ((RelativeLayout) holder.getView(R.id.zuoyebobao_rl)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("entity", (Serializable) ZuoYeBoBaoNewAdapter.this.mDatas.get(i));
                intent.putExtra("position", i);
                intent.putExtra("leixing", ZuoYeBoBaoNewAdapter.this.leixing);
                intent.setClass(ZuoYeBoBaoNewAdapter.this.mContext, ZuoyeBobaoListItemActivity.class);
                ZuoYeBoBaoNewAdapter.this.mContext.startActivity(intent);
            }
        });
    }
}
