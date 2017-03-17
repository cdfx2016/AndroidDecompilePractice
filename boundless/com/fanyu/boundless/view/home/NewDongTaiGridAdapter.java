package com.fanyu.boundless.view.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.fanyu.boundless.widget.ImagPagerUtil.ImagPagerUtil;
import com.fanyu.boundless.widget.imagepager.ImagePagerActivity;
import java.util.ArrayList;
import java.util.List;

public class NewDongTaiGridAdapter extends CommonAdapter<AttEntitysa> {
    private ArrayList<String> attstr = new ArrayList(0);
    private ImagPagerUtil imagPagerUtil;
    private ImageLoader imageloader;
    private OnItemClickListener onItemClickListener;

    public NewDongTaiGridAdapter(Context context, int layoutId, List<AttEntitysa> datas) {
        super(context, layoutId, datas);
        this.imageloader = new ImageLoader(context.getApplicationContext());
        for (AttEntitysa data : datas) {
            try {
                this.attstr.add(ImagePathUtil.getInstance().getPath(data.getFilename()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.imagPagerUtil = new ImagPagerUtil((Activity) this.mContext, this.attstr);
    }

    public void convert(ViewHolder holder, final AttEntitysa attEntitysa, final int position) {
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
                if (attEntitysa != null) {
                    Intent intent = new Intent(NewDongTaiGridAdapter.this.mContext, ImagePagerActivity.class);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, NewDongTaiGridAdapter.this.attstr);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    NewDongTaiGridAdapter.this.mContext.startActivity(intent);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
