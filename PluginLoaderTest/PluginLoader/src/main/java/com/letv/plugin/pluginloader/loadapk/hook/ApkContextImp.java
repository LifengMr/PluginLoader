package com.letv.plugin.pluginloader.loadapk.hook;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.view.Display;

import com.letv.plugin.pluginloader.application.JarApplication;
import com.letv.plugin.pluginloader.loadapk.compat.ContextImplCompat;
import com.letv.plugin.pluginloader.loader.JarClassLoader;
import com.letv.plugin.pluginloader.loader.JarResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by chenlifeng on 16/5/20.
 */
public class ApkContextImp extends Context {
    private Context mBase;
    private JarClassLoader mJarClassLoader;
    private JarResources mResources;
    private Resources.Theme mTheme;
    private Object mResourcesManager;
    private String mApkResDir;

    public ApkContextImp(Context contextImpl, JarClassLoader classLoader) {
        this.mBase = contextImpl;
        this.mJarClassLoader = classLoader;
        init();
    }

    private void init() {
//        mResourcesManager = RefInvoke.getFieldOjbect("android.app.ResourcesManager", mBase, "mResourcesManager");
//        String resDir = mJarClassLoader.mJarpath;
//        Object displayId = RefInvoke.invokeMethod("android.app.ContextImpl", "getDisplayId",
//                mBase, new Class[]{}, new Object[]{});
        mApkResDir = mJarClassLoader.mJarpath;
        mResources = JarResources.getResourceByCl(mJarClassLoader, mBase);
        Resources.Theme theme = getResources().newTheme();
        theme.setTo(JarApplication.getInstance().getTheme());
        //TODO applayStyle
        mTheme = theme;
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    @Override
    public Resources getResources() {
        return mResources.getResources();
    }

    @Override
    public PackageManager getPackageManager() {
        return mBase.getPackageManager();
    }

    @Override
    public ContentResolver getContentResolver() {
        return mBase.getContentResolver();
    }

    @Override
    public Looper getMainLooper() {
        return mBase.getMainLooper();
    }

    @Override
    public Context getApplicationContext() {
        return mBase.getApplicationContext();
    }

    @Override
    public void setTheme(int i) {
        mBase.setTheme(i);
    }

    @Override
    public Resources.Theme getTheme() {
        if (mTheme == null) {
            return mBase.getTheme();
        }
        return mTheme;
    }

    @Override
    public ClassLoader getClassLoader() {
        return mBase.getClassLoader();
    }

    @Override
    public String getPackageName() {
        return mBase.getPackageName();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return mBase.getApplicationInfo();
    }

    @Override
    public String getPackageResourcePath() {
        return mApkResDir;
    }

    @Override
    public String getPackageCodePath() {
        return mBase.getPackageCodePath();
    }

    @Override
    public SharedPreferences getSharedPreferences(String s, int i) {
        return mBase.getSharedPreferences(s, i);
    }

    @Override
    public FileInputStream openFileInput(String s) throws FileNotFoundException {
        return mBase.openFileInput(s);
    }

    @Override
    public FileOutputStream openFileOutput(String s, int i) throws FileNotFoundException {
        return mBase.openFileOutput(s, i);
    }

    @Override
    public boolean deleteFile(String s) {
        return mBase.deleteFile(s);
    }

    @Override
    public File getFileStreamPath(String s) {
        return mBase.getFileStreamPath(s);
    }

    @Override
    public File getFilesDir() {
        return mBase.getFilesDir();
    }

    @SuppressLint("NewApi")
    @Override
    public File getNoBackupFilesDir() {
        return mBase.getNoBackupFilesDir();
    }

    @Nullable
    @Override
    public File getExternalFilesDir(String s) {
        return mBase.getExternalFilesDir(s);
    }

    @SuppressLint("NewApi")
    @Override
    public File[] getExternalFilesDirs(String s) {
        return mBase.getExternalFilesDirs(s);
    }

    @Override
    public File getObbDir() {
        return mBase.getObbDir();
    }

    @SuppressLint("NewApi")
    @Override
    public File[] getObbDirs() {
        return mBase.getObbDirs();
    }

    @Override
    public File getCacheDir() {
        return mBase.getCacheDir();
    }

    @SuppressLint("NewApi")
    @Override
    public File getCodeCacheDir() {
        return mBase.getCodeCacheDir();
    }

    @Nullable
    @Override
    public File getExternalCacheDir() {
        return mBase.getExternalCacheDir();
    }

    @SuppressLint("NewApi")
    @Override
    public File[] getExternalCacheDirs() {
        return mBase.getExternalCacheDirs();
    }

    @SuppressLint("NewApi")
    @Override
    public File[] getExternalMediaDirs() {
        return mBase.getExternalMediaDirs();
    }

    @Override
    public String[] fileList() {
        return mBase.fileList();
    }

    @Override
    public File getDir(String s, int i) {
        return mBase.getDir(s, i);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String s, int i, SQLiteDatabase.CursorFactory cursorFactory) {
        return mBase.openOrCreateDatabase(s, i, cursorFactory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String s, int i, SQLiteDatabase.CursorFactory cursorFactory, DatabaseErrorHandler databaseErrorHandler) {
        return mBase.openOrCreateDatabase(s, i, cursorFactory, databaseErrorHandler);
    }

    @Override
    public boolean deleteDatabase(String s) {
        return mBase.deleteDatabase(s);
    }

    @Override
    public File getDatabasePath(String s) {
        return mBase.getDatabasePath(s);
    }

    @Override
    public String[] databaseList() {
        return mBase.databaseList();
    }

    @Override
    public Drawable getWallpaper() {
        return mBase.getWallpaper();
    }

    @Override
    public Drawable peekWallpaper() {
        return mBase.peekWallpaper();
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return mBase.getWallpaperDesiredMinimumWidth();
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        return mBase.getWallpaperDesiredMinimumHeight();
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {
        mBase.setWallpaper(bitmap);
    }

    @Override
    public void setWallpaper(InputStream inputStream) throws IOException {
        mBase.setWallpaper(inputStream);
    }

    @Override
    public void clearWallpaper() throws IOException {
        mBase.clearWallpaper();
    }

    @Override
    public void startActivity(Intent intent) {
        mBase.startActivity(intent);
    }

    @SuppressLint("NewApi")
    @Override
    public void startActivity(Intent intent, Bundle bundle) {
        mBase.startActivity(intent, bundle);
    }

    @Override
    public void startActivities(Intent[] intents) {
        mBase.startActivities(intents);
    }

    @SuppressLint("NewApi")
    @Override
    public void startActivities(Intent[] intents, Bundle bundle) {
        mBase.startActivities(intents, bundle);
    }

    @Override
    public void startIntentSender(IntentSender intentSender, Intent intent, int i, int i1, int i2) throws IntentSender.SendIntentException {
        mBase.startIntentSender(intentSender, intent, i, i1, i2);
    }

    @SuppressLint("NewApi")
    @Override
    public void startIntentSender(IntentSender intentSender, Intent intent, int i, int i1, int i2, Bundle bundle) throws IntentSender.SendIntentException {
        mBase.startIntentSender(intentSender, intent, i, i1, i2, bundle);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        mBase.sendBroadcast(intent);
    }

    @Override
    public void sendBroadcast(Intent intent, String s) {
        mBase.sendBroadcast(intent, s);
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String s) {
        mBase.sendOrderedBroadcast(intent, s);
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, String s, BroadcastReceiver broadcastReceiver, Handler handler, int i, String s1, Bundle bundle) {
        mBase.sendOrderedBroadcast(intent, s, broadcastReceiver, handler, i, s1, bundle);
    }

    @SuppressLint("NewApi")
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle) {
        mBase.sendBroadcastAsUser(intent, userHandle);
    }

    @SuppressLint("NewApi")
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle userHandle, String s) {
        mBase.sendBroadcastAsUser(intent, userHandle, s);
    }

    @SuppressLint("NewApi")
    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, String s, BroadcastReceiver broadcastReceiver, Handler handler, int i, String s1, Bundle bundle) {
        mBase.sendOrderedBroadcastAsUser(intent, userHandle, s, broadcastReceiver, handler, i, s1, bundle);
    }

    @Override
    public void sendStickyBroadcast(Intent intent) {
        mBase.sendStickyBroadcast(intent);
    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver broadcastReceiver, Handler handler, int i, String s, Bundle bundle) {
        mBase.sendStickyOrderedBroadcast(intent, broadcastReceiver, handler, i, s, bundle);
    }

    @Override
    public void removeStickyBroadcast(Intent intent) {
        mBase.removeStickyBroadcast(intent);
    }

    @SuppressLint("NewApi")
    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {
        mBase.sendStickyBroadcastAsUser(intent, userHandle);
    }

    @SuppressLint("NewApi")
    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle userHandle, BroadcastReceiver broadcastReceiver, Handler handler, int i, String s, Bundle bundle) {
        mBase.sendStickyOrderedBroadcastAsUser(intent, userHandle, broadcastReceiver, handler, i, s, bundle);

    }

    @SuppressLint("NewApi")
    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle userHandle) {
        mBase.removeStickyBroadcastAsUser(intent, userHandle);
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        return mBase.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter, String s, Handler handler) {
        return mBase.registerReceiver(broadcastReceiver, intentFilter, s, handler);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        mBase.unregisterReceiver(broadcastReceiver);
    }

    @Nullable
    @Override
    public ComponentName startService(Intent intent) {
        return mBase.startService(intent);
    }

    @Override
    public boolean stopService(Intent intent) {
        return mBase.stopService(intent);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return mBase.bindService(intent, serviceConnection, i);
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        mBase.unbindService(serviceConnection);
    }

    @Override
    public boolean startInstrumentation(ComponentName componentName, String s, Bundle bundle) {
        return mBase.startInstrumentation(componentName, s, bundle);
    }

    @Override
    public Object getSystemService(String s) {
        return mBase.getSystemService(s);
    }

    @SuppressLint("NewApi")
    @Override
    public String getSystemServiceName(Class<?> aClass) {
        return mBase.getSystemServiceName(aClass);
    }

    @Override
    public int checkPermission(String s, int i, int i1) {
        return mBase.checkPermission(s, i, i1);
    }

    @Override
    public int checkCallingPermission(String s) {
        return mBase.checkCallingPermission(s);
    }

    @Override
    public int checkCallingOrSelfPermission(String s) {
        return mBase.checkCallingOrSelfPermission(s);
    }

    @SuppressLint("NewApi")
    @Override
    public int checkSelfPermission(String s) {
        return mBase.checkSelfPermission(s);
    }

    @Override
    public void enforcePermission(String s, int i, int i1, String s1) {
        mBase.enforcePermission(s, i, i1, s1);
    }

    @Override
    public void enforceCallingPermission(String s, String s1) {
        mBase.enforceCallingPermission(s, s1);
    }

    @Override
    public void enforceCallingOrSelfPermission(String s, String s1) {
        mBase.enforceCallingOrSelfPermission(s, s1);
    }

    @Override
    public void grantUriPermission(String s, Uri uri, int i) {
        mBase.grantUriPermission(s, uri, i);
    }

    @Override
    public void revokeUriPermission(Uri uri, int i) {
        mBase.revokeUriPermission(uri, i);
    }

    @Override
    public int checkUriPermission(Uri uri, int i, int i1, int i2) {
        return mBase.checkUriPermission(uri, i, i1, i2);
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int i) {
        return mBase.checkCallingUriPermission(uri, i);
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int i) {
        return mBase.checkCallingOrSelfUriPermission(uri, i);
    }

    @Override
    public int checkUriPermission(Uri uri, String s, String s1, int i, int i1, int i2) {
        return mBase.checkUriPermission(uri, s, s1, i, i1, i2);
    }

    @Override
    public void enforceUriPermission(Uri uri, int i, int i1, int i2, String s) {
        mBase.enforceUriPermission(uri, i, i1, i2, s);
    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int i, String s) {
        mBase.enforceCallingUriPermission(uri, i, s);
    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int i, String s) {
        mBase.enforceCallingOrSelfUriPermission(uri, i, s);
    }

    @Override
    public void enforceUriPermission(Uri uri, String s, String s1, int i, int i1, int i2, String s2) {
        mBase.enforceUriPermission(uri, s, s1, i, i1, i2, s2);
    }

    @Override
    public Context createPackageContext(String s, int i) throws PackageManager.NameNotFoundException {
        return mBase.createPackageContext(s, i);
    }

    @SuppressLint("NewApi")
    @Override
    public Context createConfigurationContext(Configuration configuration) {
        return mBase.createConfigurationContext(configuration);
    }

    @SuppressLint("NewApi")
    @Override
    public Context createDisplayContext(Display display) {
        return mBase.createDisplayContext(display);
    }

    // 4.3..
    public String getBasePackageName() {
        return ContextImplCompat.getBasePackageName(mBase);
    }

    // 4.4..
    public String getOpPackageName() {
        return ContextImplCompat.getOpPackageName(mBase);
    }
}
