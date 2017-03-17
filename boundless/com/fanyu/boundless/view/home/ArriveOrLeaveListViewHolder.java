package com.fanyu.boundless.view.home;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.GetSchoolEntity;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ArriveOrLeaveListViewHolder extends BaseViewHolder<GetSchoolEntity> {
    TextView banjiname = ((TextView) $(R.id.banjiname));
    TextView content = ((TextView) $(R.id.content));
    TextView createtime = ((TextView) $(R.id.createtime));
    ImageView userhead = ((ImageView) $(R.id.userhead));
    TextView username = ((TextView) $(R.id.username));

    public ArriveOrLeaveListViewHolder(ViewGroup itemView) {
        super(itemView, R.layout.adapter_getarriveorleavelist);
    }

    public void setData(GetSchoolEntity entity) {
        Log.i("ViewHolder", "position" + getDataPosition());
        if (StringUtils.isEmpty(entity.getUserimg())) {
            ImagePathUtil.getInstance().setImageUrl(getContext(), this.userhead, entity.getUserimg(), new CropCircleTransformation(getContext()));
        }
        if (StringUtils.isEmpty(entity.getNickname())) {
            this.username.setText(entity.getNickname());
        }
        if (StringUtils.isEmpty(entity.getClassname())) {
            this.banjiname.setText(entity.getClassname());
        }
        if (StringUtils.isEmpty(entity.getCreatetime())) {
            this.createtime.setText(StringUtils.datestring(entity.getCreatetime()));
        }
        if (entity.getDaoxiao().equals("0") && entity.getGstype().equals("0")) {
            this.content.setText("您的孩子已到校。");
        } else if (entity.getDaoxiao().equals("0") && entity.getGstype().equals("1")) {
            this.content.setText("您的孩子已放学。");
        } else if (entity.getDaoxiao().equals("-1") && entity.getGstype().equals("0") && entity.getTittle().equals("一键")) {
            this.content.setText(entity.getGscontent());
        } else if (entity.getDaoxiao().equals("-1") && entity.getGstype().equals("0") && entity.getTittle().equals("通知")) {
            this.content.setText("迟到告知,学生：" + entity.getStuname() + ",迟到说明：" + entity.getGscontent());
        } else if (entity.getDaoxiao().equals("-1") && entity.getGstype().equals("1") && entity.getTittle().equals("通知")) {
            this.content.setText("留校告知,学生：" + entity.getStuname() + ",留校说明：" + entity.getGscontent());
        } else if (!entity.getDaoxiao().equals("0") && entity.getGstype().equals("0") && entity.getTittle().equals("通知")) {
            this.content.setText("您的孩子未准时到校,迟到说明：" + entity.getGscontent());
        } else if (!entity.getDaoxiao().equals("0") && entity.getGstype().equals("1") && entity.getTittle().equals("通知")) {
            this.content.setText("您的孩子被留校,留校说明：" + entity.getGscontent());
        } else if (StringUtils.isEmpty(entity.getGscontent())) {
            this.content.setText(entity.getGscontent());
        }
    }
}
