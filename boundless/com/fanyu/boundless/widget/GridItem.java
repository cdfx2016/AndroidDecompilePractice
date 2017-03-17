package com.fanyu.boundless.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fanyu.boundless.R;

public class GridItem extends RelativeLayout implements Checkable {
    private TextView bianhao;
    private boolean mChecked;
    private Context mContext;
    private TextView mImgView;
    private RelativeLayout nicaicai;

    public GridItem(Context context) {
        this(context, null, 0);
    }

    public GridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mImgView = null;
        this.bianhao = null;
        this.mContext = context;
        LayoutInflater.from(this.mContext).inflate(R.layout.grid_item, this);
        this.mImgView = (TextView) findViewById(R.id.name);
        this.nicaicai = (RelativeLayout) findViewById(R.id.nicaicai);
        this.bianhao = (TextView) findViewById(R.id.bianhao);
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
        if (this.mChecked) {
            this.nicaicai.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ico_xuankuang));
        } else {
            this.nicaicai.setBackgroundDrawable(getResources().getDrawable(R.mipmap.xuesheng_bj));
        }
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void toggle() {
        setChecked(!this.mChecked);
    }

    public void setTextName(String name) {
        if (this.mImgView != null) {
            this.mImgView.setText(name);
        }
    }

    public void setBianHao(String name) {
        if (this.bianhao != null) {
            this.bianhao.setText(name);
        }
    }
}
