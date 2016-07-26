package com.letv.plugin.pluginloader.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.letv.plugin.pluginloader.application.JarApplication;
import com.letv.plugin.pluginloader.common.Constant;
import com.letv.plugin.pluginloader.loadapk.Apker;
import com.letv.plugin.pluginloader.util.JarXmlParser.JarEntity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class JarUtil {
    public static final int TYPE_ALL = -1;
    public static final int TYPE_JAR = 0;
    public static final int TYPE_APK = 1;
    public static final int TYPE_LOADED_APK = 2;

    /**
     * 更新、初始化插件，并返回初始化失败列表
     *
     * @param context
     * @return 返回初始化失败的插件包名列表
     */
    public static void initPlugin(Context context) {
        if (!isMainProcess(context)) {
            return;
        }
        // 传递主工程ApplicationContext，并保存
        if (JarApplication.getInstance() == null) {
            JarApplication.setInstance(context.getApplicationContext());
        }
        Apker.ins().init(context);

        // 更新JAR模式插件
        updatePlugin(context, TYPE_JAR, true);
        // 更新JAR模式插件

        // 初始化apk插件
//        initApk(context);
    }

    /**
     * 更新插件
     *
     * @param context
     * @return
     */
    public synchronized static ArrayList<String> updatePlugin(Context context, int type, boolean startService) {
        ArrayList<String> error_jar = new ArrayList<String>(); // 保存初始化失败的插件包名
        JarXmlParser parser = new JarXmlParser(context);
        ArrayList<JarEntity> jars = parser.parseJars();
        if (null == jars) {
            return null;
        }
        // if (startService) {
        // Intent downIt = new Intent(context, DownLoadService.class);
        // context.startService(downIt);
        // }

        for (JarEntity jarEntity : jars) {
            int version = (int) SharedPreferenceUtils.get(context, jarEntity.getPackagename(), 0);
            if (jarEntity.getStatus() == 1 && jarEntity.getVersion() > version) { // 高于本地版本，则更新本地版本
                // 删除广告废弃的插件
                if ("com.letv.ads".equalsIgnoreCase(jarEntity.getPackagename())) {
                    deletebsoletingADS(context);
                }
                try {
                    String path = null;
                    if (type == TYPE_ALL) {
                        path = copyJar(context, jarEntity);
                    } else if (jarEntity.getType() == type) {
                        path = copyJar(context, jarEntity);
                    }
                    if (null == path) { // 拷贝失败
                        error_jar.add(jarEntity.getPackagename());
                        continue;
                    } else { // 拷贝成功保存到sp
                        SharedPreferenceUtils.put(context, jarEntity.getPackagename(), jarEntity.getVersion());
                    }
                } catch (Exception e) {
                    JLog.i("!!!!!!!e is " + e.getMessage());
                    JLog.i("!!!!!!!jarEntity.getPackagename() is " + jarEntity.getPackagename());
                    error_jar.add(jarEntity.getPackagename());
                }
            }
            if (type == TYPE_LOADED_APK && jarEntity.getType() == TYPE_LOADED_APK) {
                Apker.ins().addPlugin(context, jarEntity.getPackagename(), jarEntity.getName());
            }
        }
        // 升级广告插件
        updateADSAPK(context);
        return error_jar;
    }

    /**
     * @param context 将jar插件存放到应用目录下,不放到扩展存储设备中，防止代码注入
     *            插件初始化或更新插件时调用
     */
    public static String copyJar(Context context, JarEntity jarEntity) {
        JLog.logApk("拷贝插件到安装目录下 name=" + jarEntity.getName());
        String outPath = null;
        if (jarEntity.getType() == TYPE_APK) {
        } else if (jarEntity.getType() == TYPE_JAR || jarEntity.getType() == TYPE_ALL || jarEntity.getType() == TYPE_LOADED_APK) {
            outPath = context.getDir(Constant.JAR_IN_FOLDER_NAME, Context.MODE_PRIVATE)
                    + "/" + jarEntity.getName();
        }
        if (TextUtils.isEmpty(outPath)) {
            return null;
        }
        File dexInternalStoragePath = new File(outPath);
        BufferedInputStream bis = null;
        OutputStream dexWriter = null;
        if (dexInternalStoragePath.exists()) {
            dexInternalStoragePath.delete();
        }
        JLog.e("clf", "!!!!!!!dexInternalStoragePath=" + dexInternalStoragePath.getAbsolutePath());
        final int BUF_SIZE = 8 * 1024;
        try {
            bis = new BufferedInputStream(
                    context.getAssets().open(Constant.JAR_IN_MAIN_NAME + "/" + jarEntity.getName()));
            dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while ((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (dexWriter != null) {
                    dexWriter.close();
                }
            } catch (IOException e) {}
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {}
        }
        return dexInternalStoragePath.getAbsolutePath();
    }

    /**
     * 判断网络
     *
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        } else {
            return info.isAvailable();
        }
    }

    /**
     * 是否是主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        JLog.i("clf", "getProcessName(context, android.os.Process.myPid()="+getProcessName(context, android.os.Process.myPid()));
        return TextUtils.equals(getProcessName(context, android.os.Process.myPid()), "com.pluginloadertest");
    }

    /**
     * 获取进程名称
     *
     * @param cxt
     * @param pid
     * @return
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 判断是否有sd卡
     */
    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableStorage() {
        String storageDirectory = null;
        storageDirectory = Environment.getExternalStorageDirectory().toString();

        try {
            StatFs stat = new StatFs(storageDirectory);
            long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /**
     * 获取插件存放目录
     *
     * @param context
     * @return
     */
    public static String getJarInFolderName(Context context, String jarname) {
        // /data/data/com.letv.android.client/app_dex/xxxxxx.apk
        File dexInternalStoragePath =
                new File(context.getDir(Constant.JAR_IN_FOLDER_NAME, Context.MODE_PRIVATE), jarname);
        String inpath = dexInternalStoragePath.exists() ? dexInternalStoragePath.getAbsolutePath() : "";
        return inpath;
    }

    /**
     * 获取插件解压存放目录
     */
    public static String getJarOutFolderName(Context context) {
        File optimizedDexOutputPath = context.getDir(Constant.JAR_OUT_FOLDER_NAME, Context.MODE_PRIVATE);
        String outpath = optimizedDexOutputPath.exists() ? optimizedDexOutputPath.getAbsolutePath() : "";
        return outpath;
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (null == str) {
            return true;
        }
        return str.equals("");
    }

    /**
     * 删除废弃的广告插件
     */
    private static void deletebsoletingADS(Context context) {
        File updateFile = new File(context.getDir(Constant.JAR_IN_FOLDER_NAME, Context.MODE_PRIVATE),
                Constant.ADS_APK_UPDATE_NAME);
        File soFile = new File(context.getDir(Constant.ADS_SO_UPDATE_FOLDER, Context.MODE_PRIVATE),
                Constant.ADS_SO_FILE_NAME);
        boolean updateFileExist = updateFile.exists();
        if (updateFileExist) {
            updateFile.delete();
        }
        boolean soFileExist = soFile.exists();
        if (soFileExist) {
            soFile.delete();
        }
    }

    /**
     * 升级广告插件
     */
    private static void updateADSAPK(Context context) {
        File updateFile = new File(context.getDir(Constant.JAR_IN_FOLDER_NAME, Context.MODE_PRIVATE),
                Constant.ADS_APK_UPDATE_NAME);
        boolean updateFileExist = updateFile.exists();
        if (updateFileExist) {
            PackageInfo packageInfo = getPackageInfo(context, updateFile.getPath());
            // 防止apk不完整的情况
            if (null != packageInfo) {
                File apkFile = new File(context.getDir(Constant.JAR_IN_FOLDER_NAME, Context.MODE_PRIVATE),
                        Constant.ADS_APK_NAME);
                int apkVersionCode = packageInfo.versionCode;
                int curVersion = (int) SharedPreferenceUtils.get(context, "com.letv.ads", 0);
                // 当前版本小于更新的版本才升级
                if (curVersion < apkVersionCode) {
                    move(updateFile.getPath(), apkFile.getPath());
                    SharedPreferenceUtils.put(context, "com.letv.ads", apkVersionCode);
                } else {
                    deletebsoletingADS(context);
                }
            }
        }
    }

    private static void move(String oldPath, String newPath) {
        try {
            int byteRead;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
                oldFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static PackageInfo getPackageInfo(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            // should be something wrong with parse
            e.printStackTrace();
        }

        return pkgInfo;
    }
}
