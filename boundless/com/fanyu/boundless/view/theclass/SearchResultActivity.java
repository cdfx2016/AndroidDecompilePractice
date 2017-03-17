package com.fanyu.boundless.view.theclass;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.MyActivityManager;
import com.fanyu.boundless.util.CodeCreator;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.google.zxing.WriterException;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class SearchResultActivity extends BaseActivity {
    @Bind({2131624102})
    TextView classboss;
    @Bind({2131624108})
    TextView classgrade;
    @Bind({2131624107})
    RelativeLayout classgradeLayout;
    @Bind({2131624099})
    ImageView classimg;
    @Bind({2131624085})
    TextView classname;
    @Bind({2131624101})
    TextView classnumber;
    private schoolclassentity entity;
    @Bind({2131624100})
    ImageView erweima;
    @Bind({2131624106})
    View gradeView;
    @Bind({2131624067})
    TextView messageTitle;
    @Bind({2131624104})
    RelativeLayout schoolLayout;
    @Bind({2131624103})
    View schoolView;
    @Bind({2131624105})
    TextView schoolname;
    @Bind({2131624112})
    TextView stunumber;
    @Bind({2131624110})
    TextView teachercount;

    protected void initView() {
        setContentView((int) R.layout.activity_search_result);
    }

    protected void initPresenter() {
    }

    protected void init() {
        MyActivityManager.getsInstances().pushActivity(this);
        this.entity = (schoolclassentity) getIntent().getSerializableExtra("entity");
        if (StringUtils.isEmpty(this.entity.getClassname())) {
            this.messageTitle.setText(this.entity.getClassname());
        }
        if (StringUtils.isEmpty(this.entity.getClassnumber() + "")) {
            this.classnumber.setText(this.entity.getClassnumber() + "");
        }
        if (StringUtils.isEmpty(this.entity.getClassname())) {
            this.classname.setText(this.entity.getClassname());
        }
        if (StringUtils.isEmpty(this.entity.getCreatename())) {
            this.classboss.setText(this.entity.getCreatename());
        }
        if (StringUtils.isEmpty(this.entity.getTeanum())) {
            this.teachercount.setText(this.entity.getTeanum());
        }
        if (StringUtils.isEmpty(this.entity.getStunum())) {
            this.stunumber.setText(this.entity.getStunum());
        }
        if (StringUtils.isEmpty(this.entity.getClassgrade())) {
            this.gradeView.setVisibility(0);
            this.classgradeLayout.setVisibility(0);
            this.classgrade.setText(this.entity.getClassgrade());
        }
        if (StringUtils.isEmpty(this.entity.getSchoolname())) {
            this.schoolView.setVisibility(0);
            this.schoolLayout.setVisibility(0);
            this.schoolname.setText(this.entity.getSchoolname());
        }
        ImagePathUtil.getInstance().setImageUrl(this.mContext, this.classimg, this.entity.getClassimg(), new RoundedCornersTransformation(this.mContext, 0, 0));
        createErWeiMa(this.entity.getClassnumber() + "");
    }

    @OnClick({2131624066, 2131624219})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.addclass:
                Intent intent = new Intent(this, SelectClassRoleActivity.class);
                intent.putExtra("classid", this.entity.getId());
                intent.putExtra("classname", this.entity.getClassname());
                startActivity(intent);
                return;
            default:
                return;
        }
    }

    private void createErWeiMa(String text) {
        try {
            this.erweima.setImageBitmap(CodeCreator.createQRCode(text));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
