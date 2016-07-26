package com.letv.plugin.pluginconfig.commom;

public interface JarConstant {
	/**
	 * 配置插件参数
	 */
	//二维码扫描插件
	String LETV_ZXING_NAME = "LetvZxing.apk";
	String LETV_ZXING_PACKAGENAME = "com.letv.zxing";
	

	//TEST
	String TEST = "TestPlugin.apk";
	String TEST_PACKAGE = "com.testplugin";
	
	
	/*****************插件Activity action***********************/
	//base Action(启动ProxyActivity)
	String PROXY_ACTION = "com.letv.plugin.pluginloader.proxyactivity.VIEW";
	
	//LetvZxing
	String LEZXING_ACTION_CAPTUREACTIVITY = "com.letv.zxing.ex.CaptureActivity";

	//TEST
	String JAR_ACTION_TEST = "com.testplugin.TestActivity";
}
