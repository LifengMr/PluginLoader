package com.letv.plugin.pluginloader.loadapk.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.letv.plugin.pluginloader.loadapk.Apker;
import com.letv.plugin.pluginloader.loadapk.compat.InstrumentationCompat;
import com.letv.plugin.pluginloader.loadapk.constant.Constant;
import com.letv.plugin.pluginloader.loadapk.entity.ActivityIntentResolver;
import com.letv.plugin.pluginloader.util.JLog;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApkInstrumentation extends Instrumentation {
    public static final String TAG = ApkInstrumentation.class.getName();

    private static final String STUB_ACTIVITY_PREFIX = ".A";
    private static final String STUB_ACTIVITY_TRANSLUCENT = STUB_ACTIVITY_PREFIX + '1';
    private static final int STUB_ACTIVITIES_COUNT = 4;
    private Instrumentation mInstrumentation;

    public ApkInstrumentation(Instrumentation instrumentation) {
        mInstrumentation = instrumentation;
    }

    /**
     * api 21...
     * @param who
     * @param contextThread
     * @param token
     * @param target
     * @param intent
     * @param requestCode
     * @param options
     * @return
     */
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, android.os.Bundle options) {
        wrapIntent(intent);
        return InstrumentationCompat.execStartActivity(mInstrumentation,
                who, contextThread, token, target, intent, requestCode, options);
    }

    /**
     * api ...20
     * @param who
     * @param contextThread
     * @param token
     * @param target
     * @param intent
     * @param requestCode
     * @return
     */
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode) {
        wrapIntent(intent);
        return InstrumentationCompat.execStartActivity(mInstrumentation,
                who, contextThread, token, target, intent, requestCode);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        JLog.i("clf", "callActivityOnCreate activity.getClass().getName()=" + activity.getClass().getName());
//        if(activity.getClass().getName().equals("com.testapk.ApkMainActivity")){
//            try {
//                setResDir(activity);
//            } catch (ClassNotFoundException e) {
//                JLog.i("clf", "callActivityOnCreate ClassNotFoundException=" + e);
//            } catch (NoSuchMethodException e) {
//                JLog.i("clf", "callActivityOnCreate NoSuchMethodException=" + e);
//            } catch (InvocationTargetException e) {
//                JLog.i("clf", "callActivityOnCreate InvocationTargetException=" + e);
//            } catch (IllegalAccessException e) {
//                JLog.i("clf", "callActivityOnCreate IllegalAccessException=" + e);
//            }
//        }

        if (mInstrumentation != null) {
            mInstrumentation.callActivityOnCreate(activity, icicle);
        } else {
            super.callActivityOnCreate(activity, icicle);
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        JLog.i("clf", "callActivityOnDestroy activity.getClass().getName()=" + activity.getClass().getName());
        //TODO
        Apker.ins().changeResDir(Apker.MAIN_PACKAGE_NAME);

        if (mInstrumentation != null) {
            mInstrumentation.callActivityOnDestroy(activity);
        } else {
            super.callActivityOnDestroy(activity);
        }
    }

    private void wrapIntent(Intent intent) {
        ComponentName component = intent.getComponent();
        JLog.i(TAG, "wrapIntent..component=" + component);
        String realClazz;
        if (component == null) {
            component = intent.resolveActivity(Apker.ins().host().getPackageManager());
            JLog.i(TAG, "wrapIntent.host.component=" + component);
            if (component != null) return;

            realClazz = resolveActivity(intent);
            JLog.i(TAG, "wrapIntent.realClazz=" + realClazz);
            if (realClazz == null) return;
        } else {
            realClazz = component.getClassName();
            JLog.i(TAG, "wrapIntent.11realClazz=" + realClazz);
        }

        ActivityInfo ai = ActivityIntentResolver.ins().fetchActivityInfo(realClazz);
        JLog.i(TAG, "wrapIntent.ai=" + ai);
        if (ai == null) return;

        // Carry the real(plugin) class for incoming `newActivity' method.
        intent.addCategory(Constant.REDIRECT_FLAG + realClazz);
        String stubClazz = dequeueStubActivity(ai, realClazz);
        JLog.i(TAG, "wrapIntent.stubClazz=" + stubClazz);
        intent.setComponent(new ComponentName(Apker.ins().host(), stubClazz));
    }

    private String resolveActivity(Intent intent) {;
        Iterator<Map.Entry<String, List<IntentFilter>>> iterator
                = ActivityIntentResolver.ins().getFilters().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, List<IntentFilter>> entry = iterator.next();
            List<IntentFilter> filters = entry.getValue();
            for (IntentFilter filter : filters) {
                if (filter.hasAction(Intent.ACTION_VIEW)) {
                    // TODO: match uri
                }
                if (filter.hasCategory(Intent.CATEGORY_DEFAULT)) {
                    // custom action
                    if (filter.hasAction(intent.getAction())) {
                        // hit
                        return entry.getKey();
                    }
                }
            }
        }
        return null;
    }

    private String[] mStubQueue;

    private String dequeueStubActivity(ActivityInfo ai, String realActivityClazz) {
        if (ai.launchMode == ActivityInfo.LAUNCH_MULTIPLE) {
            // In standard mode, the stub activity is reusable.
            // Cause the `windowIsTranslucent' attribute cannot be dynamically set,
            // We should choose the STUB activity with translucent or not here.
            Resources.Theme theme = Apker.ins().host().getResources().newTheme();
            theme.applyStyle(ai.getThemeResource(), true);
            TypedArray sa = theme.obtainStyledAttributes(
                    new int[] { android.R.attr.windowIsTranslucent });
            boolean translucent = sa.getBoolean(0, false);
            sa.recycle();
            return translucent ? STUB_ACTIVITY_TRANSLUCENT : "com.letv.plugin.pluginloader" + STUB_ACTIVITY_PREFIX;
        }

        int availableId = -1;
        int stubId = -1;
        int countForMode = STUB_ACTIVITIES_COUNT;
        int countForAll = countForMode * 3; // 3=[singleTop, singleTask, singleInstance]
        if (mStubQueue == null) {
            // Lazy init
            mStubQueue = new String[countForAll];
        }
        int offset = (ai.launchMode - 1) * countForMode;
        for (int i = 0; i < countForMode; i++) {
            String usedActivityClazz = mStubQueue[i + offset];
            if (usedActivityClazz == null) {
                if (availableId == -1) availableId = i;
            } else if (usedActivityClazz.equals(realActivityClazz)) {
                stubId = i;
            }
        }
        if (stubId != -1) {
            availableId = stubId;
        } else if (availableId != -1) {
            mStubQueue[availableId + offset] = realActivityClazz;
        } else {
            // TODO:
            Log.e(TAG, "Launch mode " + ai.launchMode + " is full");
        }
        return STUB_ACTIVITY_PREFIX + ai.launchMode + availableId;
    }


    private void test() {
        try {
            Class clazz = Class.forName("android.app.Activity");
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                JLog.i("apk", "test method=" + method);
            }
        } catch (ClassNotFoundException e) {
        }
    }

    private Method getMethod(String name) {
        try {
            Class clazz = Class.forName("android.app.Activity");
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                JLog.i("apk", "test method=" + method);
                if(method.getName().equals(name)){
                    return method;
                }
            }
        } catch (ClassNotFoundException e) {
        }
        return null;
    }
//
//    private void activityAttach(Activity activity) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
////        test();
//
//
//        ApkClassLoader apkClassLoader = (ApkClassLoader) activity.getClass().getClassLoader();
//        JLog.i("clf", "~~~~activityAttach apkClassLoader=" + apkClassLoader);
//        ApkContextImp contextImp = new ApkContextImp(activity.getBaseContext(), apkClassLoader);
//        JLog.i("clf", "~~~~activityAttach contextImp=" + contextImp);
//        Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread",
//                new Class[] {}, new Object[] {});
//        Object token = RefInvoke.getFieldOjbect("android.app.Activity", activity, "mToken");
//        JLog.i("clf", "~~~~activityAttach token=" + token);
//        Object ident = RefInvoke.getFieldOjbect("android.app.Activity", activity, "mIdent");
//        JLog.i("clf", "~~~~activityAttach ident=" + ident);
//        Application application = activity.getApplication();
//        JLog.i("clf", "~~~~activityAttach application=" + application);
//        Intent intent = activity.getIntent();
//        JLog.i("clf", "~~~~activityAttach intent=" + intent);
//        Object activityInfo = RefInvoke.getFieldOjbect("android.app.Activity", activity, "mActivityInfo");
//        JLog.i("clf", "~~~~activityAttach activityInfo=" + activityInfo);
//        Object title = activity.getTitle();
//        JLog.i("clf", "~~~~activityAttach title=" + title);
//        Object parent = activity.getParent();
//        JLog.i("clf", "~~~~activityAttach parent=" + parent);
//        Object embeddedID = RefInvoke.getFieldOjbect("android.app.Activity", activity, "mEmbeddedID");
//        JLog.i("clf", "~~~~activityAttach embeddedID=" + embeddedID);
//        Object lastNonConfigurationInstances = RefInvoke.getFieldOjbect("android.app.Activity",
//                activity, "mLastNonConfigurationInstances");
//        JLog.i("clf", "~~~~activityAttach lastNonConfigurationInstances=" + lastNonConfigurationInstances);
//        Object config = RefInvoke.getFieldOjbect("android.app.Activity", activity, "mCurrentConfig");
//        JLog.i("clf", "~~~~activityAttach config=" + config);
//        Class[] params = new Class[] {Context.class, Class.forName("android.app.ActivityThread"), Instrumentation.class,
//                IBinder.class, Integer.class, Application.class, Intent.class, ActivityInfo.class,
//                CharSequence.class, Activity.class, String.class, Class.forName("android.app.Activity$NonConfigurationInstances"),
//                Configuration.class, Class.forName("com.android.internal.app.IVoiceInteractor")};
//        JLog.i("clf", "~~~~activityAttach params=" + params);
//        Object[] objects = new Object[] {contextImp, currentActivityThread, this, token, ident, application, intent, activityInfo,
//                title, parent, embeddedID, lastNonConfigurationInstances, config, null};
//        JLog.i("clf", "~~~~activityAttach objects=" + objects);
//
//        FragmentManager fm = activity.getFragmentManager();
////        RefInvoke.setFieldOjbect("android.app.FragmentManager", "mActivity", fm, null);
//        FieldUtils.writeDeclaredField(fm, "mActivity", null);
////        RefInvoke.invokeMethod("android.app.Activity", "attach", activity, params, objects);
////        RefInvoke.setFieldOjbect("android.app.Activity", "mBase", activity, null);
//        FieldUtils.writeDeclaredField(activity, "mBase", null);
//        JLog.i("clf", "~~~~activityAttach mbase=" + activity.getBaseContext());
//
//        for (Object object : objects) {
//            JLog.i("apk", "!!!!!object=" + object);
//        }
//        Method method = getMethod("attach");
//        JLog.i("clf", "~~~~activityAttach method=" + method);
//        method.setAccessible(true);
//        method.invoke(activity, objects);
//
//        //TODO
//
////        Method method = getMethod("setParent");
////        method.setAccessible(true);
////        method.invoke(activity, new Object[]{null});
//    }
//
//    @SuppressLint("NewApi")
//    private void setResDir(Activity activity) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        String dexInternalPath = JarUtil.getJarInFolderName(mHostContext, "TestApk.apk");
//        Object object = ActivityThreadCompat.currentActivityThread();
//        if (object != null) {
//            ArrayMap mPackagesObj = (ArrayMap) FieldUtils.readField(object, "mPackages");
////            JLog.i("clf", "setResDir..mPackagesObj="+mPackagesObj);
//            WeakReference wr = (WeakReference) mPackagesObj.get(mHostContext.getPackageName());
////            FieldUtils.writeDeclaredField(wr.get(), "mResDir", dexInternalPath);
////            FieldUtils.writeDeclaredField(wr.get(), "mResources", null);
//            FieldUtils.writeDeclaredField(activity, "mResources", null);
//            FieldUtils.writeDeclaredField(activity, "mInflater", null);
////            Class clazz = Class.forName("com.android.internal.policy.PolicyManager");
////            Object obj = MethodUtils.invokeStaticMethod(clazz, "makeNewLayoutInflater", new Object[]{activity}, new Class[]{Context.class});
////            FieldUtils.writeDeclaredField(activity, "mInflater", obj);
//
//            activityAttach(activity);
//
////            JLog.i("clf", "setResDir..mInflater="+FieldUtils.readField(activity, "mInflater"));
////            View view = ((LayoutInflater)obj).inflate(R.layout.apk_item, null);
////            JLog.i("clf", "setResDir..view=" + view);
////            JLog.i("clf", "setResDir..R.layout.apk_item=" + R.layout.apk_item);
////            JLog.i("clf", "setResDir..R.id.textView1=" + R.id.textView1);
////            String str = ((TextView)view.findViewById(R.id.textView1)).getText().toString();
//        }
//
//    }
}
