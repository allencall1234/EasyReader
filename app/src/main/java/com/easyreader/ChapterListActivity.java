package com.easyreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easyreader.base.BaseActivity;
import com.easyreader.bean.Chapter;
import com.easyreader.core.ApiImpl;
import com.easyreader.core.RxAsyncTask;
import com.easyreader.dialog.LoadingDialog;
import com.easyreader.utils.ToastUtils;

import java.util.List;

/**
 * Created by 524202 on 2018/9/14.
 */

public class ChapterListActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView mRecycleView;
    List<Chapter> chapterList;
    ChapterAdapter mAdapter;
    DrawerLayout drawer;
    ScrollView scrollView;
    TextView tvNext;
    TextView tvPre;
    TextView tvContent;
    TextView tvTitle;
    String base_url = "";
    String currentChapter = "";
    int selectIndex = 0;
    int tempSelectIndex = 0;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chapter_main);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        final String title = getIntent().getStringExtra("title");
        baseLayout.setTitle(title);

        tvContent = findViewById(R.id.tv_content);
        tvTitle = findViewById(R.id.tv_title);
        scrollView = findViewById(R.id.scroll_view);
        tvNext = findViewById(R.id.tv_next);
        tvPre = findViewById(R.id.tv_pre);


        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tempSelectIndex < chapterList.size() - 1) {
                    tempSelectIndex++;
                    currentChapter = chapterList.get(tempSelectIndex).chapterName;
                    getBookContent(chapterList.get(tempSelectIndex).url);
                } else {
                    ToastUtils.showShortToast("当前已经是最后一页");
                }
            }
        });

        tvPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tempSelectIndex > 0) {
                    tempSelectIndex--;
                    currentChapter = chapterList.get(tempSelectIndex).chapterName;
                    getBookContent(chapterList.get(tempSelectIndex).url);
                } else {
                    ToastUtils.showShortToast("当前已经是第一页");
                }
            }
        });


    }

    private void initRecycleView() {
        mRecycleView = findViewById(R.id.rv_chapter);
        mRecycleView.setAdapter(mAdapter = new ChapterAdapter());
        mRecycleView.setLayoutManager(new LinearLayoutManager(mBaseContext));
    }

    public static void startIt(Activity context, String url, String title) {
        Intent intent = new Intent(context, ChapterListActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    public void setData() {

        base_url = getIntent().getStringExtra("url");
//        int index = url.lastIndexOf("/");
//        base_url = url.substring(0, index + 1);
        new RxAsyncTask<String, Void, List<Chapter>>() {

            @Override
            protected List<Chapter> call(String... strings) {
                return ApiImpl.getCharpter(base_url);
            }

            @Override
            protected void onResult(List<Chapter> results) {
                super.onResult(results);

                if (chapterList == null) {
                    chapterList = results;
                } else {
                    chapterList.clear();
                    chapterList.addAll(results);
                }

                if (mAdapter == null) {
                    initRecycleView();
                } else {
                    mAdapter.notifyDataSetChanged();
                }

                currentChapter = chapterList.get(0).chapterName;
                initBaseUrl(chapterList.get(0).url);
                getBookContent(chapterList.get(0).url);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LoadingDialog.showIfNotExist(mBaseContext, false);
            }

            @Override
            protected void onCompleted() {
                super.onCompleted();
                LoadingDialog.dismissIfExist();
            }
        }.execute();
    }

    private void initBaseUrl(String url) {
        String[] baseUrls = null;
        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        if (base_url.endsWith(".html")) {
            base_url = base_url.substring(0, base_url.length() - 5);
        }

        if (base_url.endsWith("index")){
            base_url = base_url.substring(0, base_url.length() - 5);
        }

        baseUrls = base_url.split("/");

        int index = 2;
        for (int i = index; i < baseUrls.length; i++) {
            if (url.startsWith(baseUrls[i])) {
                break;
            } else {
                index++;
            }
        }
        base_url = "";

        for (int i = 0; i < index; i++) {
            base_url += baseUrls[i] + "/";
        }

    }

    public void getBookContent(String url) {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        final String resultUrl = base_url + url;
        new RxAsyncTask<String, Void, String>() {

            @Override
            protected String call(String... strings) {
                return ApiImpl.getCharpterContent(resultUrl);
            }

            @Override
            protected void onCompleted() {
                super.onCompleted();
                hideLoding();
            }

            @Override
            protected void onResult(String s) {
                super.onResult(s);
                tvContent.setText(Html.fromHtml(s));
                tvTitle.setText(currentChapter);
                scrollView.scrollTo(0, 0);
                selectIndex = tempSelectIndex;
                mAdapter.notifyDataSetChanged();
                hideLoding();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading();
            }
        }.execute();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


    public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterHolder> {

        @Override
        public ChapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mBaseContext, android.R.layout.simple_list_item_1, null);
            return new ChapterHolder(view);
        }

        @Override
        public void onBindViewHolder(ChapterHolder holder, final int position) {

            final String chapterName = chapterList.get(position).chapterName;

            holder.view.setText(chapterName);
            if (position == selectIndex) {
                holder.view.setTextColor(getResources().getColor(R.color.title_color));
            } else {
                holder.view.setTextColor(getResources().getColor(R.color.color_4a4a4a));
            }
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String bookUrl = chapterList.get(position).url;
                    currentChapter = chapterList.get(position).chapterName;
                    getBookContent(bookUrl);
                    tempSelectIndex = position;
                    drawer.closeDrawers();
                }
            });
        }

        @Override
        public int getItemCount() {
            return chapterList.size();
        }

        public class ChapterHolder extends RecyclerView.ViewHolder {

            TextView view;

            public ChapterHolder(View itemView) {
                super(itemView);
                view = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
