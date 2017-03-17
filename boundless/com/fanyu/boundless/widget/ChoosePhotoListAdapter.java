package com.fanyu.boundless.widget;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.toolsfinal.DeviceUtils;
import com.fanyu.boundless.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

public class ChoosePhotoListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<PhotoInfo> mList;
    private int mScreenWidth;

    public ChoosePhotoListAdapter(Activity activity, List<PhotoInfo> list) {
        this.mList = list;
        this.mInflater = LayoutInflater.from(activity);
        this.mScreenWidth = DeviceUtils.getScreenPix(activity).widthPixels;
    }

    public int getCount() {
        return this.mList.size();
    }

    public Object getItem(int position) {
        return Integer.valueOf(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DisplayImageOptions options = new Builder().showImageOnFail((int) R.drawable.ic_gf_default_photo).showImageForEmptyUri((int) R.drawable.ic_gf_default_photo).showImageOnLoading((int) R.drawable.ic_gf_default_photo).build();
        ImageView ivPhoto = (ImageView) this.mInflater.inflate(R.layout.adapter_photo_list_item, null);
        setHeight(ivPhoto);
        ImageLoader.getInstance().displayImage("file:/" + ((PhotoInfo) this.mList.get(position)).getPhotoPath(), ivPhoto, options);
        return ivPhoto;
    }

    private void setHeight(View convertView) {
        convertView.setLayoutParams(new LayoutParams(-1, (this.mScreenWidth / 3) - 8));
    }
}
