package com.fanyu.boundless.view.home;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.util.ImagePathUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import im.delight.android.webview.AdvancedWebView;
import im.delight.android.webview.AdvancedWebView.Listener;

public class ShowFuJianActivity extends BaseActivity implements Listener {
    private AdvancedWebView mWebView;
    @Bind({2131624067})
    TextView messageTitle;

    protected void initView() {
        setContentView((int) R.layout.activity_show_fujian);
    }

    protected void initPresenter() {
    }

    protected void init() {
        if (StringUtils.isEmpty(getIntent().getStringExtra(MessageEncoder.ATTR_FILENAME))) {
            this.messageTitle.setText(getIntent().getStringExtra(MessageEncoder.ATTR_FILENAME));
        }
        this.mWebView = (AdvancedWebView) findViewById(R.id.webview);
        this.mWebView.setListener((Activity) this, (Listener) this);
        try {
            this.mWebView.loadUrl("http://dcsapi.com?k=68083677&url=" + ImagePathUtil.getInstance().getPath(getIntent().getStringExtra("fileurl")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPageStarted(String url, Bitmap favicon) {
    }

    public void onPageFinished(String url) {
    }

    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    public void onExternalPageRequest(String url) {
    }

    @OnClick({2131624066})
    public void onClick() {
        finish();
    }
}
