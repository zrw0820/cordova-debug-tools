package com.mysoft.debug_tools.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;

import com.mysoft.debug_tools.database.protocol.IProvider;

import java.io.File;
import java.util.Collections;
import java.util.List;


/**
 * Created by linjiang on 29/05/2018.
 */

public class DatabaseProvider implements IProvider {
    private File databaseFile;

    public DatabaseProvider(File databaseFile) {
        this.databaseFile = databaseFile;
    }

    @Override
    public File getDatabaseFile() {
        return databaseFile;
    }

    @Override
    public List<File> getDatabaseFiles() {
        return Collections.singletonList(databaseFile);
    }

    @Override
    public SQLiteDatabase openDatabase(File databaseFile) throws SQLiteException {
        return performOpen(databaseFile, checkIfCanOpenWithWAL(databaseFile));
    }

    private int checkIfCanOpenWithWAL(File databaseFile) {
        int flags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            File walFile = new File(databaseFile.getParent(), databaseFile.getName() + "-wal");
            if (walFile.exists()) {
                flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
            }
        }
        return flags;
    }

    private SQLiteDatabase performOpen(File databaseFile, int options) {
        int flags = SQLiteDatabase.OPEN_READWRITE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if ((options & SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING) != 0) {
                flags |= SQLiteDatabase.ENABLE_WRITE_AHEAD_LOGGING;
            }
        }
        return SQLiteDatabase.openDatabase(databaseFile.getAbsolutePath(), null, flags);
    }
}
