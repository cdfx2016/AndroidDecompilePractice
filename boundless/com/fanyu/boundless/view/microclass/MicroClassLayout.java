package com.fanyu.boundless.view.microclass;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.microclass.VideoTypeApi;
import com.fanyu.boundless.bean.microclass.VideoTypeEntity;
import com.fanyu.boundless.presenter.microclass.MicroClassPresenter;
import com.fanyu.boundless.view.base.BaseLlayout;
import com.fanyu.boundless.widget.horizontalscrollmenu.HorizontalScrollMenu;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MicroClassLayout extends BaseLlayout<MicroClassPresenter> implements IMicroClassView {
    @Bind({2131624489})
    HorizontalScrollMenu hsmContainer;
    @Bind({2131624066})
    ImageView imgReturn;
    private MenuAdapter menuAdapter;
    @Bind({2131624067})
    TextView messageTitle;
    private List<VideoTypeEntity> titleList = new ArrayList();

    public MicroClassLayout(Context context) {
        super(context);
    }

    protected int getLayoutId() {
        return R.layout.layout_weke;
    }

    protected void initPresenter() {
        this.mPresenter = new MicroClassPresenter(this.mContext, this);
    }

    protected void init() {
        this.messageTitle.setText("We课");
        this.hsmContainer.setSwiped(true);
        ((MicroClassPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, new VideoTypeApi());
    }

    public void gettitlelist(List<VideoTypeEntity> result) {
        this.titleList.clear();
        this.titleList.addAll(result);
        this.menuAdapter = new MenuAdapter(this.mContext, this.titleList);
        this.hsmContainer.setAdapter(this.menuAdapter);
    }
}
