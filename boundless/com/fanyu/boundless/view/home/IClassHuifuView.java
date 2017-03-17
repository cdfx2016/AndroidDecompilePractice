package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IClassHuifuView extends IBaseView {
    void getIClassHuifu(List<ClassHuifuEntity> list, int i);

    void isDeleteZuoye(String str);

    void isadd();

    void isdelete(String str);

    void updateUnread(String str);

    void uploadimg(String str);
}
