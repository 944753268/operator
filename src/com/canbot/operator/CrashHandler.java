/**
 * 
 */
package com.canbot.operator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * describe �� 2016��9��22�� ����2:14:49 CrashHandler.java
 *  UncaughtExceptionHanlder ���� : ���� �̱߳�δ������쳣��ֹ �����, һ��������δ�����쳣����, ϵͳ�ͻ�ص������ 
 * uncaughtException ����; 
 * 
 * 
 * ������־����ļ�·�� : /storage/sdcard0/crashinfo/crash-2015-04-27-19-31-41-1430134301642.txt;
-- ˵�� : ���� /storage/sdcard0/ ��ϵͳĬ�ϵ� SD ��·��, crashinfo/crash-2015-04-27-19-31-41-1430134301642.txt �����Ǵ������ļ�;
 * 
 * @author peng_modify
 */
public class CrashHandler implements UncaughtExceptionHandler {  
    // ���ڴ�ӡ��־�� TAG ��ʶ��  
    public static final String TAG = "octopus.CrashHandler";  
  
    // ϵͳĬ�ϵ�UncaughtException������  
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
    // �����Context����  
    private Context mContext;  
    // �����洢�豸��Ϣ���쳣��Ϣ  
    private Map<String, String> mInfos = new HashMap<String, String>();  
    // ���ڸ�ʽ������,��Ϊ��־�ļ�����һ����  
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
  
    // ����ģʽ  
    private static CrashHandler INSTANCE = new CrashHandler();  
    private CrashHandler() {  
    }  
    public static CrashHandler getInstance() {  
        return INSTANCE;  
    }  
  
    /** 
     * ��ʼ������, ��ϵͳ��ע�� 
     * @param context 
     */  
    public void init(Context context) {  
        mContext = context;  
        // ��ȡϵͳĬ�ϵ� UncaughtException ������  
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        // ���ø� CrashHandler Ϊ�����Ĭ�ϴ�����  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
  
    /* 
     * ����δ������쳣ʱ, ���Զ��ص��÷��� 
     * (non-Javadoc) 
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable) 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        /* 
         * ���� handleException() ����������߳� 
         * ������� true ˵������ɹ�, ������� false �����Ĭ�ϵ��쳣������������ 
         * һ������¸÷�������ɹ����� 
         */  
        if (!handleException(ex) && mDefaultHandler != null) {  
            // ����û�û�д�������ϵͳĬ�ϵ��쳣������������  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
                Log.e(TAG, "error : ", e);  
            }  
            // �˳�����  
            android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(1);  
        }  
    }  
  
    /** 
     * �Զ��������,�ռ�������Ϣ ���ʹ��󱨸�Ȳ������ڴ����. 
     * @param ex     
     *      �쳣��Ϣ 
     * @return  
     *      true:��������˸��쳣��Ϣ;���򷵻�false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  
        /* 
         * ʹ��Toast����ʾ�쳣��Ϣ,  
         * ���������̻߳�����,  
         * ����ʵʱ���� Toast ��Ϣ,  
         * �������������߳��д��� Toast ��Ϣ 
         */  
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(mContext, "�ܱ�Ǹ,��������쳣,�����˳�.", Toast.LENGTH_LONG)  
                        .show();  
                Looper.loop();  
            }  
        }.start();  
        // �ռ��豸������Ϣ  
        collectDeviceInfo(mContext);  
        // ������־�ļ�  
        saveCrashInfo2File(ex);  
        return true;  
    }  
  
    /** 
     * �ռ��豸������Ϣ, ���ֻ�������Ϣ�洢�� 
     * @param ctx 
     *      �����Ķ��� 
     */  
    public void collectDeviceInfo(Context ctx) {  
        try {  
            //��ȡ��������  
            PackageManager pm = ctx.getPackageManager();  
            //��ȡ����Ϣ  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),  
                    PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                //�汾��  
                String versionName = pi.versionName == null ? "null"  
                        : pi.versionName;  
                //�汾����  
                String versionCode = pi.versionCode + "";  
                //���汾��Ϣ��ŵ� ��Ա���� Map<String, String> mInfos ��  
                this.mInfos.put("versionName", versionName);  
                this.mInfos.put("versionCode", versionCode);  
            }  
        } catch (NameNotFoundException e) {  
            Log.e(TAG, "an error occured when collect package info", e);  
        }  
          
        //��ȡ Build �ж���ı���, ʹ�÷��䷽ʽ��ȡ, �����ж������豸��صı�����Ϣ  
        Field[] fields = Build.class.getDeclaredFields();  
        //������ȡ�����, ����Щ��Ϣ��ŵ���Ա���� Map<String, String> mInfos ��  
        for (Field field : fields) {  
            try {  
                //���� Build ��Ա�����ɷ���  
                field.setAccessible(true);  
                //�� �豸��ص���Ϣ��ŵ� mInfos ��Ա������  
                mInfos.put(field.getName(), field.get(null).toString());  
                Log.d(TAG, field.getName() + " : " + field.get(null));  
            } catch (Exception e) {  
                Log.e(TAG, "an error occured when collect crash info", e);  
            }  
        }  
    }  
  
    /** 
     * ���������Ϣ���ļ��� 
     * @param ex 
     * @return �����ļ�����,���ڽ��ļ����͵������� 
     */  
    private String saveCrashInfo2File(Throwable ex) {  
        //�洢��ص��ַ�����Ϣ  
        StringBuffer sb = new StringBuffer();  
        //����Ա���� Map<String, String> mInfos  �е����� �洢�� StringBuffer sb ��  
        for (Map.Entry<String, String> entry : this.mInfos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
  
        //�� StringBuffer sb �е��ַ���д�����ļ���  
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
        String result = writer.toString();  
        sb.append(result);  
        try {  
            long timestamp = System.currentTimeMillis();  
            String time = formatter.format(new Date());  
            String fileName = "crash-" + time + "-" + timestamp + ".txt";  
            if (Environment.getExternalStorageState().equals(  
                    Environment.MEDIA_MOUNTED)) {  
                //��ȡ�ļ����·��  
//                String path = Environment.getExternalStorageDirectory()  
//                        + "/crashinfo/";  
                String path = "/data/data/com.canbot.operator"  
                        + "/crashinfo/";  
                //�����ļ��к��ļ�  
                File dir = new File(path);  
                if (!dir.exists()) {  
                    dir.mkdirs();  
                }  
                //���������  
                FileOutputStream fos = new FileOutputStream(path + fileName);  
                //���ļ���д������  
                fos.write(sb.toString().getBytes());  
                fos.close();  
            }  
            return fileName;  
        } catch (Exception e) {  
            Log.e(TAG, "an error occured while writing file...", e);  
        }  
        return null;  
    }  
}  
