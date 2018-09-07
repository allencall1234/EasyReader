package com.easyreader.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;


import com.easyreader.dialog.LoadingDialog;
import com.easyreader.utils.CommonUtils;
import com.easyreader.utils.SystemBarHelper;
import com.easyreader.utils.ToastUtils;


public abstract class BaseActivity extends FragmentActivity implements OnClickListener, IView {
    public BaseLayout baseLayout;
    public BaseActivity mBaseContext;
    private BasePresenter[] mAllPresenters = new BasePresenter[]{};
    protected Bundle mSavedInstanceState;
    private boolean registerEventBus;

    /**
     * 当使用mvp模式时实现这个方法
     */
    protected BasePresenter[] initPresenters() {
        return null;
    }

    private void addPresenters() {
        BasePresenter[] presenters = initPresenters();
        if (presenters != null) {
            mAllPresenters = presenters;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mSavedInstanceState = savedInstanceState;
            mBaseContext = this;
            BaseApplication.getInstance().addActivity(this);

            //强制竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            if (isTranslateStatusBar()) {
                setTranslucentStatus(this);
            }


            initView(mSavedInstanceState);
            addPresenters();
            setListener();
            setData();

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShortToast("页面初始化失败，请重试");
            finish();
        }

    }

    /**
     * 点击错误页面的刷新按钮时会调用这个方法
     */
    public void onReload() {

    }


    /**
     * @return true=使用状态栏一体化，false=不使用
     */
    private boolean isTranslateStatusBar() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && translateStatusBar()) {
            return true;
        }
        return false;
    }

    /**
     * 子类重写此方法来控制是否状态栏一体化
     *
     * @return
     */
    protected boolean translateStatusBar() {
        return true;
    }



    public static void setTranslucentStatus(Activity activity) {
        Window window = activity.getWindow();
        // 默认主色调为白色, 如果是6.0或者以上, 设置状态栏文字为黑色, 否则给状态栏着色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        //设置状态栏字体颜色
        SystemBarHelper.setStatusBarDarkMode(window);
    }


    /**
     * 重写了activity的setContentView
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(int layoutResID) {
        View view = View.inflate(this, layoutResID, null);
        this.setContentView(view);
    }

    /**
     * 重写了activity的setContentView
     *
     * @param view
     */
    @Override
    public void setContentView(View view) {
        baseLayout = new BaseLayout(this);
        baseLayout.addContentView(view);
        baseLayout.setBackClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.finish();
            }
        });


        super.setContentView(baseLayout);

        //baseactivity中引入butterknife，子类中直接使用就可以了
//        ButterKnife.bind(this);

        baseLayout.setStatusBarVisiable(isTranslateStatusBar());
    }

    /**
     * onCreate第一步
     *
     * @param savedInstanceState
     */
    public abstract void initView(Bundle savedInstanceState);

    /**
     * onCreate 第二步
     * 使用了butterknife之后这个setListener就可以省了
     */
    public void setListener() {

    }

    /**
     * onCreate第三步
     */
    public abstract void setData();


    @Override
    protected void onResume() {
        try {
            super.onResume();
            //正式环境才做统计
            BasePresenter.notifyIPresenter(BasePresenter.LifeStyle.onResume, mAllPresenters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        BasePresenter.notifyIPresenter(BasePresenter.LifeStyle.onStart, mAllPresenters);
        super.onStart();
    }

    @Override
    public void showLoading() {
        LoadingDialog.showIfNotExist(mBaseContext, false);
    }

    @Override
    public void hideLoding() {
        LoadingDialog.dismissIfExist();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BasePresenter.notifyIPresenter(BasePresenter.LifeStyle.onPause, mAllPresenters);
    }



    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());

        //设置字体大小不随系统字体大小而改变
        return res;
    }


    /**
     * 注册EventBus
     */
    protected void registerEventBus() {
        registerEventBus = true;
    }


    @Override
    protected void onStop() {
        BasePresenter.notifyIPresenter(BasePresenter.LifeStyle.onStop, mAllPresenters);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        BaseApplication.getInstance().removeActivity(this);
        BasePresenter.notifyIPresenter(BasePresenter.LifeStyle.onDestroy, mAllPresenters);
        //关闭软键盘
        CommonUtils.closeSoftKeyBoard(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
    }

    /**
     * {@link IView}
     */
    @Override
    public Activity getAct() {
        return mBaseContext;
    }


}
