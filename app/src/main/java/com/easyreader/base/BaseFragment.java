package com.easyreader.base;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyreader.dialog.LoadingDialog;
import com.easyreader.utils.CommonUtils;


/**
 * 若把初始化内容放到{@link BaseFragment#initDataDelay()}实现,就是采用Lazy方式加载的Fragment
 * 若不需要Lazy加载则initDataDelay方法内留空,初始化内容放到initView即可
 * -
 * -注1: 如果是与ViewPager一起使用，调用的是setUserVisibleHint。
 * ------可以调用mViewPager.setOffscreenPageLimit(size),若设置了该属性 则viewpager会缓存指定数量的Fragment
 * -注2: 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * -注3: 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener, IView {
    public Activity mBaseContext;
    private View myXmlView;
    public BaseLayout baseLayout;
    private BasePresenter[] mAllPresenters = new BasePresenter[]{};
    private boolean isVisible;                  //是否可见状态
    private boolean isViewPrepared;             //标志位，View已经初始化完成。
    private boolean isDataInit = true;         //是否第一次加载
    private boolean registerEventBus;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseContext = this.getActivity();
        baseLayout = new BaseLayout(mBaseContext);
        myXmlView = initView(inflater, container, savedInstanceState);
        baseLayout.addContentView(myXmlView);
        baseLayout.setStatusBarVisiable(isTranslateStatusBar());

        addPresenters();
        setListener();

        isDataInit = true;
        isViewPrepared = true;
        lazyLoad();
        if (registerEventBus) {
            //注册
//            EventBusUtils.register(this);
        }
        return baseLayout;
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected void onInvisible() {
    }

    protected void lazyLoad() {
        if (!isViewPrepared || !isVisible || !isDataInit) {
            return;
        }
        isDataInit = false;
        initDataDelay();
    }

    public void setDataInit(boolean dataInit) {
        isDataInit = dataInit;
    }

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void setListener();

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
        return false;
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * fragment可见的时候，获取数据填充fragment
     */
    public abstract void initDataDelay();

    /**
     * 注册EventBus
     */
    protected void registerEventBus() {
        registerEventBus = true;
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
    public Activity getAct() {
        return mBaseContext;
    }

    @Override
    public void onDestroy() {
        BasePresenter.notifyIPresenter(BasePresenter.LifeStyle.onDestroy, mAllPresenters);
        super.onDestroy();
    }


}
