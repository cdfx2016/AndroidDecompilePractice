package com.fanyu.boundless.view.home;

import com.fanyu.boundless.bean.home.Posthomeworkentity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.view.base.IBaseView;
import java.util.List;

public interface IZuoyeBoBaoView extends IBaseView {
    void getIMyClassName(List<schoolclassentity> list);

    void getIZuoyeBoBao(List<Posthomeworkentity> list, int i);
}
