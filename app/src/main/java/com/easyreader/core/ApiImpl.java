package com.easyreader.core;

import android.util.Log;

import com.easyreader.bean.AuthorInfo;
import com.easyreader.bean.AuthorPortrait;
import com.easyreader.bean.Chapter;
import com.easyreader.database.bean.Book;
import com.easyreader.database.bean.Category;
import com.easyreader.database.bean.Writer;
import com.easyreader.utils.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 524202 on 2018/8/30.
 */

public class ApiImpl extends ApiUrl {

    private static String TAG = ApiImpl.class.getName();

    public static List<Category> getCategories() {
        LogUtil.d("------------getAuthorType()--------------");
        List<Category> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect(BASE_URL).get();
            Elements element = document.select("td");
            for (Element e : element) {
                String text = e.text();
                if (text.contains("所有作家")) {
                    Elements authors = e.getElementsByTag("a");
                    for (Element author : authors) {
                        list.add(new Category(author.text(), author.attr("href")));
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

    public static List<Writer> getAuthorInfos(String url) {
        List<Writer> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect(BASE_URL + url).get();
            Elements elements = document.select(".tb a[href]");
            for (Element e : elements) {
                list.add(new Writer(e.text(), e.attr("href")));
            }
            Log.d("zlt", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static AuthorPortrait getAuthorPortrait(String url) {
        AuthorPortrait portrait = new AuthorPortrait();
        try {
            Document document = Jsoup.connect(url).get();
//            Elements titleEle = document.select("h2 b");
//            if (titleEle.size() > 0) {
//                portrait.title = titleEle.get(0).text();
//            }

            Elements booksEle = document.select("tbody tr td[valign='top']");
            document = Jsoup.parse(booksEle.get(1).toString());

            if (document.select("p").size() > 0) {
                portrait.content = document.select("p").get(0).text();
            }

            booksEle = document.select("a:not(:has(img))[href]");

            List<Book> bookInfos = new ArrayList<>();

            for (Element e : booksEle) {
                Book book = new Book();
                book.setBookName(e.text().split("\\s+")[0]);
                book.setBookUrl(e.attr("href"));
                bookInfos.add(book);
            }

            portrait.bookList = bookInfos;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return portrait;
    }

    public static List<Chapter> getCharpter(String url) {
        List<Chapter> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("tr[bgcolor]>td>a[href]");
            for (Element element : elements) {
                Chapter chapter = new Chapter(element.attr("href"), element.text());
                list.add(chapter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String getCharpterContent(String url) {
        String content = "";
        try {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.select("td[bgcolor]>p");
            if (elements.size() <= 0) {
                elements = document.select("#content");
            }
            content = elements.get(0).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}
