package com.fanyu.boundless.view.myself;

import android.text.Selection;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.myself.Tsuser;
import com.fanyu.boundless.bean.myself.UpdateXinXiApi;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.myself.UpdateXinXiPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.UpdateNameEvent;
import de.greenrobot.event.EventBus;
import org.jivesoftware.smackx.packet.DiscoverItems.Item;

public class UpdateNickNameActivity extends BaseActivity<UpdateXinXiPresenter> implements IUpdateXinXIView {
    @Bind({2131624261})
    EditText editContext;
    private SharedPreferencesUtil msharedPreference;
    private String newname;
    private String userid;
    private String username;

    protected void initView() {
        setContentView((int) R.layout.activity_update_nickname);
    }

    protected void initPresenter() {
        this.mPresenter = new UpdateXinXiPresenter(this.mContext, this);
    }

    protected void init() {
        this.msharedPreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharedPreference.getString(Preferences.USER_ID, "");
        this.username = this.msharedPreference.getString(Preferences.NICKNAME, "");
        this.editContext.setText(this.username);
        Selection.setSelection(this.editContext.getText(), this.editContext.length());
    }

    @OnClick({2131624066, 2131624260})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.txt_save:
                this.newname = this.editContext.getText().toString();
                if (this.newname.equals(this.username) || "".equals(this.newname)) {
                    Toast.makeText(this, "修改内容不能为空或与上次内容相同！", 0).show();
                    return;
                }
                UpdateXinXiApi updateXinXiApi = new UpdateXinXiApi();
                updateXinXiApi.setTableName("tsuser");
                updateXinXiApi.setFileName(Preferences.NICKNAME);
                updateXinXiApi.setFileValue(this.newname);
                updateXinXiApi.setId(this.userid);
                ((UpdateXinXiPresenter) this.mPresenter).startPost(this, updateXinXiApi);
                return;
            default:
                return;
        }
    }

    public void isupdate(String isupdate, String state) {
        if (isupdate.equals("true")) {
            Toast.makeText(this, "修改成功", 0).show();
            SharedPreferencesUtil.getsInstances(this.mContext).putString(Preferences.NICKNAME, this.newname);
            EventBus.getDefault().post(new UpdateNameEvent(Item.UPDATE_ACTION));
            finish();
            return;
        }
        Toast.makeText(this, "修改失败", 0).show();
    }

    public void getMyXinXi(Tsuser tsuser) {
    }
}
