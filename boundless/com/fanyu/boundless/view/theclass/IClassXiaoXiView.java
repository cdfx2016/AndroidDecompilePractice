package com.fanyu.boundless.view.theclass;

import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.view.base.IBaseView;

public interface IClassXiaoXiView extends IBaseView {
    void deleteClass(String str);

    void outClass(String str);

    void updateXiaoXi(schoolclassentity com_fanyu_boundless_bean_theclass_schoolclassentity);
}
