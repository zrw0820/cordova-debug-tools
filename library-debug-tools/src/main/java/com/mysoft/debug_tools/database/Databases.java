package com.mysoft.debug_tools.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;


/**
 * Created by linjiang on 29/05/2018.
 */

public final class Databases {
    private static final String TAG = "Databases";

    private File databaseFile;
    private DatabaseDriver mDriver;

    public Databases(File databaseFile) {
        this.databaseFile = databaseFile;
        mDriver = new DatabaseDriver(new DatabaseProvider(databaseFile));
    }

    public String getDatabaseName() {
        if (mDriver == null) {
            mDriver = new DatabaseDriver(new DatabaseProvider(databaseFile));
        }
        return mDriver.getDatabaseName().name();
    }

    public List<String> getTableNames() {
        return mDriver.getTableNames(mDriver.getDatabaseName());
    }

    public DatabaseResult getTableInfo(String table) {
        String sql = String.format("pragma table_info(%s)", table);
        return executeSQL(sql);
    }

    public DatabaseResult query(String table, String condition) {
        // TODO if ROW_ID not exists
        //String sql = String.format("select " + Column.ROW_ID + " as " + Column.ROW_ID + ", * from %s", table);
        String sql = String.format("select * from %s", table);
        if (!TextUtils.isEmpty(condition)) {
            sql = sql.concat(" where ").concat(condition);
        }
        return executeSQL(sql);
    }

    public DatabaseResult update(String table,
                                 String primaryKey,
                                 String primaryValue,
                                 String key,
                                 String value) {
        String sql = String.format("update %s set %s = '%s' where %s = '%s'",
                table, key, value, primaryKey, primaryValue);
        return executeSQL(sql);
    }

    public DatabaseResult insert(String table, ContentValues values) {
        Iterator<String> sets = values.keySet().iterator();
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        while (sets.hasNext()) {
            String key = sets.next();
            if (values.getAsString(key) == null) {
                // distinguish NULL and ''
                continue;
            }
            keyBuilder.append(key);
            valueBuilder.append("'").append(values.getAsString(key)).append("'");
            keyBuilder.append(",");
            valueBuilder.append(",");
        }
        if (keyBuilder.length() > 0) {
            keyBuilder.deleteCharAt(keyBuilder.lastIndexOf(","));
            valueBuilder.deleteCharAt(valueBuilder.lastIndexOf(","));
        }
        String sql = String.format("insert into %s (%s) values (%s)",
                table, keyBuilder.toString(), valueBuilder.toString());
        return executeSQL(sql);
    }

    public DatabaseResult delete(String table,
                                 String primaryKey,
                                 String primaryValue) {
        String sql = String.format("delete from %s", table);
        if (!TextUtils.isEmpty(primaryKey) && !TextUtils.isEmpty(primaryValue)) {
            sql = sql.concat(String.format(" where %s = '%s'", primaryKey, primaryValue));
        }
        return executeSQL(sql);
    }

    /**
     * primaryKey first, and then is ROW_ID
     */
    public String getPrimaryKey(String table) {
        // 1. get the struct of table
        DatabaseResult info = getTableInfo(table);
        int columnSize = info.columnNames.size();
        int pkIndex = -1;
        int nameIndex = -1;
        // 2. find the position of name and pk
        for (int i = 0; i < columnSize; i++) {
            if (TextUtils.equals(info.columnNames.get(i), Column.PK)) {
                pkIndex = i;
            } else if (TextUtils.equals(info.columnNames.get(i), Column.NAME)) {
                nameIndex = i;
            }
            if (pkIndex >= 0 && nameIndex >= 0) {
                break;
            }
        }
        // 3. determine the primary key based on the value of pk
        String primaryKeyName = null;
        for (int i = 0; i < info.values.size(); i++) {
            String pkValue = info.values.get(i).get(pkIndex);
            if (!TextUtils.isEmpty(pkValue) && "1".equals(pkValue)) {
                primaryKeyName = info.values.get(i).get(nameIndex);
                break;
            }
        }
        // 4. if no primary key defined, use ROW_ID as default
        if (TextUtils.isEmpty(primaryKeyName)) {
            primaryKeyName = Column.ROW_ID;
        }
        return primaryKeyName;
    }

    public DatabaseResult executeSQL(String sql) {
        Timber.d("executeSQL: %s", sql);
        DatabaseResult result = new DatabaseResult();
        try {
            mDriver.executeSQL(mDriver.getDatabaseName(), sql, result);
        } catch (SQLiteException e) {
            DatabaseResult.Error error = new DatabaseResult.Error();
            error.code = 0;
            error.message = e.getMessage();
            result.sqlError = error;
        }
        return result;
    }
}
