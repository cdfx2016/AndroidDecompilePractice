package com.fanyu.boundless.view.theclass;

import com.fanyu.boundless.bean.theclass.StudentsModel;
import com.fanyu.boundless.bean.theclass.classzuentity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IUpdateStudentView extends IBaseView {
    void getClassList(List<schoolclassentity> list);

    void getClassStuList(List<StudentsModel> list);

    void getZuList(List<classzuentity> list);
}
