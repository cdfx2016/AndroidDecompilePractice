package com.fanyu.boundless.view.theclass;

import com.fanyu.boundless.bean.theclass.classmember;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IDeleteStudentView extends IBaseView {
    void getStudentList(List<classmember> list);

    void isdelete(String str);
}
