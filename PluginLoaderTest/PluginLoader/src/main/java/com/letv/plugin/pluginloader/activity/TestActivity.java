package com.letv.plugin.pluginloader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.letv.plugin.pluginloader.R;

/**
 * Created by chenlifeng on 16/5/15.
 */
public class TestActivity extends ProxyActivity {

    public static void laucn(Context context){
        Intent it = new Intent(context, TestActivity.class);
        context.startActivity(it);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.plugin_main);
    }
}
