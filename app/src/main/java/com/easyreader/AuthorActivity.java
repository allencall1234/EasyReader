package com.easyreader;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyreader.base.BaseActivity;
import com.easyreader.base.CustomWebViewActivity;
import com.easyreader.bean.AuthorPortrait;
import com.easyreader.core.ApiImpl;
import com.easyreader.core.ApiUrl;
import com.easyreader.core.RxAsyncTask;
import com.easyreader.dialog.LoadingDialog;

import java.util.List;

/**
 * Created by 524202 on 2018/9/14.
 */

public class AuthorActivity extends BaseActivity {

    TextView contentView;
    RecyclerView mRecycleView;
    List<AuthorPortrait.BookInfo> bookInfos;
    AuthorAdapter mAdapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_author_layout);
        contentView = findViewById(R.id.author_protrait);
    }

    private void initRecycleView() {
        mRecycleView = findViewById(R.id.author_list);
        mRecycleView.setAdapter(mAdapter = new AuthorAdapter());
        mRecycleView.setLayoutManager(new LinearLayoutManager(mBaseContext));
    }

    @Override
    public void setData() {

        final String url = getIntent().getStringExtra("url");
        new RxAsyncTask<String, Void, AuthorPortrait>() {

            @Override
            protected AuthorPortrait call(String... strings) {
                return ApiImpl.getAuthorPortrait(url);
            }

            @Override
            protected void onResult(AuthorPortrait authorPortrait) {
                super.onResult(authorPortrait);

                if (bookInfos == null) {
                    bookInfos = authorPortrait.bookList;
                } else {
                    bookInfos.clear();
                    bookInfos.addAll(authorPortrait.bookList);
                }

                if (mAdapter == null) {
                    initRecycleView();
                } else {
                    mAdapter.notifyDataSetChanged();
                }

                baseLayout.setTitle(authorPortrait.title);
                contentView.setText(authorPortrait.content);
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


    public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorHolder> {

        @Override
        public AuthorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mBaseContext, android.R.layout.simple_list_item_1, null);
            return new AuthorHolder(view);
        }

        @Override
        public void onBindViewHolder(AuthorHolder holder, final int position) {

            String bookName = bookInfos.get(position).bookName;
            if (!bookName.matches("《(.*?)》")) {
                bookName = "《" + bookName + "》";
            }
            final String title = bookName.replaceAll("《|》","");
            holder.view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

            holder.view.setText(bookName);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String bookUrl = bookInfos.get(position).bookUrl;
                    if (!bookUrl.startsWith("http:")) {
                        bookUrl = ApiUrl.BASE_URL + bookUrl;
                    }
//                    CustomWebViewActivity.startIt(mBaseContext, bookUrl, "");
                    ChapterListActivity.startIt(mBaseContext, bookUrl, title);
                }
            });
        }

        @Override
        public int getItemCount() {
            return bookInfos.size();
        }

        public class AuthorHolder extends RecyclerView.ViewHolder {

            TextView view;

            public AuthorHolder(View itemView) {
                super(itemView);
                view = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
