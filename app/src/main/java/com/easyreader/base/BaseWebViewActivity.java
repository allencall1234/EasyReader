package com.easyreader.base;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.easyreader.R;


/**
 * 用的是系统webview
 */
public abstract class BaseWebViewActivity extends BaseActivity {

    private BaseWebViewFragment webViewFragment;

    @Override
    public void initView(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_base_webview);
        baseLayout.setTitleBarAndStatusBar(false, true);
        initLayout();
        afterInitView();
    }

    @Override
    public void setListener() {
    }

    @Override
    public void setData() {
    }

    public String getActivityTitle() {
        return "";
    }

    public abstract void afterInitView();

    public abstract String getWebviewUrl();

    /**
     * 是否需要关闭网页的按钮
     */
    protected boolean needCloseImage() {
        return false;
    }


    private void initLayout() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        webViewFragment = createWebViewFragment();
        if (webViewFragment == null) {
            throw new IllegalArgumentException("webViewFragment cann't be null");
        }
        transaction.replace(R.id.container, webViewFragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * webView所在的fragment
     *
     * @return
     */
    public abstract BaseWebViewFragment createWebViewFragment();


    /**
     * 上一个activity是谁
     *
     * @return
     */
    public static Activity getLastActivity() {
        int size = BaseApplication.getInstance().undestroyActivities.size();
        if (size > 1) {
            return BaseApplication.getInstance().undestroyActivities.get(size - 1);
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        if (webViewFragment == null || !webViewFragment.webviewCanGoBack()) {
            super.onBackPressed();
        }
    }
}
