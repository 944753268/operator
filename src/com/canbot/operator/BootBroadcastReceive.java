/**
 * 
 */
package com.canbot.operator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * describe ��
 * 2016��8��15��  ����6:02:23
 * BootBroadcastReceive.java
 * @author peng_modify
 */
public class BootBroadcastReceive extends BroadcastReceiver {
	
	private static String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("--" + intent.getAction());
		if(intent.getAction().equals(BOOT_ACTION)){
			//����service
//			context.startService(new Intent(context, OperatorService.class));
//			System.out.println("----reboot----");
		}
		
	}

}
