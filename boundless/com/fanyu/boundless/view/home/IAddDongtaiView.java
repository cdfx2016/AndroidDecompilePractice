package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IAddDongtaiView extends IBaseView {
    void addAtt(String str);

    void addDongtai(String str);

    void fileList(String str);

    void getIMyClassName(List<schoolclassentity> list);
}
