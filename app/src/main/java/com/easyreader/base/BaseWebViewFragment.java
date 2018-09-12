package com.easyreader.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easyreader.R;
import com.easyreader.utils.CommonUtils;


/**
 * Created by 513419 on 2017/12/6.
 * 这是实现webview基本配置的fragment，所有需要使用webview的fragment都可继承于BaseWebViewFragment
 */

public abstract class BaseWebViewFragment extends BaseFragment {

    public static Bundle getArgumentsBundle(String webUrl, String webTitle, boolean needTitleBar, boolean needCloseImage) {
        Bundle extras = new Bundle();
        extras.putString("webUrl", webUrl);
        extras.putString("webTitle", webTitle);
        extras.putBoolean("needTitleBar", needTitleBar);
        extras.putBoolean("needCloseImage", needCloseImage);
        return extras;
    }

    /**
     * 平台可以识别的 外部url 的正则
     */
    public static java.lang.String urlRegularExpression = "(((http|ftp|https)://)|www)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";
    protected TextView tv_title;
    protected LinearLayout ll_bottom;
    protected boolean isWebPageFinished = false;
    protected ProgressBar pBar;
//    protected NativeMethod nativeMethod;

    /**
     * fragment的根view
     */
    private View rootView;
    private WebView webView;
    /**
     * webView的容器
     */
    private FrameLayout mContainer;
    private Activity mActivity;
    /**
     * 网页url
     */
    private String webUrl;
    /**
     * UI的标题，可为空，为空的时候显示网页的标题
     */
    private String webTitle;
    /**
     * 是否需要显示titileBar，即baseLayout的头部
     */
    private boolean needTitleBar = true;
    /**
     * 是否需要显示直接关闭网页的按钮
     */
    private boolean needCloseImage;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_base_webview, null);
        mActivity = mBaseContext;
        Bundle extras = getArguments();
        if (extras != null) {
            webUrl = extras.getString("webUrl");
            webTitle = extras.getString("webTitle");
            needTitleBar = extras.getBoolean("needTitleBar", true);
            needCloseImage = extras.getBoolean("needCloseImage");
        }
        initLayout();
        afterInitView();
        return rootView;
    }

    /**
     * 初始化view元素
     */
    private void initLayout() {
        tv_title = baseLayout.top_tab_center_title;
        pBar = (ProgressBar) rootView.findViewById(R.id.pb_web);
        ll_bottom = (LinearLayout) rootView.findViewById(R.id.ll_bottom);
        mContainer = (FrameLayout) rootView.findViewById(R.id.web_container);
        webView = createWebView();
        if (webView == null) {
            throw new IllegalStateException("webview can't be null");
        }
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(layoutParams);
        mContainer.addView(webView);
        if (needTitleBar) {
            //需要直接关闭网页的按钮
            if (needCloseImage) {
                baseLayout.setLeftImageListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.finish();
                    }
                });
            }
            baseLayout.setBackClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //网页可返回上一个界面
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        mActivity.finish();
                    }
                }
            });
            tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isWebPageFinished && webView != null) {
                        webView.loadUrl("javascript:scrollTo(0,0);");
                    }
                }
            });
        } else {
            //不需要titleBar
            baseLayout.setTitleBarAndStatusBar(false, true);
        }
        //配置webView
        initWebviewSetting(webView);
        if (!fillWebviewWithHtmlData(webView)) {
            Intent intent = getIntentFromUrl(mActivity, getUrlNotNull());
            if (intent != null) {
                startActivity(intent);
                mActivity.finish();
            } else {
                webView.loadUrl(getUrlNotNull());
            }
        }
    }

    /**
     * webView的配置
     *
     * @param webview
     */
    protected void initWebviewSetting(final WebView webview) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        String userAgentString = webview.getSettings().getUserAgentString() + ";DF/" + CommonUtils.getVersionName();;
        webview.getSettings().setUserAgentString(userAgentString);
        webview.getSettings().setDomStorageEnabled(true);

        webview.getSettings().setAppCacheEnabled(false);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        nativeMethod = new NativeMethod(mActivity, webview);
//        webview.addJavascriptInterface(nativeMethod, "df");
        // android 5.0以上默认不支持Mixed Content 使其支持https网页
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(webTitle)) {
                    tv_title.setText(title);
                } else {
                    tv_title.setText(webTitle);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    pBar.setVisibility(View.GONE);
                } else {
                    if (pBar.getVisibility() == View.GONE) {
                        pBar.setVisibility(View.VISIBLE);
                    }
                    pBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

        });

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书, 使其支持https网页
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = getIntentFromUrl(mActivity, url);

                if (intent != null) {
                    startActivity(intent);
                    return true;
                }

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                onPageStartedInBase(webview, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isWebPageFinished = true;

//                view.loadUrl("javascript:window.pengsiid=" + 111);
//				view.loadUrl("javascript:window.demo.clickDemo1(" + "document.getElementsByTagName('title')[0].innerText" + ");");
//				view.loadUrl("javascript:window.demo.clickDemo2(" + "document.getElementsByName('description')[0].attributes['content'].value" + ");");
//				view.loadUrl("javascript:window.demo.clickDemo3(" + "document.getElementsByTagName('img')[0].attributes['src'].value" + ");");
//				view.loadUrl("javascript:window.demo.clickDemo4(" + "title" + ");");
//				view.loadUrl("javascript:window.demo.clickDemo5(" + "description" + ");");
//				view.loadUrl("javascript:window.demo.clickDemo6(" + "imgPath" + ");");
//				view.loadUrl("javascript:window.demo.clickDemo7(" + "isHideLink" + ");");
//				view.loadUrl("javascript:window.demo.clickDemo8(" + "pengsi_url" + ");");

                onPageFinishedInBase(view, url);
            }
        });
    }

    /**
     * 获取webView示例，可复写,根据需要使用继承于WebView的子类
     *
     * @return
     */
    protected WebView createWebView() {
        return new WebView(mBaseContext);
    }


    /**
     * view初始化完成后调用的方法
     */
    protected abstract void afterInitView();

    /**
     * 刷新url
     *
     * @param url
     */
    public void reloadUrl(String url) {
        webUrl = url;
        if (webView != null) {
            webView.loadUrl(url);
        }
    }


    private String getUrlNotNull() {
        return handleUrl(webUrl);
    }

    /**
     * @return 兼容www.baidu.com->http://www.baidu.com
     */
    private String handleUrl(String rawUrl) {
        String url = rawUrl == null ? "" : rawUrl;
        if (url.indexOf("www.") == 0 || url.indexOf("wap.") == 0) {//兼容www.baidu.com
            url = "http://" + url;
        }
        return url;
    }

    protected Intent getIntentFromUrl(Context mBaseContext, String url) {
        return null;
    }

    public boolean fillWebviewWithHtmlData(WebView webview) {
        return false;
    }


    public void onPageStartedInBase(WebView view, String url, Bitmap favicon) {

    }

    public void onPageFinishedInBase(WebView view, String url) {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == baseLayout.ll_tab_left_image) {
            mActivity.finish();
        }
    }

    @Override
    public void onResume() {
        if (webView != null) {
            webView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
            if (mContainer != null) {
                mContainer.removeView(webView);
            }
        }
        webView = null;
        super.onDestroy();
    }

    public boolean webviewCanGoBack() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void initDataDelay() {

    }
}
