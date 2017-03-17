package com.fanyu.boundless.view.microclass;

import com.fanyu.boundless.bean.microclass.VideoTypeEntity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IMicroClassView extends IBaseView {
    void gettitlelist(List<VideoTypeEntity> list);
}
