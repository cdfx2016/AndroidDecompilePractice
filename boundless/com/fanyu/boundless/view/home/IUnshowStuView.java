package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IUnshowStuView extends IBaseView {
    void getUnreadStu(List<student> list);
}
