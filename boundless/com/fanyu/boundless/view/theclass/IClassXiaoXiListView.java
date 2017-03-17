package com.fanyu.boundless.view.theclass;

import com.fanyu.boundless.bean.theclass.applyentity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IClassXiaoXiListView extends IBaseView {
    void getXiaoXiList(List<applyentity> list, int i);
}
