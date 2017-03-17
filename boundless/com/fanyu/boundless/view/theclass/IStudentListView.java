package com.fanyu.boundless.view.theclass;

import com.fanyu.boundless.bean.theclass.classzuentity;
import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IStudentListView extends IBaseView {
    void getChildList(List<student> list);

    void getMyRole(String str);

    void getZuList(List<classzuentity> list);
}
