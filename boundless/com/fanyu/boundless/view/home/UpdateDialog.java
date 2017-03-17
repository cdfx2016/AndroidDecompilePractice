package com.fanyu.boundless.view.home;

import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.Update;
import com.fanyu.boundless.bean.home.UpdateApi;
import com.fanyu.boundless.common.adapter.CommonAdapter;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.presenter.home.UpdatePresenter;
import com.fanyu.boundless.service.UpdateService;
import com.fanyu.boundless.util.StringUtils;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateDialog extends Dialog implements IUpdateView {
    private CommonAdapter<String> adapter;
    @Bind({2131624338})
    Button btnCancel;
    private boolean btnCancelFlag = true;
    private boolean btnFlag = true;
    @Bind({2131624339})
    Button btnOk;
    private List<String> contentlist;
    private Context context;
    @Bind({2131624335})
    LinearLayout llRoot;
    private UpdatePresenter mPresenter;
    private boolean mService;
    private Update mUpdate;
    @Bind({2131624337})
    RecyclerView rvContent;
    @Bind({2131624027})
    TextView title;

    private class ContentAdapter extends CommonAdapter<String> {
        public ContentAdapter(Context context, int layoutId, List<String> datas) {
            super(context, layoutId, datas);
        }

        public void convert(ViewHolder holder, String s, int position) {
            holder.setText(R.id.tv_content, s);
        }
    }

    public UpdateDialog(Context context) {
        super(context, R.style.dialog_no_title);
        this.context = context;
    }

    public UpdateDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected UpdateDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutParams lp = getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        getWindow().setAttributes(lp);
        getWindow().addFlags(2);
        setContentView(R.layout.dialog_update);
        ButterKnife.bind((Dialog) this);
        this.contentlist = new ArrayList();
        this.mPresenter = new UpdatePresenter(this.context, this);
        UpdateApi updateApi = new UpdateApi();
        updateApi.setSystem("android");
        this.mPresenter.startPost((RxAppCompatActivity) this.context, updateApi);
        this.rvContent.setLayoutManager(new LinearLayoutManager(this.context));
        this.adapter = new ContentAdapter(this.context, R.layout.adapter_versionupdate, this.contentlist);
        this.rvContent.setAdapter(this.adapter);
    }

    @OnClick({2131624339, 2131624335, 2131624336, 2131624338})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                if (!this.mUpdate.getMethod().equals("0")) {
                    dismiss();
                    return;
                } else if (this.btnFlag) {
                    System.exit(0);
                    return;
                } else {
                    return;
                }
            case R.id.btn_ok:
                Intent intent;
                if (VERSION.SDK_INT >= 24) {
                    intent = new Intent("android.intent.action.VIEW");
                    intent.setData(Uri.parse(this.mUpdate.getApp_url()));
                    this.context.startActivity(intent);
                    return;
                } else if (!StringUtils.isEmpty(this.mUpdate.getMethod())) {
                    return;
                } else {
                    if (this.mUpdate.getMethod().equals("0")) {
                        if (this.btnFlag) {
                            this.context.startService(new Intent(this.context, UpdateService.class));
                            this.btnFlag = false;
                        }
                        this.btnCancelFlag = false;
                        return;
                    } else if (this.mUpdate.getMethod().equals("1")) {
                        this.context.startService(new Intent(this.context, UpdateService.class));
                        dismiss();
                        return;
                    } else if (this.mUpdate.getMethod().equals("2")) {
                        intent = new Intent("android.intent.action.VIEW");
                        intent.setData(Uri.parse(this.mUpdate.getApp_url()));
                        this.context.startActivity(intent);
                        dismiss();
                        return;
                    } else {
                        return;
                    }
                }
            default:
                return;
        }
    }

    private boolean ServiceIsStart(List<RunningServiceInfo> list, String className) {
        for (int i = 0; i < list.size(); i++) {
            if (className.equals(((RunningServiceInfo) list.get(i)).service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void showTip(String msg) {
    }

    public void loadSuccess(Object object) {
    }

    public void loadFailure(String errorMsg) {
    }

    public void showLoadingDialog() {
    }

    public void closeLoadingDialog() {
    }

    public void getUpdate(Update update) {
        if (update != null) {
            this.mUpdate = update;
            this.title.setText("升级提醒V" + update.getVersion_name());
            this.contentlist.addAll(Arrays.asList(update.getRenew().split("\\|")));
            if (update.getMethod().equals("0")) {
                this.btnCancel.setText("退出");
            } else {
                this.btnCancel.setText("稍后再说");
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    public void onBackPressed() {
        if (!this.mUpdate.getMethod().equals("0")) {
            super.onBackPressed();
        }
    }
}
