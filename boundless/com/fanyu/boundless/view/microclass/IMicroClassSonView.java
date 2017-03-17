package com.fanyu.boundless.view.microclass;

import com.fanyu.boundless.bean.microclass.VideoalbumEntity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IMicroClassSonView extends IBaseView {
    void getVideoList(List<VideoalbumEntity> list, int i);
}
