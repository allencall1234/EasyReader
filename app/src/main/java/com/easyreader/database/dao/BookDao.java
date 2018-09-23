package com.easyreader.database.dao;


import com.easyreader.database.DbHelper;
import com.easyreader.database.bean.Book;
import com.easyreader.database.bean.Writer;
import com.j256.ormlite.dao.Dao;

import java.util.List;

/**
 * Created by 524202 on 2018/9/21.
 */

public class BookDao {

    private Dao<Book, Integer> bookDao;
    private DbHelper dbHelper;

    public BookDao() {
        try {
            dbHelper = DbHelper.getDbHelper();
            bookDao = dbHelper.getDao(Book.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Dao<Book, Integer> getBookDao() {
        return bookDao;
    }

    public void add(Book book) {
        try {
            bookDao.create(book);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(List<Book> books) {
        try {
            bookDao.create(books);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Book book) {
        try {
            bookDao.delete(book);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Book book) {
        try {
            bookDao.update(book);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Book query(Integer id) {
        try {
            return bookDao.queryForId(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> queryAll(int writerId){
        try {
            return bookDao.queryForEq("writer_id",writerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> queryAll() {
        try {
            return bookDao.queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Book queryByName(String field, String value) {
        try {
            List<Book> result = bookDao.queryForEq(field, value);
            if (result.size() > 0) {
                return result.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
