package com.easyreader.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 524202 on 2018/9/21.
 */

@DatabaseTable(tableName = "tb_writer_category")
public class WriterCategory {

    public final static String WRITER_ID_FIELD_NAME = "writer_id";
    public final static String CATEGORY_ID_FIELD_NAME = "category_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true,columnName = WRITER_ID_FIELD_NAME)
    public Writer writer;

    @DatabaseField(foreign = true,columnName = CATEGORY_ID_FIELD_NAME)
    public Category category;

    public WriterCategory() {

    }

    public WriterCategory(int id,Writer writer, Category category) {
        this.id = id;
        this.writer = writer;
        this.category = category;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
