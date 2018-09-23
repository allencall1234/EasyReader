package com.easyreader.database.dao;


import com.easyreader.database.DbHelper;
import com.easyreader.database.bean.Category;
import com.j256.ormlite.dao.Dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by 524202 on 2018/9/21.
 */

public class CommonDao<T> {

    private Dao<T, Integer> dao;
    private DbHelper dbHelper;

    public CommonDao() {
        try {
            Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

            dbHelper = DbHelper.getDbHelper();
            dao = dbHelper.getDao(entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(T t) {
        try {
            dao.createIfNotExists(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(List<T> t) {
        try {
            dao.create(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(T t) {
        try {
            dao.delete(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(T t) {
        try {
            dao.update(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T query(Integer id) {
        try {
            return dao.queryForId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public T queryByName(String field, String value) {
        try {
            List<T> result = dao.queryForEq(field, value);
            if (result.size() > 0) {
                return result.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<T> queryAll() {
        try {
            return dao.queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
