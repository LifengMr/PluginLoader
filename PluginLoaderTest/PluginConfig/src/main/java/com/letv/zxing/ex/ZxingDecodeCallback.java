package com.letv.zxing.ex;

import android.content.Context;

public interface ZxingDecodeCallback {
	/**
	 * result扫描结果,如果为null，代表扫描失败
	 */
	void callback(Context context, ParseResultEntity result);
}
