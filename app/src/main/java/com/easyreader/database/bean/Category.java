package com.easyreader.database.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by 524202 on 2018/9/21.
 */

@DatabaseTable(tableName = "tb_category")
public class Category implements Serializable {

    public final static String ID_FIELD_NAME = "category_id";

    @DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
    private int id;

    @DatabaseField
    private String categoryName;

    @DatabaseField
    private String categoryUrl;

    public Category(){

    }

    public Category(String categoryName,String categoryUrl){
        this.categoryName = categoryName;
        this.categoryUrl = categoryUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public int getId() {
        return id;
    }
}
