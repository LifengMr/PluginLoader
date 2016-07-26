package com.letv.plugin.pluginconfig.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 说明：已在主工程manifest中注册ProxyReceiver
 * 适用于：插件中需要在manifest中静态注册的broadcastReceiver
 * @author chenlifeng1
 * 开发步骤
 * 1、在主工程manifest的ProxyReceiver中添加相应的action
 * 2、根据action区分广播来源，并做相应处理
 */
public class ProxyReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals("")){
			//TODO 处理相应广播
		}
	}
}
