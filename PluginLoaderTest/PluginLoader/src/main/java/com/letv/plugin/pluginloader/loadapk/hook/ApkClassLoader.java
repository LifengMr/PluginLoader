package com.letv.plugin.pluginloader.loadapk.hook;

import com.letv.plugin.pluginloader.loader.JarClassLoader;
import com.letv.plugin.pluginloader.util.JLog;

/**
 * Created by chenlifeng on 16/5/19.
 */
public class ApkClassLoader extends JarClassLoader {

    public ApkClassLoader(String packagename, String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(packagename, dexPath, optimizedDirectory, libraryPath, parent);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        JLog.i("apk", "ApkClassLoader..className=" + className);
        return super.loadClass(className);
    }
}
