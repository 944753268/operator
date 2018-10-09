/**
 * 
 */
package com.canbot.operator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * describe ：
 * 2016年8月15日  下午6:02:23
 * BootBroadcastReceive.java
 * @author peng_modify
 */
public class BootBroadcastReceive extends BroadcastReceiver {
	
	private static String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("--" + intent.getAction());
		if(intent.getAction().equals(BOOT_ACTION)){
			//开启service
//			context.startService(new Intent(context, OperatorService.class));
//			System.out.println("----reboot----");
		}
		
	}

}
