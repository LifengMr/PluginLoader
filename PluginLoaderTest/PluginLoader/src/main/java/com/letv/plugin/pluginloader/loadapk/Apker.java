package com.letv.plugin.pluginloader.loadapk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.letv.plugin.pluginloader.loadapk.compat.ActivityThreadCompat;
import com.letv.plugin.pluginloader.loadapk.compat.LoadedApkCompat;
import com.letv.plugin.pluginloader.loadapk.entity.LoadedApk;
import com.letv.plugin.pluginloader.loadapk.hook.ApkClassLoader;
import com.letv.plugin.pluginloader.loadapk.hook.MainClassLoader;
import com.letv.plugin.pluginloader.util.JLog;
import com.letv.plugin.pluginloader.util.JarUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

;

/**
 * Created by chenlifeng on 16/5/19.
 */
public class Apker {

    public static final String TAG = Apker.class.getName();
    public static String MAIN_PACKAGE_NAME;
    private final ArrayMap<String, LoadedApk> mPackages = new ArrayMap<>();
    private Context mHostContext;
    private static Apker sApkLoader;

    private Apker() {}

    public static Apker ins() {
        if (sApkLoader == null) {
            synchronized (Apker.class) {
                if (sApkLoader == null) {
                    sApkLoader = new Apker();
                }
            }
        }
        return sApkLoader;
    }

    @SuppressLint("NewApi")
    public void init(Context hostContext) {
        if (!(hostContext instanceof Application)) {
            throw new IllegalArgumentException("init hostContext must be appliction");
        }

        mHostContext = hostContext;
        MAIN_PACKAGE_NAME = hostContext.getPackageName();

        MainClassLoader classLoader = new MainClassLoader(hostContext.getClassLoader());
        Object activityThread = ActivityThreadCompat.currentActivityThread();

        ActivityThreadCompat.setInstrumentation(activityThread);
        ActivityThreadCompat.setHHandler(activityThread);
        LoadedApkCompat.setClassLoader(activityThread, MAIN_PACKAGE_NAME, classLoader);

        // 更新JAR模式插件
        JarUtil.updatePlugin(mHostContext, JarUtil.TYPE_LOADED_APK, true);

        // 整合res资源
//        String[] resDirs = new String[mPackages.size() + 1];
//        resDirs[0] = mHostContext.getPackageResourcePath();
//        int i = 1;
//        Iterator<Map.Entry<String, LoadedApk>> it = mPackages.entrySet().iterator();
//        while (it.hasNext()){
//            resDirs[i++] = it.next().getValue().getResDir();
//        }
//        for (String s : resDirs){
//            JLog.i(TAG, "init s=" + s);
//        }

//        Context base = ((Application)hostContext).getBaseContext();
//        Resources resources = ResourcesCompat.createResources(base, resDirs);
//        JLog.i(TAG, "init resources=" + resources);
//        ContextImplCompat.setResources(base, resources);
    }

    public boolean changeResDir(String packageName) {
        //TOCO
        return true;
//        if (TextUtils.equals(mCurrentPackage, packageName)) {
//            return true;
//        }
//        if (TextUtils.isEmpty(packageName) || mLoadedApk == null) {
//            return false;
//        }
//        if (TextUtils.equals(MAIN_PACKAGE_NAME, packageName) && !TextUtils.isEmpty(MAIN_RES_DIR)) {
//            mCurrentPackage = packageName;
//            return RefInvoke.setFieldOjbect("android.app.LoadedApk", "mResDir",
//                    mLoadedApk, MAIN_RES_DIR);
//        }
//
//        JarClassLoader classLoader = sLoaders.get(packageName);
//        String resDir = classLoader.mJarpath;
//        mCurrentPackage = packageName;
//        return RefInvoke.setFieldOjbect("android.app.LoadedApk", "mResDir",
//                mLoadedApk, resDir);
    }

    public void addPlugin(Context hostContext, String packageName, String apkName) {
        JLog.i("clf", "ApkLoader...addPlugin packageName=" + packageName);
        String dexInternalPath = JarUtil.getJarInFolderName(hostContext, apkName);
        String optimizedDexOutputPath = JarUtil.getJarOutFolderName(hostContext);
        ApkClassLoader classLoader = new ApkClassLoader(packageName, dexInternalPath, optimizedDexOutputPath,
                hostContext.getApplicationInfo().nativeLibraryDir, hostContext.getClassLoader());
        LoadedApk loadedApk = LoadedApk.create(classLoader);
        JLog.i("clf", "ApkLoader...addPlugin loadedApk=" + loadedApk);
        if (loadedApk != null) {
            mPackages.put(packageName, loadedApk);
        }
    }

    public Class<?> loadClass(String className) {
        Class<?> clazz = loadClassInner(className);
        if (clazz == null) {
            clazz = loadClassGlobal(className);
        }
        return clazz;
    }

    public Class<?> loadClassInner(String className)  {
        String packageName = getPackageName(className);
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        LoadedApk loadedApk = mPackages.get(packageName);
        if (loadedApk == null) {
            return null;
        }
        ClassLoader classLoader = loadedApk.getClassLoader();
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    public Class<?> loadClassGlobal(String className) {
        Iterator<Map.Entry<String, LoadedApk>> it = mPackages.entrySet().iterator();
        while (it.hasNext()) {
            LoadedApk loadedApk = it.next().getValue();
            if (loadedApk == null) {
                continue;
            }
            ClassLoader classLoader = loadedApk.getClassLoader();
            try {
                Class<?> clazz = classLoader.loadClass(className);
                if (clazz != null) {
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
            }
        }
        return null;
    }

    public String getPackageName(String className){
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        Set<String> keys = mPackages.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String packageName = iterator.next();
            if (className.startsWith(packageName)) {
                return packageName;
            }
        }
        return null;
    }

    public Context host() {
        return mHostContext;
    }
}
