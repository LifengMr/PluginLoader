package com.letv.plugin.pluginloader.loadapk.helper;

import android.app.Activity;
import android.content.Context;

import com.letv.plugin.pluginloader.loadapk.hook.ApkContextImp;
import com.letv.plugin.pluginloader.loader.JarClassLoader;

/**
 * Created by chenlifeng on 16/5/25.
 */
public class ContextBuild {

    public static Context build(Context baseContext, Activity activity) {
        ClassLoader classLoader = activity.getClass().getClassLoader();
        if (!(classLoader instanceof JarClassLoader)) {
            return baseContext;
        }
        JarClassLoader jarClassLoader = (JarClassLoader) classLoader;
        return new ApkContextImp(baseContext, jarClassLoader);
    }
}
