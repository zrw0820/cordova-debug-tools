package com.mysoft.debug_tools.crash;

import android.content.Context;

import com.mysoft.debug_tools.utils.Utils;

import java.io.File;

/**
 * Created by Zourw on 2018/9/15.
 */
public class CrashKep {
    public static final String LOG_PATH = "appcloud_log";

    public static void bind(Context context) {
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (handler == null) {
            handler = new Handler(context);
        } else {
            handler = new Wrapper(handler, new Handler(context));
        }
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    private static final class Handler implements Thread.UncaughtExceptionHandler {
        private final Context context;
        private final ReportFactory reportFactory;

        private Handler(Context context) {
            this.context = context;
            this.reportFactory = new ReportFactory(context);
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            String dataDir = context.getApplicationInfo().dataDir;
            File logDir = new File(dataDir, LOG_PATH);
            if (!logDir.exists()) {
                logDir.mkdir();
            }
            if (logDir.exists()) {
                Report report = reportFactory.createReport(e);
                File log = new File(logDir, Utils.millis2String(System.currentTimeMillis(), Utils.WITHOUT_MILLIS) + ".log");
                Utils.writeTextToFile(log, report.log());
            }
        }
    }

    private static final class Wrapper implements Thread.UncaughtExceptionHandler {
        private final Thread.UncaughtExceptionHandler base;
        private final Handler handler;

        private Wrapper(Thread.UncaughtExceptionHandler base, Handler handler) {
            this.base = base;
            this.handler = handler;
        }

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            handler.uncaughtException(t, e);
            base.uncaughtException(t, e);
        }
    }
}