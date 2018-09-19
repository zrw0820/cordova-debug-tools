package com.mysoft.sample;

import android.content.Context;
import android.content.SharedPreferences;

import com.cocosw.favor.FavorAdapter;

/**
 * @author Zouruw
 * createDate 2018/8/10
 * description SharePreference工具类，Favor库实现
 */
public class PrefsHelper {
    public static SharedPreferences getSharePrefs(String name) {
        return App.app().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static FavorAdapter adapter(String name) {
        return new FavorAdapter
                .Builder(getSharePrefs(name))
                .build();
    }

    /**
     * @param service 接口类，使用@AllFavor注解，提供get和set方法
     */
    public static <T> T getPrefs(Class<T> service) {
        return adapter(service.getSimpleName() + "_prefs").create(service);
    }
}