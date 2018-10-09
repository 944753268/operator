/**
 * 
 */
package com.canbot.operator;

import android.app.Application;

/**
 * describe ：
 * 2016年9月26日  上午9:19:35
 * MyApplication.java
 * @author peng_modify
 */
public class MyApplication extends Application{

	
	@Override
	public void onCreate() {
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		
		
	}
	
	
}
