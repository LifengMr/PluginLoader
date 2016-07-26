package com.letv.zxing.ex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.letv.plugin.pluginconfig.R;
import com.letv.plugin.pluginconfig.commom.JarConstant;
import com.letv.plugin.pluginloader.activity.ProxyActivity;

/**
 * com.letv.zxing.CaptureActivity的代理启动类
 * @author chenlifeng1
 *
 */
public class CaptureActivity extends ProxyActivity {

    public static void launch(Context context) {
        // 跳转二维码
        Intent intent = new Intent(JarConstant.LEZXING_ACTION_CAPTUREACTIVITY);
        intent.putExtra(ProxyActivity.EXTRA_JARNAME, JarConstant.LETV_ZXING_NAME);
        intent.putExtra(ProxyActivity.EXTRA_PACKAGENAME, JarConstant.LETV_ZXING_PACKAGENAME);
        intent.putExtra(ProxyActivity.EXTRA_CLASS, "CaptureActivity");
        intent.putExtra("top", context.getString(R.string.more_setting_scan_qr_code));
        intent.putExtra("bottom", context.getString(R.string.more_setting_scan_qr_code_bottom));
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
}
