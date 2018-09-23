package com.easyreader.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 524202 on 2018/9/23.
 */

@DatabaseTable(tableName = "tb_book")
public class Book {
    public final static String ID_FIELD_NAME = "book_id";

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;

    @DatabaseField
    private String bookName;

    @DatabaseField
    private String bookUrl;

    @DatabaseField
    private int writer_Id;

    public Book(){

    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setWriterId(int writerId) {
        this.writer_Id = writerId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }
}
