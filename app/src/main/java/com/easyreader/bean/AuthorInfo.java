package com.easyreader.bean;

/**
 * Created by 524202 on 2018/9/7.
 */

public class AuthorInfo {

    public String authorName;
    public String authorUrl;
    public String firstLetter;

    public AuthorInfo(String authorUrl, String authorName) {
        this.authorName = authorName;
        this.authorUrl = authorUrl;
    }
}
