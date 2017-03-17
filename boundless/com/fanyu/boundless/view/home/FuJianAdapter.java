package com.fanyu.boundless.view.home;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AttEntitysa;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.util.StringUtils;
import java.util.List;

public class FuJianAdapter extends CommonAdapter<AttEntitysa> {
    private Context context;

    public FuJianAdapter(Context context, int layoutId, List<AttEntitysa> datas) {
        super(context, layoutId, datas);
        this.context = context;
    }

    public void convert(ViewHolder holder, final AttEntitysa attEntitysa, int position) {
        holder.setText(R.id.fujian_bianhao, "附件" + (position + 1) + ":");
        if (StringUtils.isEmpty(attEntitysa.getOriginalfilename())) {
            holder.setText(R.id.fujian_name, attEntitysa.getOriginalfilename());
        }
        holder.setOnClickListener(R.id.fujian, new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(FuJianAdapter.this.context, ShowFuJianActivity.class);
                intent.putExtra(MessageEncoder.ATTR_FILENAME, attEntitysa.getOriginalfilename());
                intent.putExtra("fileurl", attEntitysa.getFilename());
                FuJianAdapter.this.context.startActivity(intent);
            }
        });
    }
}
