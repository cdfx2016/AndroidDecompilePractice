package com.fanyu.boundless.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.fanyu.boundless.R;

public class MyProgressDialog extends ProgressDialog {
    private Context context;

    public MyProgressDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this.context, R.layout.progressdialog_jiazai, null);
        setContentView(view);
        view.setClickable(false);
    }
}
