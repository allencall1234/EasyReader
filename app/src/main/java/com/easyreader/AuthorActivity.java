package com.easyreader;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyreader.base.BaseActivity;
import com.easyreader.base.CustomWebViewActivity;
import com.easyreader.bean.AuthorPortrait;
import com.easyreader.core.ApiImpl;
import com.easyreader.core.ApiUrl;
import com.easyreader.core.RxAsyncTask;
import com.easyreader.database.bean.Book;
import com.easyreader.database.bean.Writer;
import com.easyreader.database.dao.BookDao;
import com.easyreader.database.dao.CommonDao;
import com.easyreader.database.dao.WriterDao;
import com.easyreader.dialog.LoadingDialog;
import com.easyreader.utils.CommonUtils;

import org.jsoup.helper.StringUtil;

import java.util.List;

/**
 * Created by 524202 on 2018/9/14.
 */

public class AuthorActivity extends BaseActivity {

    TextView contentView;
    RecyclerView mRecycleView;
    AuthorAdapter mAdapter;

    WriterDao writerDao;
    BookDao bookDao;
    private List<Book> bookList;
    private Writer writer;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_author_layout);
        contentView = findViewById(R.id.author_protrait);
        writerDao = new WriterDao();
        bookDao = new BookDao();
    }

    private void initRecycleView() {
        mRecycleView = findViewById(R.id.author_list);
        mRecycleView.setAdapter(mAdapter = new AuthorAdapter());
        mRecycleView.setLayoutManager(new LinearLayoutManager(mBaseContext));
    }

    @Override
    public void setData() {

        final String url = getIntent().getStringExtra("url");

        writer = writerDao.query(url);
        bookList = bookDao.queryAll(writer.getId());
        if (TextUtils.isEmpty(writer.getResumeMe())
                && CommonUtils.isNullOrEmpty(bookList)) {
            queryFromNetwork(url);
        } else {
            if (mAdapter == null) {
                initRecycleView();
            } else {
                mAdapter.notifyDataSetChanged();
            }

            baseLayout.setTitle(writer.getWriterName());
            contentView.setText(writer.getResumeMe());
        }


    }

    private void queryFromNetwork(String url) {
        if (url != null && !url.startsWith("http")) {
            url = ApiUrl.BASE_URL + url;
        }

        final String queryUrl = url;
        new RxAsyncTask<String, Void, AuthorPortrait>() {

            @Override
            protected AuthorPortrait call(String... strings) {
                return ApiImpl.getAuthorPortrait(queryUrl);
            }

            @Override
            protected void onResult(AuthorPortrait authorPortrait) {
                super.onResult(authorPortrait);
                if (authorPortrait.bookList == null) {
                    finish();
                    return;
                }

                bookList = authorPortrait.bookList;
                for (Book b : bookList) {
                    b.setWriterId(writer.getId());
                }

                if (mAdapter == null) {
                    initRecycleView();
                } else {
                    mAdapter.notifyDataSetChanged();
                }

                baseLayout.setTitle(writer.getWriterName());
                contentView.setText(authorPortrait.content);

                //存储
                bookDao.add(bookList);
                writer.resumeMe = authorPortrait.content;
                writerDao.update(writer);
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

            String bookName = bookList.get(position).getBookName();
            if (!bookName.matches("《(.*?)》")) {
                bookName = "《" + bookName + "》";
            }
            final String title = bookName.replaceAll("《|》", "");
            holder.view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

            holder.view.setText(bookName);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String bookUrl = bookList.get(position).getBookUrl();
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
            return bookList.size();
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
