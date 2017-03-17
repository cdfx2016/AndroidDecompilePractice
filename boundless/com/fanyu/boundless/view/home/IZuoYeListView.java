package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IZuoYeListView extends IBaseView {
    void getXiaoXiList(List<ClassHuifuEntity> list, int i);
}
