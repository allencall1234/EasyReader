package com.easyreader.author;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyreader.R;
import com.easyreader.base.BaseFragment;
import com.easyreader.core.ApiImpl;
import com.easyreader.core.RxAsyncTask;
import com.easyreader.database.bean.Category;
import com.easyreader.database.dao.CategoryDao;
import com.easyreader.utils.CommonUtils;
import com.easyreader.utils.ScaleTransitionPagerTitleView;
import com.easyreader.utils.ToastUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 524202 on 2018/8/30.
 */

public class AuthorFragment extends BaseFragment {

    MagicIndicator indicator = null;
    ViewPager viewPager = null;
    PagerAdapter vAdapter = null;
    CommonNavigatorAdapter indicatorAdapter;
    List<Category> mDataList;
    private CategoryDao categoryDao;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author_layout, null);
        baseLayout.setTitleBarAndStatusBar(false, true);
        indicator = view.findViewById(R.id.magic_indicator);
        viewPager = view.findViewById(R.id.fragments);
        showLoading();


        categoryDao = new CategoryDao();
        List<Category> categories = categoryDao.queryAll();

        if (CommonUtils.isNullOrEmpty(categories)) {
            queryCategoriesFromNetwork();
        } else {
            initMagicIndicator6(categories);
        }
        return view;
    }

    private void queryCategoriesFromNetwork() {
        new RxAsyncTask<Void, Void, List<Category>>() {

            @Override
            protected List<Category> call(Void... voids) {
                return ApiImpl.getCategories();
            }

            @Override
            protected void onResult(List<Category> categories) {
                super.onResult(categories);
                saveToDatabase(categories);
                initMagicIndicator6(categories);
            }

            @Override
            protected void onCompleted() {
                super.onCompleted();
                hideLoding();
            }

            @Override
            protected void onError(Throwable e) {
                super.onError(e);
                hideLoding();
                ToastUtils.showShortToast("查询作家列表失败");
            }
        }.execute();
    }

    private void saveToDatabase(List<Category> categories) {
        categoryDao.add(categories);
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void initDataDelay() {
    }

    private void initMagicIndicator6(List<Category> categories) {

        if (mDataList == null) {
            mDataList = new ArrayList<>();
        } else {
            mDataList.clear();
        }

        mDataList.addAll(categories);

        initViewPager();

        indicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(indicatorAdapter = new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index).getCategoryName());
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(Color.BLACK);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                BezierPagerIndicator indicator = new BezierPagerIndicator(context);
                indicator.setColors(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4"), Color.parseColor("#76b0ff"), Color.parseColor("#c683fe"));
                return indicator;
            }
        });
        indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(indicator, viewPager);
    }

    private void initViewPager() {
        if (vAdapter == null) {
            vAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    Category bean = mDataList.get(position);
                    Category saveData = categoryDao.queryByName(bean.getCategoryName());
                    Fragment fragment = AuthorListFragment.newInstance(saveData);
                    return fragment;
                }

                @Override
                public int getCount() {
                    return mDataList.size();
                }
            };
            viewPager.setAdapter(vAdapter);
        } else {
            vAdapter.notifyDataSetChanged();
        }
    }
}
