package im.delight.android.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.finalteam.toolsfinal.io.IOUtils;
import com.fanyu.boundless.util.FileUtil;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

public class AdvancedWebView extends WebView {
    protected static final String[] ALTERNATIVE_BROWSERS = new String[]{"org.mozilla.firefox", "com.android.chrome", "com.opera.browser", "org.mozilla.firefox_beta", "com.chrome.beta", "com.opera.browser.beta"};
    protected static final String CHARSET_DEFAULT = "UTF-8";
    protected static final String DATABASES_SUB_FOLDER = "/databases";
    protected static final String LANGUAGE_DEFAULT_ISO3 = "eng";
    public static final String PACKAGE_NAME_DOWNLOAD_MANAGER = "com.android.providers.downloads";
    protected static final int REQUEST_CODE_FILE_PICKER = 51426;
    protected WeakReference<Activity> mActivity;
    protected WebChromeClient mCustomWebChromeClient;
    protected WebViewClient mCustomWebViewClient;
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;
    protected WeakReference<Fragment> mFragment;
    protected boolean mGeolocationEnabled;
    protected final Map<String, String> mHttpHeaders = new HashMap();
    protected String mLanguageIso3;
    protected long mLastError;
    protected Listener mListener;
    protected final List<String> mPermittedHostnames = new LinkedList();
    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;
    protected String mUploadableFileTypes = "*/*";

    public interface Listener {
        void onDownloadRequested(String str, String str2, String str3, long j, String str4, String str5);

        void onExternalPageRequest(String str);

        void onPageError(int i, String str, String str2);

        void onPageFinished(String str);

        void onPageStarted(String str, Bitmap bitmap);
    }

    public static class Browsers {
        private static String mAlternativePackage;

        public static boolean hasAlternative(Context context) {
            return getAlternative(context) != null;
        }

        public static String getAlternative(Context context) {
            if (mAlternativePackage != null) {
                return mAlternativePackage;
            }
            List<String> alternativeBrowsers = Arrays.asList(AdvancedWebView.ALTERNATIVE_BROWSERS);
            for (ApplicationInfo app : context.getPackageManager().getInstalledApplications(128)) {
                if (app.enabled && alternativeBrowsers.contains(app.packageName)) {
                    mAlternativePackage = app.packageName;
                    return app.packageName;
                }
            }
            return null;
        }

        public static void openUrl(Activity context, String url) {
            openUrl(context, url, false);
        }

        public static void openUrl(Activity context, String url, boolean withoutTransition) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            intent.setPackage(getAlternative(context));
            intent.addFlags(268435456);
            context.startActivity(intent);
            if (withoutTransition) {
                context.overridePendingTransition(0, 0);
            }
        }
    }

    public AdvancedWebView(Context context) {
        super(context);
        init(context);
    }

    public AdvancedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AdvancedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setListener(Activity activity, Listener listener) {
        setListener(activity, listener, (int) REQUEST_CODE_FILE_PICKER);
    }

    public void setListener(Activity activity, Listener listener, int requestCodeFilePicker) {
        if (activity != null) {
            this.mActivity = new WeakReference(activity);
        } else {
            this.mActivity = null;
        }
        setListener(listener, requestCodeFilePicker);
    }

    public void setListener(Fragment fragment, Listener listener) {
        setListener(fragment, listener, (int) REQUEST_CODE_FILE_PICKER);
    }

    public void setListener(Fragment fragment, Listener listener, int requestCodeFilePicker) {
        if (fragment != null) {
            this.mFragment = new WeakReference(fragment);
        } else {
            this.mFragment = null;
        }
        setListener(listener, requestCodeFilePicker);
    }

    protected void setListener(Listener listener, int requestCodeFilePicker) {
        this.mListener = listener;
        this.mRequestCodeFilePicker = requestCodeFilePicker;
    }

    public void setWebViewClient(WebViewClient client) {
        this.mCustomWebViewClient = client;
    }

    public void setWebChromeClient(WebChromeClient client) {
        this.mCustomWebChromeClient = client;
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    public void setGeolocationEnabled(boolean enabled) {
        if (enabled) {
            getSettings().setJavaScriptEnabled(true);
            getSettings().setGeolocationEnabled(true);
            setGeolocationDatabasePath();
        }
        this.mGeolocationEnabled = enabled;
    }

    @SuppressLint({"NewApi"})
    protected void setGeolocationDatabasePath() {
        Activity activity;
        if (this.mFragment != null && this.mFragment.get() != null && VERSION.SDK_INT >= 11 && ((Fragment) this.mFragment.get()).getActivity() != null) {
            activity = ((Fragment) this.mFragment.get()).getActivity();
        } else if (this.mActivity != null && this.mActivity.get() != null) {
            activity = (Activity) this.mActivity.get();
        } else {
            return;
        }
        getSettings().setGeolocationDatabasePath(activity.getFilesDir().getPath());
    }

    public void setUploadableFileTypes(String mimeType) {
        this.mUploadableFileTypes = mimeType;
    }

    public void loadHtml(String html) {
        loadHtml(html, null);
    }

    public void loadHtml(String html, String baseUrl) {
        loadHtml(html, baseUrl, null);
    }

    public void loadHtml(String html, String baseUrl, String historyUrl) {
        loadHtml(html, baseUrl, historyUrl, "utf-8");
    }

    public void loadHtml(String html, String baseUrl, String historyUrl, String encoding) {
        loadDataWithBaseURL(baseUrl, html, "text/html", encoding, historyUrl);
    }

    @SuppressLint({"NewApi"})
    public void onResume() {
        if (VERSION.SDK_INT >= 11) {
            super.onResume();
        }
        resumeTimers();
    }

    @SuppressLint({"NewApi"})
    public void onPause() {
        pauseTimers();
        if (VERSION.SDK_INT >= 11) {
            super.onPause();
        }
    }

    public void onDestroy() {
        try {
            ((ViewGroup) getParent()).removeView(this);
        } catch (Exception e) {
        }
        try {
            removeAllViews();
        } catch (Exception e2) {
        }
        destroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != this.mRequestCodeFilePicker) {
            return;
        }
        if (resultCode == -1) {
            if (intent == null) {
                return;
            }
            if (this.mFileUploadCallbackFirst != null) {
                this.mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                this.mFileUploadCallbackFirst = null;
            } else if (this.mFileUploadCallbackSecond != null) {
                Uri[] dataUris = null;
                try {
                    if (intent.getDataString() != null) {
                        dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                    } else if (VERSION.SDK_INT >= 16 && intent.getClipData() != null) {
                        int numSelectedFiles = intent.getClipData().getItemCount();
                        dataUris = new Uri[numSelectedFiles];
                        for (int i = 0; i < numSelectedFiles; i++) {
                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                        }
                    }
                } catch (Exception e) {
                }
                this.mFileUploadCallbackSecond.onReceiveValue(dataUris);
                this.mFileUploadCallbackSecond = null;
            }
        } else if (this.mFileUploadCallbackFirst != null) {
            this.mFileUploadCallbackFirst.onReceiveValue(null);
            this.mFileUploadCallbackFirst = null;
        } else if (this.mFileUploadCallbackSecond != null) {
            this.mFileUploadCallbackSecond.onReceiveValue(null);
            this.mFileUploadCallbackSecond = null;
        }
    }

    public void addHttpHeader(String name, String value) {
        this.mHttpHeaders.put(name, value);
    }

    public void removeHttpHeader(String name) {
        this.mHttpHeaders.remove(name);
    }

    public void addPermittedHostname(String hostname) {
        this.mPermittedHostnames.add(hostname);
    }

    public void addPermittedHostnames(Collection<? extends String> collection) {
        this.mPermittedHostnames.addAll(collection);
    }

    public List<String> getPermittedHostnames() {
        return this.mPermittedHostnames;
    }

    public void removePermittedHostname(String hostname) {
        this.mPermittedHostnames.remove(hostname);
    }

    public void clearPermittedHostnames() {
        this.mPermittedHostnames.clear();
    }

    public boolean onBackPressed() {
        if (!canGoBack()) {
            return true;
        }
        goBack();
        return false;
    }

    @SuppressLint({"NewApi"})
    protected static void setAllowAccessFromFileUrls(WebSettings webSettings, boolean allowed) {
        if (VERSION.SDK_INT >= 16) {
            webSettings.setAllowFileAccessFromFileURLs(allowed);
            webSettings.setAllowUniversalAccessFromFileURLs(allowed);
        }
    }

    public void setCookiesEnabled(boolean enabled) {
        CookieManager.getInstance().setAcceptCookie(enabled);
    }

    @SuppressLint({"NewApi"})
    public void setThirdPartyCookiesEnabled(boolean enabled) {
        if (VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, enabled);
        }
    }

    public void setMixedContentAllowed(boolean allowed) {
        setMixedContentAllowed(getSettings(), allowed);
    }

    @SuppressLint({"NewApi"})
    protected void setMixedContentAllowed(WebSettings webSettings, boolean allowed) {
        if (VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(allowed ? 0 : 1);
        }
    }

    public void setDesktopMode(boolean enabled) {
        String newUserAgent;
        WebSettings webSettings = getSettings();
        if (enabled) {
            newUserAgent = webSettings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA");
        } else {
            newUserAgent = webSettings.getUserAgentString().replace("eliboM", "Mobile").replace("diordnA", "Android");
        }
        webSettings.setUserAgentString(newUserAgent);
        webSettings.setUseWideViewPort(enabled);
        webSettings.setLoadWithOverviewMode(enabled);
        webSettings.setSupportZoom(enabled);
        webSettings.setBuiltInZoomControls(enabled);
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    protected void init(Context context) {
        if (!isInEditMode()) {
            if (context instanceof Activity) {
                this.mActivity = new WeakReference((Activity) context);
            }
            this.mLanguageIso3 = getLanguageIso3();
            setFocusable(true);
            setFocusableInTouchMode(true);
            setSaveEnabled(true);
            String filesDir = context.getFilesDir().getPath();
            String databaseDir = filesDir.substring(0, filesDir.lastIndexOf("/")) + DATABASES_SUB_FOLDER;
            WebSettings webSettings = getSettings();
            webSettings.setAllowFileAccess(false);
            setAllowAccessFromFileUrls(webSettings, false);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            if (VERSION.SDK_INT < 18) {
                webSettings.setRenderPriority(RenderPriority.HIGH);
            }
            webSettings.setDatabaseEnabled(true);
            if (VERSION.SDK_INT < 19) {
                webSettings.setDatabasePath(databaseDir);
            }
            setMixedContentAllowed(webSettings, true);
            setThirdPartyCookiesEnabled(true);
            super.setWebViewClient(new WebViewClient() {
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if (!(AdvancedWebView.this.hasError() || AdvancedWebView.this.mListener == null)) {
                        AdvancedWebView.this.mListener.onPageStarted(url, favicon);
                    }
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onPageStarted(view, url, favicon);
                    }
                }

                public void onPageFinished(WebView view, String url) {
                    if (!(AdvancedWebView.this.hasError() || AdvancedWebView.this.mListener == null)) {
                        AdvancedWebView.this.mListener.onPageFinished(url);
                    }
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onPageFinished(view, url);
                    }
                }

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    AdvancedWebView.this.setLastError();
                    if (AdvancedWebView.this.mListener != null) {
                        AdvancedWebView.this.mListener.onPageError(errorCode, description, failingUrl);
                    }
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
                    }
                }

                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (AdvancedWebView.this.isHostnameAllowed(url)) {
                        if (AdvancedWebView.this.mCustomWebViewClient == null || !AdvancedWebView.this.mCustomWebViewClient.shouldOverrideUrlLoading(view, url)) {
                            view.loadUrl(url);
                        }
                    } else if (AdvancedWebView.this.mListener != null) {
                        AdvancedWebView.this.mListener.onExternalPageRequest(url);
                    }
                    return true;
                }

                public void onLoadResource(WebView view, String url) {
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onLoadResource(view, url);
                    } else {
                        super.onLoadResource(view, url);
                    }
                }

                @SuppressLint({"NewApi"})
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    if (VERSION.SDK_INT < 11) {
                        return null;
                    }
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        return AdvancedWebView.this.mCustomWebViewClient.shouldInterceptRequest(view, url);
                    }
                    return super.shouldInterceptRequest(view, url);
                }

                @SuppressLint({"NewApi"})
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    if (VERSION.SDK_INT < 21) {
                        return null;
                    }
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        return AdvancedWebView.this.mCustomWebViewClient.shouldInterceptRequest(view, request);
                    }
                    return super.shouldInterceptRequest(view, request);
                }

                public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onFormResubmission(view, dontResend, resend);
                    } else {
                        super.onFormResubmission(view, dontResend, resend);
                    }
                }

                public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.doUpdateVisitedHistory(view, url, isReload);
                    } else {
                        super.doUpdateVisitedHistory(view, url, isReload);
                    }
                }

                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onReceivedSslError(view, handler, error);
                    } else {
                        super.onReceivedSslError(view, handler, error);
                    }
                }

                @SuppressLint({"NewApi"})
                public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                    if (VERSION.SDK_INT < 21) {
                        return;
                    }
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onReceivedClientCertRequest(view, request);
                    } else {
                        super.onReceivedClientCertRequest(view, request);
                    }
                }

                public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
                    } else {
                        super.onReceivedHttpAuthRequest(view, handler, host, realm);
                    }
                }

                public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        return AdvancedWebView.this.mCustomWebViewClient.shouldOverrideKeyEvent(view, event);
                    }
                    return super.shouldOverrideKeyEvent(view, event);
                }

                public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onUnhandledKeyEvent(view, event);
                    } else {
                        super.onUnhandledKeyEvent(view, event);
                    }
                }

                @SuppressLint({"NewApi"})
                public void onUnhandledInputEvent(WebView view, InputEvent event) {
                    if (VERSION.SDK_INT < 21) {
                        return;
                    }
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onUnhandledInputEvent(view, event);
                    } else {
                        super.onUnhandledInputEvent(view, event);
                    }
                }

                public void onScaleChanged(WebView view, float oldScale, float newScale) {
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onScaleChanged(view, oldScale, newScale);
                    } else {
                        super.onScaleChanged(view, oldScale, newScale);
                    }
                }

                @SuppressLint({"NewApi"})
                public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                    if (VERSION.SDK_INT < 12) {
                        return;
                    }
                    if (AdvancedWebView.this.mCustomWebViewClient != null) {
                        AdvancedWebView.this.mCustomWebViewClient.onReceivedLoginRequest(view, realm, account, args);
                    } else {
                        super.onReceivedLoginRequest(view, realm, account, args);
                    }
                }
            });
            super.setWebChromeClient(new WebChromeClient() {
                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    openFileChooser(uploadMsg, null);
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                    openFileChooser(uploadMsg, acceptType, null);
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    AdvancedWebView.this.openFileInput(uploadMsg, null, false);
                }

                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    boolean allowMultiple = false;
                    if (VERSION.SDK_INT < 21) {
                        return false;
                    }
                    if (fileChooserParams.getMode() == 1) {
                        allowMultiple = true;
                    }
                    AdvancedWebView.this.openFileInput(null, filePathCallback, allowMultiple);
                    return true;
                }

                public void onProgressChanged(WebView view, int newProgress) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onProgressChanged(view, newProgress);
                    } else {
                        super.onProgressChanged(view, newProgress);
                    }
                }

                public void onReceivedTitle(WebView view, String title) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onReceivedTitle(view, title);
                    } else {
                        super.onReceivedTitle(view, title);
                    }
                }

                public void onReceivedIcon(WebView view, Bitmap icon) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onReceivedIcon(view, icon);
                    } else {
                        super.onReceivedIcon(view, icon);
                    }
                }

                public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
                    } else {
                        super.onReceivedTouchIconUrl(view, url, precomposed);
                    }
                }

                public void onShowCustomView(View view, CustomViewCallback callback) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onShowCustomView(view, callback);
                    } else {
                        super.onShowCustomView(view, callback);
                    }
                }

                @SuppressLint({"NewApi"})
                public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                    if (VERSION.SDK_INT < 14) {
                        return;
                    }
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onShowCustomView(view, requestedOrientation, callback);
                    } else {
                        super.onShowCustomView(view, requestedOrientation, callback);
                    }
                }

                public void onHideCustomView() {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onHideCustomView();
                    } else {
                        super.onHideCustomView();
                    }
                }

                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                    }
                    return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
                }

                public void onRequestFocus(WebView view) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onRequestFocus(view);
                    } else {
                        super.onRequestFocus(view);
                    }
                }

                public void onCloseWindow(WebView window) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onCloseWindow(window);
                    } else {
                        super.onCloseWindow(window);
                    }
                }

                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.onJsAlert(view, url, message, result);
                    }
                    return super.onJsAlert(view, url, message, result);
                }

                public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.onJsConfirm(view, url, message, result);
                    }
                    return super.onJsConfirm(view, url, message, result);
                }

                public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.onJsPrompt(view, url, message, defaultValue, result);
                    }
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }

                public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.onJsBeforeUnload(view, url, message, result);
                    }
                    return super.onJsBeforeUnload(view, url, message, result);
                }

                public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
                    if (AdvancedWebView.this.mGeolocationEnabled) {
                        callback.invoke(origin, true, false);
                    } else if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);
                    } else {
                        super.onGeolocationPermissionsShowPrompt(origin, callback);
                    }
                }

                public void onGeolocationPermissionsHidePrompt() {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onGeolocationPermissionsHidePrompt();
                    } else {
                        super.onGeolocationPermissionsHidePrompt();
                    }
                }

                @SuppressLint({"NewApi"})
                public void onPermissionRequest(PermissionRequest request) {
                    if (VERSION.SDK_INT < 21) {
                        return;
                    }
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onPermissionRequest(request);
                    } else {
                        super.onPermissionRequest(request);
                    }
                }

                @SuppressLint({"NewApi"})
                public void onPermissionRequestCanceled(PermissionRequest request) {
                    if (VERSION.SDK_INT < 21) {
                        return;
                    }
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onPermissionRequestCanceled(request);
                    } else {
                        super.onPermissionRequestCanceled(request);
                    }
                }

                public boolean onJsTimeout() {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.onJsTimeout();
                    }
                    return super.onJsTimeout();
                }

                public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onConsoleMessage(message, lineNumber, sourceID);
                    } else {
                        super.onConsoleMessage(message, lineNumber, sourceID);
                    }
                }

                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.onConsoleMessage(consoleMessage);
                    }
                    return super.onConsoleMessage(consoleMessage);
                }

                public Bitmap getDefaultVideoPoster() {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.getDefaultVideoPoster();
                    }
                    return super.getDefaultVideoPoster();
                }

                public View getVideoLoadingProgressView() {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        return AdvancedWebView.this.mCustomWebChromeClient.getVideoLoadingProgressView();
                    }
                    return super.getVideoLoadingProgressView();
                }

                public void getVisitedHistory(ValueCallback<String[]> callback) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.getVisitedHistory(callback);
                    } else {
                        super.getVisitedHistory(callback);
                    }
                }

                public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, QuotaUpdater quotaUpdater) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
                    } else {
                        super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
                    }
                }

                public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
                    if (AdvancedWebView.this.mCustomWebChromeClient != null) {
                        AdvancedWebView.this.mCustomWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
                    } else {
                        super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
                    }
                }
            });
            setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                    String suggestedFilename = URLUtil.guessFileName(url, contentDisposition, mimeType);
                    if (AdvancedWebView.this.mListener != null) {
                        AdvancedWebView.this.mListener.onDownloadRequested(url, suggestedFilename, mimeType, contentLength, contentDisposition, userAgent);
                    }
                }
            });
        }
    }

    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        if (additionalHttpHeaders == null) {
            additionalHttpHeaders = this.mHttpHeaders;
        } else if (this.mHttpHeaders.size() > 0) {
            additionalHttpHeaders.putAll(this.mHttpHeaders);
        }
        super.loadUrl(url, additionalHttpHeaders);
    }

    public void loadUrl(String url) {
        if (this.mHttpHeaders.size() > 0) {
            super.loadUrl(url, this.mHttpHeaders);
        } else {
            super.loadUrl(url);
        }
    }

    public void loadUrl(String url, boolean preventCaching) {
        if (preventCaching) {
            url = makeUrlUnique(url);
        }
        loadUrl(url);
    }

    public void loadUrl(String url, boolean preventCaching, Map<String, String> additionalHttpHeaders) {
        if (preventCaching) {
            url = makeUrlUnique(url);
        }
        loadUrl(url, (Map) additionalHttpHeaders);
    }

    protected static String makeUrlUnique(String url) {
        StringBuilder unique = new StringBuilder();
        unique.append(url);
        if (url.contains("?")) {
            unique.append('&');
        } else {
            if (url.lastIndexOf(47) <= 7) {
                unique.append(IOUtils.DIR_SEPARATOR_UNIX);
            }
            unique.append('?');
        }
        unique.append(System.currentTimeMillis());
        unique.append('=');
        unique.append(1);
        return unique.toString();
    }

    protected boolean isHostnameAllowed(String url) {
        if (this.mPermittedHostnames.size() == 0) {
            return true;
        }
        String actualHost = Uri.parse(url).getHost();
        for (String expectedHost : this.mPermittedHostnames) {
            if (actualHost.equals(expectedHost)) {
                return true;
            }
            if (actualHost.endsWith(FileUtil.FILE_EXTENSION_SEPARATOR + expectedHost)) {
                return true;
            }
        }
        return false;
    }

    protected void setLastError() {
        this.mLastError = System.currentTimeMillis();
    }

    protected boolean hasError() {
        return this.mLastError + 500 >= System.currentTimeMillis();
    }

    protected static String getLanguageIso3() {
        try {
            return Locale.getDefault().getISO3Language().toLowerCase(Locale.US);
        } catch (MissingResourceException e) {
            return LANGUAGE_DEFAULT_ISO3;
        }
    }

    protected String getFileUploadPromptLabel() {
        try {
            if (this.mLanguageIso3.equals("zho")) {
                return decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2");
            }
            if (this.mLanguageIso3.equals("spa")) {
                return decodeBase64("RWxpamEgdW4gYXJjaGl2bw==");
            }
            if (this.mLanguageIso3.equals("hin")) {
                return decodeBase64("4KSP4KSVIOCkq+CkvOCkvuCkh+CksiDgpJrgpYHgpKjgpYfgpII=");
            }
            if (this.mLanguageIso3.equals("ben")) {
                return decodeBase64("4KaP4KaV4Kaf4Ka/IOCmq+CmvuCmh+CmsiDgpqjgpr/gprDgp43gpqzgpr7gpprgpqg=");
            }
            if (this.mLanguageIso3.equals("ara")) {
                return decodeBase64("2KfYrtiq2YrYp9ixINmF2YTZgSDZiNin2K3Yrw==");
            }
            if (this.mLanguageIso3.equals("por")) {
                return decodeBase64("RXNjb2xoYSB1bSBhcnF1aXZv");
            }
            if (this.mLanguageIso3.equals("rus")) {
                return decodeBase64("0JLRi9Cx0LXRgNC40YLQtSDQvtC00LjQvSDRhNCw0LnQuw==");
            }
            if (this.mLanguageIso3.equals("jpn")) {
                return decodeBase64("MeODleOCoeOCpOODq+OCkumBuOaKnuOBl+OBpuOBj+OBoOOBleOBhA==");
            }
            if (this.mLanguageIso3.equals("pan")) {
                return decodeBase64("4KiH4Kmx4KiVIOCoq+CovuCoh+CosiDgqJrgqYHgqKPgqYs=");
            }
            if (this.mLanguageIso3.equals("deu")) {
                return decodeBase64("V8OkaGxlIGVpbmUgRGF0ZWk=");
            }
            if (this.mLanguageIso3.equals("jav")) {
                return decodeBase64("UGlsaWggc2lqaSBiZXJrYXM=");
            }
            if (this.mLanguageIso3.equals("msa")) {
                return decodeBase64("UGlsaWggc2F0dSBmYWls");
            }
            if (this.mLanguageIso3.equals("tel")) {
                return decodeBase64("4LCS4LCVIOCwq+CxhuCxluCwsuCxjeCwqOCxgSDgsI7gsILgsJrgsYHgsJXgsYvgsILgsKHgsL8=");
            }
            if (this.mLanguageIso3.equals("vie")) {
                return decodeBase64("Q2jhu41uIG3hu5l0IHThuq1wIHRpbg==");
            }
            if (this.mLanguageIso3.equals("kor")) {
                return decodeBase64("7ZWY64KY7J2YIO2MjOydvOydhCDshKDtg50=");
            }
            if (this.mLanguageIso3.equals("fra")) {
                return decodeBase64("Q2hvaXNpc3NleiB1biBmaWNoaWVy");
            }
            if (this.mLanguageIso3.equals("mar")) {
                return decodeBase64("4KSr4KS+4KSH4KSyIOCkqOCkv+CkteCkoeCkvg==");
            }
            if (this.mLanguageIso3.equals("tam")) {
                return decodeBase64("4K6S4K6w4K+BIOCuleCvh+CuvuCuquCvjeCuquCviCDgrqTgr4fgrrDgr43grrXgr4E=");
            }
            if (this.mLanguageIso3.equals("urd")) {
                return decodeBase64("2KfbjNqpINmB2KfYptmEINmF24zauiDYs9uSINin2YbYqtiu2KfYqCDaqdix24zaug==");
            }
            if (this.mLanguageIso3.equals("fas")) {
                return decodeBase64("2LHYpyDYp9mG2KrYrtin2Kgg2qnZhtuM2K8g24zaqSDZgdin24zZhA==");
            }
            if (this.mLanguageIso3.equals("tur")) {
                return decodeBase64("QmlyIGRvc3lhIHNlw6dpbg==");
            }
            if (this.mLanguageIso3.equals("ita")) {
                return decodeBase64("U2NlZ2xpIHVuIGZpbGU=");
            }
            if (this.mLanguageIso3.equals("tha")) {
                return decodeBase64("4LmA4Lil4Li34Lit4LiB4LmE4Lif4Lil4LmM4Lir4LiZ4Li24LmI4LiH");
            }
            if (this.mLanguageIso3.equals("guj")) {
                return decodeBase64("4KqP4KqVIOCqq+CqvuCqh+CqsuCqqOCrhyDgqqrgqrjgqoLgqqY=");
            }
            return "Choose a file";
        } catch (Exception e) {
        }
    }

    protected static String decodeBase64(String base64) throws IllegalArgumentException, UnsupportedEncodingException {
        return new String(Base64.decode(base64, 0), "UTF-8");
    }

    @SuppressLint({"NewApi"})
    protected void openFileInput(ValueCallback<Uri> fileUploadCallbackFirst, ValueCallback<Uri[]> fileUploadCallbackSecond, boolean allowMultiple) {
        if (this.mFileUploadCallbackFirst != null) {
            this.mFileUploadCallbackFirst.onReceiveValue(null);
        }
        this.mFileUploadCallbackFirst = fileUploadCallbackFirst;
        if (this.mFileUploadCallbackSecond != null) {
            this.mFileUploadCallbackSecond.onReceiveValue(null);
        }
        this.mFileUploadCallbackSecond = fileUploadCallbackSecond;
        Intent i = new Intent("android.intent.action.GET_CONTENT");
        i.addCategory("android.intent.category.OPENABLE");
        if (allowMultiple && VERSION.SDK_INT >= 18) {
            i.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
        }
        i.setType(this.mUploadableFileTypes);
        if (this.mFragment != null && this.mFragment.get() != null && VERSION.SDK_INT >= 11) {
            ((Fragment) this.mFragment.get()).startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), this.mRequestCodeFilePicker);
        } else if (this.mActivity != null && this.mActivity.get() != null) {
            ((Activity) this.mActivity.get()).startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), this.mRequestCodeFilePicker);
        }
    }

    public static boolean isFileUploadAvailable() {
        return isFileUploadAvailable(false);
    }

    public static boolean isFileUploadAvailable(boolean needsCorrectMimeType) {
        if (VERSION.SDK_INT != 19) {
            return true;
        }
        String platformVersion = VERSION.RELEASE == null ? "" : VERSION.RELEASE;
        if (needsCorrectMimeType || (!platformVersion.startsWith("4.4.3") && !platformVersion.startsWith("4.4.4"))) {
            return false;
        }
        return true;
    }

    @SuppressLint({"NewApi"})
    public static boolean handleDownload(Context context, String fromUrl, String toFilename) {
        if (VERSION.SDK_INT < 9) {
            throw new RuntimeException("Method requires API level 9 or above");
        }
        Request request = new Request(Uri.parse(fromUrl));
        if (VERSION.SDK_INT >= 11) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(1);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, toFilename);
        DownloadManager dm = (DownloadManager) context.getSystemService("download");
        try {
            dm.enqueue(request);
            return true;
        } catch (SecurityException e) {
            try {
                if (VERSION.SDK_INT >= 11) {
                    request.setNotificationVisibility(0);
                }
                dm.enqueue(request);
                return true;
            } catch (IllegalArgumentException e2) {
                openAppSettings(context, PACKAGE_NAME_DOWNLOAD_MANAGER);
                return false;
            }
        }
    }

    @SuppressLint({"NewApi"})
    private static boolean openAppSettings(Context context, String packageName) {
        if (VERSION.SDK_INT < 9) {
            throw new RuntimeException("Method requires API level 9 or above");
        }
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + packageName));
            intent.setFlags(268435456);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
