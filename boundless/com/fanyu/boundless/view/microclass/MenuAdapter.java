package com.fanyu.boundless.view.microclass;

import android.content.Context;
import android.view.View;
import com.fanyu.boundless.bean.microclass.VideoTypeEntity;
import com.fanyu.boundless.widget.horizontalscrollmenu.BaseAdapter;
import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> names = new ArrayList();
    private List<VideoTypeEntity> videolist;

    public MenuAdapter(Context mContext, List<VideoTypeEntity> videolist) {
        this.mContext = mContext;
        this.videolist = videolist;
    }

    public List<String> getMenuItems() {
        this.names.clear();
        for (VideoTypeEntity entity : this.videolist) {
            this.names.add(entity.getType());
        }
        return this.names;
    }

    public List<View> getContentViews() {
        List<View> views = new ArrayList();
        for (VideoTypeEntity entity : this.videolist) {
            MicroClassSonLayout sonLayout = new MicroClassSonLayout(this.mContext);
            sonLayout.setText(entity.getId());
            views.add(sonLayout);
        }
        return views;
    }

    public void onPageChanged(int position, boolean visitStatus) {
    }
}
