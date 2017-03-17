package com.fanyu.boundless.view.home;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AttEntitysa;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.config.imageloader.ImageLoader;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class ZuoyeBobaoGridAdapter extends CommonAdapter<AttEntitysa> {
    private List<String> attstr = new ArrayList(0);
    private ImageLoader imageloader;
    private OnItemClickListener onItemClickListener;

    public ZuoyeBobaoGridAdapter(Context context, int layoutId, List<AttEntitysa> datas) {
        super(context, layoutId, datas);
        this.imageloader = new ImageLoader(context.getApplicationContext());
        for (AttEntitysa data : datas) {
            try {
                if (StringUtils.isEmpty(data.getFilename())) {
                    this.attstr.add(ImagePathUtil.getInstance().getPath(data.getFilename()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void convert(final ViewHolder holder, AttEntitysa attEntitysa, final int position) {
        ImageView imageView = (ImageView) holder.getView(R.id.ItemImage);
        if (this.attstr.size() == 1) {
            LayoutParams params = (LayoutParams) imageView.getLayoutParams();
            params.width = -2;
            params.height = -2;
            imageView.setLayoutParams(params);
            try {
                this.imageloader.DisplayImage(ImagePathUtil.getInstance().getPath(attEntitysa.getFilename()), imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (attEntitysa == null) {
            holder.setImageResource(R.id.ItemImage, R.mipmap.touming);
        } else if (StringUtils.isEmpty(attEntitysa.getFilename())) {
            holder.setImageUrl(R.id.ItemImage, attEntitysa.getFilename());
        }
        holder.setOnClickListener(R.id.ItemImage, new OnClickListener() {
            public void onClick(View v) {
                ZuoyeBobaoGridAdapter.this.onItemClickListener.onItemClick(holder, position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
