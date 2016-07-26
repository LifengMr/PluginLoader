package com.testplugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.letv.plugin.pluginconfig.commom.JarConstant;
import com.letv.plugin.pluginloader.activity.ProxyActivity;
import com.letv.plugin.pluginloader.activity.ProxyFragmentActivity;

/**
 * Created by chenlifeng on 16/5/16.
 */
public class TestActivity extends ProxyFragmentActivity {

    public static void launch(Context context) {
        Intent intent = new Intent(JarConstant.JAR_ACTION_TEST);
        intent.putExtra(ProxyActivity.EXTRA_JARNAME, JarConstant.TEST);
        intent.putExtra(ProxyActivity.EXTRA_PACKAGENAME, JarConstant.TEST_PACKAGE);
        intent.putExtra(ProxyActivity.EXTRA_CLASS, "MainActivity");
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
