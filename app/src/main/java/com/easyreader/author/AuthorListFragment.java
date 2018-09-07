package com.easyreader.author;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyreader.R;
import com.easyreader.base.BaseFragment;
import com.easyreader.bean.AuthorInfo;
import com.easyreader.core.ApiImpl;
import com.easyreader.core.RxAsyncTask;
import com.easyreader.utils.PinyinComparator;
import com.xp.sortrecyclerview.ClearEditText;
import com.xp.sortrecyclerview.PinyinUtils;
import com.xp.sortrecyclerview.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 524202 on 2018/9/7.
 */

public class AuthorListFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    LinearLayoutManager manager;
    private View view;
    private List<AuthorInfo> authorInfos;

    /**
     * 根据拼音来排列RecyclerView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_authorlist_layout, null);
        baseLayout.setTitleBarAndStatusBar(false, true);
        return view;
    }

    @Override
    protected void setListener() {

    }

    @Override
    public void initDataDelay() {
        String name = getArguments().getString("name");
        final String url = getArguments().getString("url");

        new RxAsyncTask<String, Void, List<AuthorInfo>>() {

            @Override
            protected List<AuthorInfo> call(String... strings) {
                return ApiImpl.getAuthorInfos(url);
            }

            @Override
            protected void onResult(List<AuthorInfo> lists) {
                super.onResult(lists);
                if (authorInfos == null){
                    authorInfos = new ArrayList<>();
                }else {
                    authorInfos.clear();
                }

                authorInfos.addAll(lists);
                initViews();
            }
        }.execute();
    }

    public static AuthorListFragment newInstance(String name, String url) {

        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("url", url);
        AuthorListFragment fragment = new AuthorListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void initViews() {
        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) view.findViewById(R.id.sideBar);
        dialog = (TextView) view.findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        //设置右侧SideBar触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }

            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        parseAuthorLists();

        // 根据a-z进行排序源数据
        Collections.sort(authorInfos, pinyinComparator);
        //RecyclerView社置manager
        manager = new LinearLayoutManager(mBaseContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        adapter = new SortAdapter(mBaseContext, authorInfos);
        mRecyclerView.setAdapter(adapter);
        //item点击事件
        /*adapter.setOnItemClickListener(new SortAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, ((SortModel)adapter.getItem(position)).getName(),Toast.LENGTH_SHORT).show();
            }
        });*/
        mClearEditText = (ClearEditText) view.findViewById(R.id.filter_edit);

        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void parseAuthorLists() {
        for (AuthorInfo authorInfo : authorInfos) {
            //汉字转换成拼音
            String pinyin = PinyinUtils.getPingYin(authorInfo.authorName);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                authorInfo.firstLetter = sortString.toUpperCase();
            } else {
                authorInfo.firstLetter = "#";
            }
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新RecyclerView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<AuthorInfo> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = authorInfos;
        } else {
            filterDateList.clear();
            for (AuthorInfo authorInfo : authorInfos) {
                String name = authorInfo.authorName;
                if (name.indexOf(filterStr.toString()) != -1 ||
                        PinyinUtils.getFirstSpell(name).startsWith(filterStr.toString())
                        //不区分大小写
                        || PinyinUtils.getFirstSpell(name).toLowerCase().startsWith(filterStr.toString())
                        || PinyinUtils.getFirstSpell(name).toUpperCase().startsWith(filterStr.toString())
                        ) {
                    filterDateList.add(authorInfo);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateList(filterDateList);
    }
}
