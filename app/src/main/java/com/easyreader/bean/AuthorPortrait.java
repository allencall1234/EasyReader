package com.easyreader.bean;

import java.util.List;

/**
 * Created by 524202 on 2018/9/14.
 */

public class AuthorPortrait {
    public String title;
    public String content;

    public List<BookInfo> bookList;

    public static class BookInfo {
        public String bookName;
        public String bookUrl;
    }
}
