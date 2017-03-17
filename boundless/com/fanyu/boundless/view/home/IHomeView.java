package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.home.maincount;
import com.fanyu.boundless.bean.microclass.VideoEntity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IHomeView extends IBaseView {
    void getUnreadMessage(maincount com_fanyu_boundless_bean_home_maincount);

    void searchVideoListTop4(List<VideoEntity> list);
}
