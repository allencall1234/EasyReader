package com.easyreader.database.dao;


import com.easyreader.database.DbHelper;
import com.easyreader.database.bean.Writer;
import com.easyreader.utils.LogUtil;
import com.j256.ormlite.dao.Dao;

import java.util.List;

/**
 * Created by 524202 on 2018/9/21.
 */

public class WriterDao {

    private Dao<Writer, Integer> writerDao;
    private DbHelper dbHelper;

    public WriterDao() {
        try {
            dbHelper = DbHelper.getDbHelper();
            writerDao = dbHelper.getDao(Writer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Dao<Writer, Integer> getWriterDao() {
        return writerDao;
    }

    public void add(Writer writer) {
        try {
            writerDao.create(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Writer writer) {
        try {
            writerDao.delete(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Writer writer) {
        try {
            writerDao.update(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Writer query(Integer id) {
        try {
            return writerDao.queryForId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Writer query(String url){
        try {
            List<Writer> results = writerDao.queryForEq("writerUrl", url);
            if (results.size() > 0){
                return results.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
