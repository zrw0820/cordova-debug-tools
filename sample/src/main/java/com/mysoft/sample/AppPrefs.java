package com.mysoft.sample;

import com.cocosw.favor.AllFavor;
import com.cocosw.favor.Default;

/**
 * App相关数据
 * <p>
 * Created by Zourw on 2018/8/22.
 */
@AllFavor
public interface AppPrefs {
    // 开发模式输入的调试URL
    void setDebugUrl(String url);

    @Default("")
    String getDebugUrl();

    // 解压的web版本
    void setLocalWebVersion(String version);

    String getLocalWebVersion();

    // X5是否已经安装
    void setX5Install(boolean install);

    boolean isX5Install();

    // 是否使用X5引擎
    void setUseX5Engine(boolean use);

    @Default("true")
    boolean isUseX5Engine();
}