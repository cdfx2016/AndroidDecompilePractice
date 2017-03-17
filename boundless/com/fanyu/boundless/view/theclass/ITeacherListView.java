package com.fanyu.boundless.view.theclass;

import com.fanyu.boundless.bean.theclass.classmember;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface ITeacherListView extends IBaseView {
    void getTeacherList(List<classmember> list, int i);
}
