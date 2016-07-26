package com.pluginloader;

import android.app.Application;

import com.letv.plugin.pluginloader.util.JarUtil;

/**
 * Created by chenlifeng on 16/5/15.
 */
public class TestAppliction extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        JarUtil.initPlugin(this);
    }
}
