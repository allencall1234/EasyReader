package com.easyreader.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 524202 on 2018/9/21.
 */

@DatabaseTable(tableName = "tb_writer")
public class Writer {
    public final static String ID_FIELD_NAME = "writer_id";

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;

    @DatabaseField
    private String writerName;

    @DatabaseField
    private String writerUrl;

    @DatabaseField
    public String firstLetter;

    public Writer() {

    }

    public Writer(String writerName, String writerUrl) {
        this.writerName = writerName;
        this.writerUrl = writerUrl;
    }

    public String getWriterName() {
        return writerName;
    }

    public String getWriterUrl() {
        return writerUrl;
    }

    public int getId() {
        return id;
    }

}
