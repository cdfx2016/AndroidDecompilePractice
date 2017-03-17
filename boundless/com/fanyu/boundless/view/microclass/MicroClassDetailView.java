package com.fanyu.boundless.view.microclass;

import com.fanyu.boundless.bean.microclass.SpinglunEntity;
import com.fanyu.boundless.bean.microclass.VideoEntity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface MicroClassDetailView extends IBaseView {
    void savePinglun(String str);

    void searchWeikeListSecond(List<VideoEntity> list);

    void xjObtainVedioComment(List<SpinglunEntity> list, int i);

    void xjVCheckNum(String str);

    void zanzan(String str);
}
