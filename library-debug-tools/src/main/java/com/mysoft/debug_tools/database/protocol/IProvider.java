package com.mysoft.debug_tools.database.protocol;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.File;
import java.util.List;

/**
 * Created by linjiang on 30/05/2018.
 * <p>
 * If the database is not in the default path, you can provide the file by implementing the interface
 */

public interface IProvider {
    File getDatabaseFile();

    List<File> getDatabaseFiles();

    SQLiteDatabase openDatabase(File databaseFile) throws SQLiteException;
}
