package com.mysoft.debug_tools.database.protocol;

import android.database.sqlite.SQLiteException;

import com.mysoft.debug_tools.database.DatabaseResult;

import java.util.List;


/**
 * Created by linjiang on 29/05/2018.
 * <p>
 * Database driver：SQLite、ContentProvider
 */

public interface IDriver<T extends IDescriptor> {
    T getDatabaseName();

    List<T> getDatabaseNames();

    List<String> getTableNames(T databaseDesc) throws SQLiteException;

    void executeSQL(T databaseDesc, String query, DatabaseResult result) throws SQLiteException;
}
