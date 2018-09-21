package com.easyreader.database.dao;

import com.easyreader.database.DbHelper;
import com.easyreader.database.bean.Writer;
import com.easyreader.database.bean.WriterCategory;
import com.j256.ormlite.dao.Dao;

import java.util.List;

/**
 * Created by 524202 on 2018/9/21.
 */

public class WriterCategoryDao {
    private Dao<WriterCategory, Integer> dao;
    private DbHelper dbHelper;

    public WriterCategoryDao() {
        try {
            dbHelper = DbHelper.getDbHelper();
            dao = dbHelper.getDao(WriterCategory.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(WriterCategory writerCategory){
        try {
            dao.createIfNotExists(writerCategory);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<WriterCategory> queryById(int id){
        try {
            return dao.queryForEq("category_id",id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
