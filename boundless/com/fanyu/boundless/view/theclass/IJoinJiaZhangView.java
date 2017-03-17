package com.fanyu.boundless.view.theclass;

import com.fanyu.boundless.bean.theclass.student;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IJoinJiaZhangView extends IBaseView {
    void isapply(String str);

    void selectChildList(List<student> list);
}
