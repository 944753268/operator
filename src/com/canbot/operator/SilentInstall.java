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
 * describe ：静默安装的实现类，调用install()方法执行具体的静默安装逻辑。 
 * 2016年8月15日  下午2:10:53
 * SilentInstall.java
 * @author peng_modify
 */
public class SilentInstall {

    /** 
     * 执行具体的静默安装逻辑，需要手机ROOT。 
     * @param apkPath 
     *          要安装的apk文件的路径 
     * @return 安装成功返回true，安装失败返回false。 
     */  
    public boolean install(String apkPath) {  
        boolean result = false;  
        DataOutputStream dataOutputStream = null;  
        BufferedReader errorStream = null;  
        try {  
            // 申请su权限  
            Process process = Runtime.getRuntime().exec("su");  
            dataOutputStream = new DataOutputStream(process.getOutputStream());  
            // 执行pm install命令  
            String command = "pm install -r " + apkPath + "\n";  
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));  
            dataOutputStream.flush();  
            dataOutputStream.writeBytes("exit\n");  
            dataOutputStream.flush();  
            process.waitFor();  
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));  
            String msg = "";  
            String line;  
            // 读取命令的执行结果  
            while ((line = errorStream.readLine()) != null) {  
                msg += line;  
            }   
            Log.d("TAG", "install msg is " + msg);  
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功  
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
     * 判断当前手机是否有ROOT权限
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
	
	
