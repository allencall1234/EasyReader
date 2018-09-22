package com.easyreader.database.dao;

import com.easyreader.database.DbHelper;
import com.easyreader.database.bean.Category;
import com.easyreader.database.bean.Writer;
import com.easyreader.database.bean.WriterCategory;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by 524202 on 2018/9/21.
 */

public class WriterCategoryDao {
    private Dao<WriterCategory, Integer> dao;
    private DbHelper dbHelper;
    private CategoryDao categoryDao;
    private WriterDao writerDao;

    public WriterCategoryDao() {
        try {
            dbHelper = DbHelper.getDbHelper();
            dao = dbHelper.getDao(WriterCategory.class);
            categoryDao = new CategoryDao();
            writerDao = new WriterDao();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(WriterCategory writerCategory){
        try {
            dao.create(writerCategory);
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

    public List<Writer> lookupWriterForCategory(Category category) throws SQLException {

        PreparedQuery query = makeWriterForCategoryQuery();

        query.setArgumentHolderValue(0, category);
        return dao.query(query);
    }
    /**
     * 查询某个项目的所有负责人
     */
    private PreparedQuery<Writer> makeWriterForCategoryQuery() throws SQLException {
        QueryBuilder<WriterCategory, Integer> writeCategory = dao.queryBuilder();
        writeCategory.selectColumns(WriterCategory.WRITER_ID_FIELD_NAME);
        SelectArg userSelectArg = new SelectArg();
        writeCategory.where().eq(WriterCategory.CATEGORY_ID_FIELD_NAME, userSelectArg);
        QueryBuilder<Writer, Integer> postQb = writerDao.getWriterDao().queryBuilder();
        postQb.where().in(Writer.ID_FIELD_NAME, writeCategory);
        return postQb.prepare();
    }


}
