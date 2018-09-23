package com.easyreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.easyreader.base.BaseApplication;
import com.easyreader.database.bean.Book;
import com.easyreader.database.bean.Category;
import com.easyreader.database.bean.Writer;
import com.easyreader.database.bean.WriterCategory;
import com.easyreader.utils.LogUtil;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * 时间 :  2017/6/2;
 * 版本号 :
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    @SuppressWarnings("WeakerAccess")
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DbHelper mDbHelper;

    @SuppressWarnings("WeakerAccess")
    public static DbHelper getDbHelper() {
        if (null == mDbHelper) {
            synchronized (DbHelper.class) {
                if (null == mDbHelper) {
                    mDbHelper = new DbHelper(BaseApplication.getInstance().getApplicationContext());
                }
            }
        }
        return mDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, Writer.class);
            TableUtils.createTable(connectionSource, WriterCategory.class);
            TableUtils.createTable(connectionSource, Book.class);
            LogUtil.d("DbHelper onCreate");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        LogUtil.d("DbHelper onUpgrade");
        if (oldVersion < newVersion) {
            try {
                TableUtils.dropTable(connectionSource, Writer.class, true);
                TableUtils.dropTable(connectionSource, Category.class, true);
                TableUtils.dropTable(connectionSource, WriterCategory.class, true);
                TableUtils.dropTable(connectionSource, Book.class, true);

                TableUtils.createTable(connectionSource, Category.class);
                TableUtils.createTable(connectionSource, Writer.class);
                TableUtils.createTable(connectionSource, WriterCategory.class);
                TableUtils.createTable(connectionSource, Book.class);

                LogUtil.d("Updata database to version :" + newVersion);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
