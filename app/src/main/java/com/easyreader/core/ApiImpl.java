package com.easyreader.core;

import android.app.ProgressDialog;
import android.util.Log;

import com.easyreader.bean.AuthorInfo;
import com.easyreader.bean.AuthorType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 524202 on 2018/8/30.
 */

public class ApiImpl extends ApiUrl {

    private static String TAG = ApiImpl.class.getName();

    public static List<AuthorType> getAuthorType() {
        List<AuthorType> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect(BASE_URL).get();
            Elements element = document.select("td");

            for (Element e : element) {
                String text = e.text();
                if (text.contains("所有作家")) {
                    Elements authors = e.getElementsByTag("a");
                    for (Element author : authors) {
                        list.add(new AuthorType(author.attr("href"), author.text()));
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list.size() > 0) {
            list.remove(0);
        }
        return list;
    }

    public static List<AuthorInfo> getAuthorInfos(String url) {
        List<AuthorInfo> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect(BASE_URL + url).get();
            Elements elements = document.select(".tb a[href]");
            for (Element e : elements) {
                list.add(new AuthorInfo(e.attr("href"), e.text()));
            }
            Log.d("zlt", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;
    }
}
