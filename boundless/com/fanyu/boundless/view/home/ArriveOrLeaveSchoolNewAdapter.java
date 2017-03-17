package com.fanyu.boundless.view.home;

import android.content.Context;
import android.widget.ImageView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.GetSchoolEntity;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ArriveOrLeaveSchoolNewAdapter extends CommonAdapter<GetSchoolEntity> {
    public ArriveOrLeaveSchoolNewAdapter(Context context, int layoutId, List<GetSchoolEntity> datas) {
        super(context, layoutId, datas);
    }

    public void convert(ViewHolder holder, GetSchoolEntity getSchoolEntity, int position) {
        ImageView userhead = (ImageView) holder.getView(R.id.userhead);
        if (StringUtils.isEmpty(((GetSchoolEntity) this.mDatas.get(position)).getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(this.mContext, userhead, getSchoolEntity.getUserimg(), new CropCircleTransformation(this.mContext));
        }
        if (StringUtils.isEmpty(((GetSchoolEntity) this.mDatas.get(position)).getNickname())) {
            holder.setText(R.id.username, ((GetSchoolEntity) this.mDatas.get(position)).getNickname());
        }
        if (StringUtils.isEmpty(((GetSchoolEntity) this.mDatas.get(position)).getClassname())) {
            holder.setText(R.id.banjiname, ((GetSchoolEntity) this.mDatas.get(position)).getClassname());
        }
        if (StringUtils.isEmpty(((GetSchoolEntity) this.mDatas.get(position)).getCreatetime())) {
            holder.setText(R.id.createtime, StringUtils.datestring(((GetSchoolEntity) this.mDatas.get(position)).getCreatetime()));
        }
        if (((GetSchoolEntity) this.mDatas.get(position)).getDaoxiao().equals("0") && ((GetSchoolEntity) this.mDatas.get(position)).getGstype().equals("0")) {
            holder.setText(R.id.content, "您的孩子已到校。");
        } else if (((GetSchoolEntity) this.mDatas.get(position)).getDaoxiao().equals("0") && ((GetSchoolEntity) this.mDatas.get(position)).getGstype().equals("1")) {
            holder.setText(R.id.content, "您的孩子已放学。");
        } else if (((GetSchoolEntity) this.mDatas.get(position)).getDaoxiao().equals("-1") && ((GetSchoolEntity) this.mDatas.get(position)).getGstype().equals("0") && ((GetSchoolEntity) this.mDatas.get(position)).getTittle().equals("一键")) {
            holder.setText(R.id.content, ((GetSchoolEntity) this.mDatas.get(position)).getGscontent());
        } else if (((GetSchoolEntity) this.mDatas.get(position)).getDaoxiao().equals("-1") && ((GetSchoolEntity) this.mDatas.get(position)).getGstype().equals("0") && ((GetSchoolEntity) this.mDatas.get(position)).getTittle().equals("通知")) {
            holder.setText(R.id.content, "迟到告知,学生：" + ((GetSchoolEntity) this.mDatas.get(position)).getStuname() + ",迟到说明：" + ((GetSchoolEntity) this.mDatas.get(position)).getGscontent());
        } else if (((GetSchoolEntity) this.mDatas.get(position)).getDaoxiao().equals("-1") && ((GetSchoolEntity) this.mDatas.get(position)).getGstype().equals("1") && ((GetSchoolEntity) this.mDatas.get(position)).getTittle().equals("通知")) {
            holder.setText(R.id.content, "留校告知,学生：" + ((GetSchoolEntity) this.mDatas.get(position)).getStuname() + ",留校说明：" + ((GetSchoolEntity) this.mDatas.get(position)).getGscontent());
        } else if (!((GetSchoolEntity) this.mDatas.get(position)).getDaoxiao().equals("0") && ((GetSchoolEntity) this.mDatas.get(position)).getGstype().equals("0") && ((GetSchoolEntity) this.mDatas.get(position)).getTittle().equals("通知")) {
            holder.setText(R.id.content, "您的孩子未准时到校,迟到说明：" + ((GetSchoolEntity) this.mDatas.get(position)).getGscontent());
        } else if (!((GetSchoolEntity) this.mDatas.get(position)).getDaoxiao().equals("0") && ((GetSchoolEntity) this.mDatas.get(position)).getGstype().equals("1") && ((GetSchoolEntity) this.mDatas.get(position)).getTittle().equals("通知")) {
            holder.setText(R.id.content, "您的孩子被留校,留校说明：" + ((GetSchoolEntity) this.mDatas.get(position)).getGscontent());
        } else if (StringUtils.isEmpty(((GetSchoolEntity) this.mDatas.get(position)).getGscontent())) {
            holder.setText(R.id.content, ((GetSchoolEntity) this.mDatas.get(position)).getGscontent());
        }
    }
}
