package com.letv.plugin.pluginloader.loadapk.helper;

import android.content.Intent;

import com.letv.plugin.pluginloader.loadapk.constant.Constant;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by chenlifeng on 16/5/23.
 */
public class ApkHelper {

    public static String unwrapIntent(Intent intent) {
        if (intent == null) {
            return null;
        }
        Set<String> categories = intent.getCategories();
        if (categories == null) return null;

        // Get plugin activity class name from categories
        Iterator<String> it = categories.iterator();
        while (it.hasNext()) {
            String category = it.next();
            if (category.charAt(0) == Constant.REDIRECT_FLAG) {
                return category.substring(1);
            }
        }
        return null;
    }
}
