package com.mysoft.debug_tools;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.mysoft.debug_tools.crash.CrashKep;

import timber.log.Timber;

/**
 * Created by Zourw on 2018/9/15.
 */
public class CDTFileProvider extends FileProvider {
    @Override
    public boolean onCreate() {
        if (getContext() instanceof Application) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(int priority, String tag, @NonNull String message, Throwable t) {
                    super.log(priority, "Debug-Tools", "TAG: " + tag + ", Message: " + message, t);
                }
            });
            CrashKep.bind(getContext());
        }
        return super.onCreate();
    }
}