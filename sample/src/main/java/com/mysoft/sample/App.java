package com.mysoft.sample;

import android.app.Application;

/**
 * Created by Zourw on 2018/9/15.
 */
public class App extends Application {
    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public static App app() {
        return app;
    }
}
