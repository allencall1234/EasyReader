package com.easyreader.base;

/**
 * Created by 513419 on 2017/12/6.
 */

public class WebViewFragment extends BaseWebViewFragment {

    public static WebViewFragment newInstance(String webUrl, String webTitle, boolean needCloseImage) {
        return newInstance(webUrl, webTitle, true, needCloseImage);
    }

    /**
     * @param webUrl         网页url
     * @param webTitle       网页title
     * @param needTitleBar   fragment是否需要titleBar
     * @param needCloseImage 是否需要直接关闭网页的按钮
     * @return
     */
    public static WebViewFragment newInstance(String webUrl, String webTitle, boolean needTitleBar, boolean needCloseImage) {
        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setArguments(getArgumentsBundle(webUrl, webTitle, needTitleBar, needCloseImage));
        return webViewFragment;
    }

    @Override
    public void afterInitView() {

    }
}
