package com.letv.plugin.pluginloader.loadapk.compat;

import android.app.Instrumentation;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.letv.plugin.pluginloader.loadapk.helper.ReflectHelper;
import com.letv.plugin.pluginloader.loadapk.hook.ApkInstrumentation;
import com.letv.plugin.pluginloader.loadapk.hook.HCallBack;
import com.letv.plugin.pluginloader.loadapk.utils.RefInvoke;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by chenlifeng on 16/5/25.
 */
public class ActivityThreadCompat {

    public static Class<?> clazz() {
        try {
            return Class.forName("android.app.ActivityThread");
        } catch (ClassNotFoundException e) {
        }
        return null;
    }

    public static Object currentActivityThread() {
        Object activityThread = ReflectHelper.invokeStaticMethod(clazz(), "currentActivityThread", null, null);
        if (activityThread == null) {
            activityThread = reGetActivityThread();
        }
        return activityThread;
    }

    private static Object reGetActivityThread() {
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = new Handler(mainLooper);
        final Object lock = new Object();
        final Object[] activityThread = new Object[1];
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    activityThread[0] = ReflectHelper.invokeStaticMethod(clazz(), "currentActivityThread", null, null);
                } catch (Exception e) {
                } finally {
                    lock.notify();
                }
            }
        });
        if (activityThread[0] == null && Looper.myLooper() != mainLooper) {
            try {
                lock.wait(300);
            } catch (InterruptedException e) {
            }
        }
        return activityThread[0];
    }

    public static Object getPackage(Object activityThread, String packageName) {
        Object loadedApk;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            android.util.ArrayMap mPackages = (android.util.ArrayMap) RefInvoke.getFieldOjbect(
                    "android.app.ActivityThread", activityThread, "mPackages");
            WeakReference ref = (WeakReference) mPackages.get(packageName);
            loadedApk = ref.get();
        } else {
            HashMap mPackages = (HashMap) RefInvoke.getFieldOjbect(
                    "android.app.ActivityThread", activityThread, "mPackages");
            WeakReference ref = (WeakReference) mPackages.get(packageName);
            loadedApk = ref.get();
        }
        return loadedApk;
    }

    public static void setInstrumentation(Object activityThread) {
        Field field = ReflectHelper.getField(clazz(), "mInstrumentation", false);
        Instrumentation instrumentation = ReflectHelper.readField(field, activityThread);
        if (!(instrumentation instanceof ApkInstrumentation)) {
            ApkInstrumentation apkInstrumentation = new ApkInstrumentation(instrumentation);
            ReflectHelper.writeField(field, activityThread, apkInstrumentation);
        }
    }

    public static void setHHandler(Object activityThread) {
        Field field = ReflectHelper.getField(clazz(), "mH", false);
        Handler handler = ReflectHelper.readField(field, activityThread);
        field = ReflectHelper.getField(Handler.class, "mCallback", true);
        ReflectHelper.writeField(field, handler, new HCallBack());
    }
}
