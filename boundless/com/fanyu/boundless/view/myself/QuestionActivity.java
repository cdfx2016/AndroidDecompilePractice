package com.fanyu.boundless.view.myself;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.myself.QuestionApi;
import com.fanyu.boundless.presenter.myself.QuestionPresenter;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;

public class QuestionActivity extends BaseActivity<QuestionPresenter> implements IQuestionView {
    private String describe;
    @Bind({2131624160})
    EditText editdescribe;

    protected void initView() {
        setContentView((int) R.layout.activity_question);
    }

    protected void initPresenter() {
        this.mPresenter = new QuestionPresenter(this.mContext, this);
    }

    protected void init() {
    }

    @OnClick({2131624066, 2131624212})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.edit_fabu:
                this.describe = this.editdescribe.getText().toString();
                if (StringUtils.isEmpty(this.describe)) {
                    QuestionApi questionApi = new QuestionApi();
                    questionApi.setQuestioncontent(this.describe);
                    ((QuestionPresenter) this.mPresenter).startPost(this, questionApi);
                    return;
                }
                Toast.makeText(this, "请您填写宝贵意见！", 0).show();
                return;
            default:
                return;
        }
    }

    public void isAdd(String isadd) {
        if (isadd != null) {
            Toast.makeText(this, "提交成功", 1).show();
            finish();
            return;
        }
        Toast.makeText(this, "提交失败", 1).show();
    }
}
