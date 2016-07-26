package com.letv.plugin.pluginloader.loadapk.hook;

import android.text.TextUtils;

import com.letv.plugin.pluginloader.loadapk.Apker;
import com.letv.plugin.pluginloader.util.JLog;

/**
 * Created by chenlifeng on 16/5/20.
 */
public class MainClassLoader extends ClassLoader {

    public MainClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        JLog.i("clf", "MainClassLoader..loadClass className=" + className);
        if (TextUtils.isEmpty(className)) {
            return super.loadClass(className);
        }

        Class<?> clazz = super.loadClass(className);
        if (clazz == null) {
            clazz = Apker.ins().loadClass(className);
        }
        return clazz;
    }
}
