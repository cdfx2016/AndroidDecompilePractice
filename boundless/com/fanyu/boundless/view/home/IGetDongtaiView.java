package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.home.DongTaiEntity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IGetDongtaiView extends IBaseView {
    void addPinglun(String str);

    void deleteDongtai(String str);

    void getDongtai(List<DongTaiEntity> list, int i);

    void getGerenShuoShuo(List<DongTaiEntity> list, int i);

    void getMyClass(List<schoolclassentity> list);

    void praiseCancel(String str);

    void praiseIsOrNo(String str);

    void praiseSave(String str);
}
