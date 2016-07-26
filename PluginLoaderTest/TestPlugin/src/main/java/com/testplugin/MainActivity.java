package com.testplugin;

import android.os.Bundle;

import com.letv.plugin.pluginloader.activity.JarBaseFragmentActivity;
import com.testplugin.test.Test;

/**
 * Created by chenlifeng on 16/5/15.
 */
public class MainActivity extends JarBaseFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Test.run(context);
//        TestActivity1.launch(this);
    }

    @Override
    protected ClassLoader getClassLoader() {
        return MainActivity.class.getClassLoader();
    }
}
