package com.testplugin.test;

import android.content.Context;
import android.util.Log;

/**
 * Created by chenlifeng on 16/5/16.
 */
public class Test {
    public void a(){
        System.out.print("aaa");
    }

    public static void run(Context context){
        Test t = new Test();
        t.test(context);
    }

    private void test(Context context){
//        Toast.makeText(context, "测试测试", Toast.LENGTH_SHORT).show();
        Log.i("clf", "Test...test");
    }
}
