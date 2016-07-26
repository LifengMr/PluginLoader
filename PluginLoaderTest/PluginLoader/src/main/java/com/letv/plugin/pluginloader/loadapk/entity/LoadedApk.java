package com.letv.plugin.pluginloader.loadapk.entity;

import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.text.TextUtils;

import com.letv.plugin.pluginloader.loadapk.hook.ApkClassLoader;
import com.letv.plugin.pluginloader.loadapk.parser.ApkPackageParser;

/**
 * Created by chenlifeng on 16/5/22.
 */
public class LoadedApk {

    private boolean mAvailable;
    private ApkClassLoader mClassLoader;
    private Resources mResources;
    private PackageInfo mPackageInfo;
    private String mPackageName;
    private String mResDir;

    public static LoadedApk create(ApkClassLoader classLoader) {
        LoadedApk loadedApk = new LoadedApk(classLoader);
        loadedApk.parsePackage(classLoader.mJarpath, classLoader.mPackagename);
        return loadedApk;
    }

    private LoadedApk(ApkClassLoader classLoader) {
        if (classLoader == null) {
            throw new IllegalArgumentException("ApkClassLoader cannot be null");
        }
        mClassLoader = classLoader;
        mPackageName = mClassLoader.mPackagename;
        mResDir = mClassLoader.mJarpath;
    }

    private void parsePackage(String packagePath, String packageName) {
        if (TextUtils.isEmpty(packagePath) || TextUtils.isEmpty(packageName)) {
            setAvailable(false);
            return;
        }
        ApkPackageParser packageParser = new ApkPackageParser(packagePath, packageName);
        boolean success = packageParser.parsePackage();
        //TODO 验证签名通过执行collectActivities
        success = packageParser.collectActivities();
        mResources = packageParser.getResources();
        mPackageInfo = packageParser.getPackageInfo();

        if (!success) {
            setAvailable(false);
            return;
        }

        ActivityIntentResolver.ins().register(this, packageParser);
    }

    public ClassLoader getClassLoader() {
        return mClassLoader;
    }

    public Resources getResources() {
        return mResources;
    }

    public PackageInfo getPackageInfo() {
        return mPackageInfo;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getResDir() {
        return mResDir;
    }

    public boolean isAvailable() {
        return mAvailable;
    }

    public void setAvailable(boolean available) {
        mAvailable = available;
    }
}
