package com.fanyu.boundless.view.home;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.PopupList;
import com.fanyu.boundless.util.PopupList.OnPopupListClickListener;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.widget.ImagPagerUtil.ImagPagerUtil;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class HuifuLeft implements ItemViewDelegate<ClassHuifuEntity> {
    private List<String> attstr = new ArrayList(0);
    private deleteBack deBack = null;
    private huifuBack hfBack = null;
    private ImagPagerUtil imagPagerUtil;
    private Context mContext;
    private List<String> popupMenuItemList = new ArrayList();
    SharedPreferencesUtil sharedPreferencesUtil;
    private String userid;

    public interface deleteBack {
        void onListen(int i);
    }

    public interface huifuBack {
        void onhfListen(String str);
    }

    public huifuBack getHfBack() {
        return this.hfBack;
    }

    public void setHfBack(huifuBack hfBack) {
        this.hfBack = hfBack;
    }

    public deleteBack getDeBack() {
        return this.deBack;
    }

    public void setDeBack(deleteBack deBack) {
        this.deBack = deBack;
    }

    public HuifuLeft(Context mContext) {
        this.mContext = mContext;
        this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(mContext);
        this.userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
    }

    public int getItemViewLayoutId() {
        return R.layout.adapter_class_huifu_left;
    }

    public boolean isForViewType(ClassHuifuEntity item, int position) {
        if (item.getMytype() == 1) {
            return true;
        }
        return false;
    }

    public void convert(ViewHolder holder, final ClassHuifuEntity classHuifuEntity, int position) {
        ImageView tupian = (ImageView) holder.getView(R.id.send_picture);
        TextView content = (TextView) holder.getView(R.id.content);
        if (classHuifuEntity.getAtttype().equals("0")) {
            tupian.setVisibility(0);
            LayoutParams params = (LayoutParams) tupian.getLayoutParams();
            params.width = -2;
            params.height = -2;
            tupian.setLayoutParams(params);
            content.setVisibility(8);
            ImagePathUtil.getInstance().setYsImageUrl(this.mContext, tupian, classHuifuEntity.getContent(), new RoundedCornersTransformation(this.mContext, 15, 0));
        } else {
            content.setVisibility(0);
            tupian.setVisibility(8);
            if (StringUtils.isEmpty(classHuifuEntity.getContent())) {
                content.setText(classHuifuEntity.getContent());
            }
        }
        tupian.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    HuifuLeft.this.attstr.clear();
                    HuifuLeft.this.attstr.add(ImagePathUtil.getInstance().getPath(classHuifuEntity.getContent()));
                    HuifuLeft.this.imagPagerUtil = new ImagPagerUtil((Activity) HuifuLeft.this.mContext, HuifuLeft.this.attstr);
                    HuifuLeft.this.imagPagerUtil.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (StringUtils.isEmpty(classHuifuEntity.getCreatetime())) {
            holder.setText(R.id.createtime, StringUtils.formatDateTime(StringUtils.datestring(classHuifuEntity.getCreatetime())));
        }
        if (StringUtils.isEmpty(classHuifuEntity.getNickname())) {
            holder.setText(R.id.username, classHuifuEntity.getNickname());
        }
        ImageView imageView = (ImageView) holder.getView(R.id.userhead);
        if (StringUtils.isEmpty(classHuifuEntity.getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(this.mContext, imageView, classHuifuEntity.getUserimg(), new CropCircleTransformation(this.mContext));
        } else {
            imageView.setImageResource(R.mipmap.morenimg);
        }
        content.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                HuifuLeft.this.popupMenuItemList.clear();
                HuifuLeft.this.popupMenuItemList.add("回复");
                PopupList popupList = new PopupList();
                popupList.init(HuifuLeft.this.mContext, v, HuifuLeft.this.popupMenuItemList, new OnPopupListClickListener() {
                    public void onPopupListClick(View contextView, int contextPosition, int myposition) {
                        if (HuifuLeft.this.hfBack != null) {
                            HuifuLeft.this.hfBack.onhfListen(classHuifuEntity.getNickname());
                        }
                    }
                });
                popupList.setTextSize((float) popupList.sp2px(16.0f));
                popupList.setTextPadding(popupList.dp2px(15.0f), popupList.dp2px(10.0f), popupList.dp2px(15.0f), popupList.dp2px(10.0f));
                popupList.setIndicatorView(popupList.getDefaultIndicatorView((float) popupList.dp2px(24.0f), (float) popupList.dp2px(12.0f), -12303292));
                return false;
            }
        });
        tupian.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                HuifuLeft.this.popupMenuItemList.clear();
                HuifuLeft.this.popupMenuItemList.add("回复");
                PopupList popupList = new PopupList();
                popupList.init(HuifuLeft.this.mContext, v, HuifuLeft.this.popupMenuItemList, new OnPopupListClickListener() {
                    public void onPopupListClick(View contextView, int contextPosition, int myposition) {
                        if (HuifuLeft.this.hfBack != null) {
                            HuifuLeft.this.hfBack.onhfListen(classHuifuEntity.getNickname());
                        }
                    }
                });
                popupList.setTextSize((float) popupList.sp2px(16.0f));
                popupList.setTextPadding(popupList.dp2px(15.0f), popupList.dp2px(10.0f), popupList.dp2px(15.0f), popupList.dp2px(10.0f));
                popupList.setIndicatorView(popupList.getDefaultIndicatorView((float) popupList.dp2px(24.0f), (float) popupList.dp2px(12.0f), -12303292));
                return false;
            }
        });
    }
}
