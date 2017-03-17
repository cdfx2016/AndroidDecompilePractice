package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.home.GetSchoolEntity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IGetSchoolView extends IBaseView {
    void addGet(String str);

    void getArriveOrLeaveList(List<GetSchoolEntity> list, int i);

    void getIMyClassName(List<schoolclassentity> list);

    void getITeacherClassName(List<schoolclassentity> list);

    void updateUnread(String str);
}
