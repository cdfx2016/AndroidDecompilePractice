package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.theclass.classzuentity;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IGetNoticeView extends IBaseView {
    void addGet(String str);

    void getChildList(List<student> list);

    void getZuList(List<classzuentity> list);
}
