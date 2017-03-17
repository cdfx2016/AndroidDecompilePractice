package com.fanyu.boundless.view.theclass;

import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IClassView extends IBaseView {
    void getClassList(List<schoolclassentity> list, int i);

    void getUnread(String str);
}
