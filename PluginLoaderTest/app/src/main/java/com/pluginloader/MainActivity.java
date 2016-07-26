package com.pluginloader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.letv.zxing.ex.CaptureActivity;
import com.testplugin.TestActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    private View mBtn0;
    private View mBtn1;
    private View mBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn0 = findViewById(R.id.btn0);
        mBtn1 = findViewById(R.id.btn1);
        mBtn2 = findViewById(R.id.btn2);
        mBtn0.setOnClickListener(this);
        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mBtn0.getId() == view.getId()) {
            test0();
        } else if (mBtn1.getId() == view.getId()) {
            test1();
        } else if (mBtn2.getId() == view.getId()) {
            test2();
        }
    }

    private void test0() {
        CaptureActivity.launch(this);
    }

    private void test1(){
        TestActivity.launch(this);
    }

    private void test2() {
    }

    private void test3() {
    }
}
