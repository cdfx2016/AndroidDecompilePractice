package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.home.ClassHuifuEntity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface ISubmitHomeWorkView extends IBaseView {
    void getZuoyeList(List<ClassHuifuEntity> list, int i);

    void isadd();

    void isdelete(String str);

    void issend();

    void updateUnread();

    void uploadimg(String str);
}
