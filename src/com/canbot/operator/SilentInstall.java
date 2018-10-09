/**
 * 
 */
package com.canbot.operator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.util.Log;

/**
 * describe ����Ĭ��װ��ʵ���࣬����install()����ִ�о���ľ�Ĭ��װ�߼��� 
 * 2016��8��15��  ����2:10:53
 * SilentInstall.java
 * @author peng_modify
 */
public class SilentInstall {

    /** 
     * ִ�о���ľ�Ĭ��װ�߼�����Ҫ�ֻ�ROOT�� 
     * @param apkPath 
     *          Ҫ��װ��apk�ļ���·�� 
     * @return ��װ�ɹ�����true����װʧ�ܷ���false�� 
     */  
    public boolean install(String apkPath) {  
        boolean result = false;  
        DataOutputStream dataOutputStream = null;  
        BufferedReader errorStream = null;  
        try {  
            // ����suȨ��  
            Process process = Runtime.getRuntime().exec("su");  
            dataOutputStream = new DataOutputStream(process.getOutputStream());  
            // ִ��pm install����  
            String command = "pm install -r " + apkPath + "\n";  
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));  
            dataOutputStream.flush();  
            dataOutputStream.writeBytes("exit\n");  
            dataOutputStream.flush();  
            process.waitFor();  
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));  
            String msg = "";  
            String line;  
            // ��ȡ�����ִ�н��  
            while ((line = errorStream.readLine()) != null) {  
                msg += line;  
            }   
            Log.d("TAG", "install msg is " + msg);  
            // ���ִ�н���а���Failure��������Ϊ�ǰ�װʧ�ܣ��������Ϊ��װ�ɹ�  
            if (!msg.contains("Failure")) {  
                result = true;   
            }  
        } catch (Exception e) {  
            Log.e("TAG", e.getMessage(), e);  
        } finally {  
            try {  
                if (dataOutputStream != null) {  
                    dataOutputStream.close();  
                }  
                if (errorStream != null) {  
                    errorStream.close();  
                }  
            } catch (IOException e) {  
                Log.e("TAG", e.getMessage(), e);  
            }  
        }  
        return result;  
    }  
    
    
    /**
     * �жϵ�ǰ�ֻ��Ƿ���ROOTȨ��
     * @return
     */
    public boolean isRoot(){
        boolean bool = false;

        try{
            if ((!new File("/system/bin/su").exists()) && (!new File("/system/bin/su").exists())){
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception e) {

        } 
        return bool;
    }
	
    
  
}  
	
	
