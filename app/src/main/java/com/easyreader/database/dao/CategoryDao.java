package com.easyreader.database.dao;


import com.easyreader.database.DbHelper;
import com.easyreader.database.bean.Category;
import com.easyreader.database.bean.Writer;
import com.j256.ormlite.dao.Dao;

import java.util.List;

/**
 * Created by 524202 on 2018/9/21.
 */

public class CategoryDao {

    private Dao<Category, Integer> categoryDao;
    private DbHelper dbHelper;

    public CategoryDao() {
        try {
            dbHelper = DbHelper.getDbHelper();
            categoryDao = dbHelper.getDao(Category.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(Category category) {
        try {
            categoryDao.createIfNotExists(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(List<Category> categories) {
        try {
            categoryDao.create(categories);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Category category) {
        try {
            categoryDao.delete(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Category category) {
        try {
            categoryDao.update(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Category query(Integer id) {
        try {
            return categoryDao.queryForId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Category queryByName(String name) {
        try {
            List<Category> result = categoryDao.queryForEq("categoryName", name);
            if (result.size() > 0) {
                return result.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Category> queryAll() {
        try {
            return categoryDao.queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
