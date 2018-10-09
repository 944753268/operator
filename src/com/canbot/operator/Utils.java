/**
 * 
 */
package com.canbot.operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.json.JSONArray;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * describe �� 2016��9��28�� ����3:39:54 Utils.java
 * 
 * @author peng_modify
 */
public class Utils {

	private static final long FILE_SIZE = 1024 * 1024 * 1;
	private static final int PRINT_DEBUG = 0;
	// private static final int PRINT_DEBUG = 1;

	// ��ȡ�����Ĳ�Ʒ��
	public static String get_machine_number() {

		return android.os.Build.MODEL;
	}

	@SuppressLint("SdCardPath")
	public static void print_data(String str, String path) {
		File file = new File(path);
		long fileSize = getFileSize(file);
		// Log.d("text", "�ļ���С---" + fileSize);

		if (PRINT_DEBUG == 0) {

			if (fileSize > FILE_SIZE) {
				// �ļ�����1M��ʱ��ɾ�����ļ�
				file.delete();
				// Log.d("text", "-----delete file-----" + str);
			}
			// �ڶ�������������˵�Ƿ���append��ʽ�������
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter(file, true));
				bw.write(str);
				bw.write("\n");

				bw.flush();
				Log.i("text", str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// System.out.println(str);
		}
	}

	public static void mk_all_dir() {
		String cmd = null;
		// ������Ŀ¼
		String HOME_PATH = "/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/";
		cmd = " cd /sdcard/ai/ && mkdir -p ai05res/a1/res/audio/suspendVoiceLib";
		ExecCmd(cmd);
		// ������Ŀ¼
		cmd = "cd " + HOME_PATH + " && mkdir -p system minglingdiaoyong  news xingzou teshutianqi jieri jieqi bodyInduction ";
		ExecCmd(cmd);

		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/ " + "&&  mkdir -p wakeUp";
		ExecCmd(cmd);

		// cmd ="cd "+ "/sdcard/ai/ai05res/a1/res/audio/wakeUp/ " +"&& mkdir -p
		// jieri jieqi ";
		// ExecCmd(cmd);

		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/wakeUp/" + "&&  mkdir -p jieri";
		ExecCmd(cmd);
		// �������������Ľ���Ŀ¼
		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/wakeUp/jieri/ "
				+ "&&  mkdir -p chongyangjie chuxi  duanwujie ertongjie funvjie fuqinjie"
				+ " guoqingjie jiandangjie jianjunjie laodongjie longtaitou labajie muqinjie pinganye qingnianjie qingrenjie"
				+ " qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie";
		ExecCmd(cmd);

		// ������ͨ���͵Ľ���Ŀ¼
		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/jieri/ "
				+ " &&  mkdir -p chongyangjie chuxi  duanwujie ertongjie funvjie fuqinjie"
				+ " guoqingjie jiandangjie jianjunjie laodongjie longtaitou muqinjie pinganye qingnianjie qingrenjie"
				+ " qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie ";
		ExecCmd(cmd);

		// ������ͨ���͵Ľ���Ŀ¼
		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/jieqi/ "
				+ " && mkdir -p bailu chunfen chushu dahan dashu daxue dongzhi guyu"
				+ " hanlu jingzhe lichun lidong liqiu lixia mangzhong qiufen "
				+ "shuangjiang xiaohan xiaoman xiaoshu xiaoxue xiazhi yushui";
		ExecCmd(cmd);
		// �����������

		cmd = "cd " + HOME_PATH + "teshutianqi/ && mkdir -p baoyu bingbao hanchao jiangxue shachenbao taifeng wumai ";
		ExecCmd(cmd);
		// ���������ļ���
		cmd = "cd " + HOME_PATH + "news/ " + "&&  mkdir -p xinwenku zhongdianshijian";
		ExecCmd(cmd);
		// ���������ļ���
		cmd = "cd " + HOME_PATH + "xingzou/ " + "&&  mkdir -p daogouxingzou daolanxingzou";
		ExecCmd(cmd);

		// ������ͨ����������Ŀ¼
		/*
		 * cmd="cd "+ HOME_PATH +"daiji/ && mkdir -p putong  teshushijian";
		 * ExecCmd(cmd); //����������� ���գ�����Ŀ¼ cmd="cd "+ HOME_PATH +
		 * "daiji/teshushijian/ && mkdir -p jieqi jieri teshutianqi";
		 * ExecCmd(cmd); //����������� //��������������Ŀ¼
		 * 
		 * 
		 * //����������������ո�Ŀ¼ cmd ="cd "+ HOME_PATH +"huanxingfanku/"+
		 * "  && mkdir -p tssj/jieri"; ExecCmd(cmd);
		 * 
		 * //�������ѽ���������Ŀ¼ cmd ="cd "+ HOME_PATH +
		 * "huanxingfanku/tssj/jieri/ && mkdir -p chongyangjie chuxi duanwujie ertongjie funvjie fuqinjie"
		 * +
		 * " guoqingjie jiandangjie jianjunjie laodongjie longtaitou muqinjie pinganye qingnianjie"
		 * +
		 * " qingrenjie qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie "
		 * ; ExecCmd(cmd);
		 * 
		 */

	}

	/*
	 * public static void mk_all_dir(){ String cmd=null; //������Ŀ¼ String
	 * HOME_PATH=
	 * "/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/u05_yunduan_yuyinku/";
	 * cmd=
	 * " cd /sdcard/ai/ && mkdir -p ai05res/a1/res/audio/suspendVoiceLib/u05_yunduan_yuyinku"
	 * ; ExecCmd(cmd); //��������Ŀ¼ cmd="cd "+ HOME_PATH +
	 * " && mkdir -p daiji  huanxingfanku " +
	 * " minglingdiaoyong  tuisong xingzou "; ExecCmd(cmd); //������ͨ����������Ŀ¼ cmd=
	 * "cd "+ HOME_PATH +"daiji/ && mkdir -p putong  teshushijian";
	 * ExecCmd(cmd); //����������� ���գ�����Ŀ¼ cmd="cd "+ HOME_PATH +
	 * "daiji/teshushijian/ && mkdir -p jieqi jieri teshutianqi"; ExecCmd(cmd);
	 * //����������� cmd ="cd "+ HOME_PATH +
	 * "daiji/teshushijian/jieqi/ && mkdir -p bailu chunfen chushu dahan dashu daxue dongzhi guyu"
	 * + " hanlu jingzhe lichun lidong liqiu lixia mangzhong qiufen " +
	 * "shuangjiang xiaohan xiaoman xiaoshu xiaoxue xiazhi yushui";
	 * ExecCmd(cmd); //����������� cmd ="cd "+ HOME_PATH +
	 * "daiji/teshushijian/jieri/ && mkdir -p chongyangjie chuxi  duanwujie ertongjie funvjie fuqinjie"
	 * +
	 * " guoqingjie jiandangjie jianjunjie laodongjie longtaitou muqinjie pinganye qingnianjie qingrenjie"
	 * +
	 * " qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie "
	 * ; ExecCmd(cmd); //��������������Ŀ¼ cmd ="cd "+ HOME_PATH +
	 * "daiji/teshushijian/teshutianqi/ && mkdir -p baoyu bingbao hanchao jiangxue shachenbao taifeng wumai "
	 * ; ExecCmd(cmd);
	 * 
	 * //����������������ո�Ŀ¼ cmd ="cd "+ HOME_PATH +"huanxingfanku/"+
	 * "  && mkdir -p tssj/jieri"; ExecCmd(cmd); //�������ѽ���������Ŀ¼ cmd ="cd "+
	 * HOME_PATH +
	 * "huanxingfanku/tssj/jieri/ && mkdir -p chongyangjie chuxi duanwujie ertongjie funvjie fuqinjie"
	 * +
	 * " guoqingjie jiandangjie jianjunjie laodongjie longtaitou muqinjie pinganye qingnianjie"
	 * +
	 * " qingrenjie qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie "
	 * ; ExecCmd(cmd);
	 * 
	 * //���������ļ��� cmd ="cd "+ HOME_PATH +"tuisong/" +
	 * "&&  mkdir -p xinwenku zhongdianshijian"; ExecCmd(cmd); //���������ļ��� cmd =
	 * "cd "+ HOME_PATH +"xingzou/" +"&&  mkdir -p daogouxingzou daolanxingzou";
	 * ExecCmd(cmd);
	 * 
	 * }
	 */
	// // �ж��ļ����Ƿ����
	// public static boolean DirExists() {
	// String HOME_PATH="/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/jieri";
	// File file=new File(HOME_PATH);
	//
	// if (file.exists()) {
	// return true;
	// } else {
	// return false;
	// }
	//
	// }

	@SuppressWarnings("unused")
	public static void ExecCmd(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {

		} finally {
			try {
				if (os != null) {
					os.close();
					os = null;
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ��ȡ�ļ���С
	 * 
	 * @param file
	 *            �ļ�
	 * @return �ļ���С
	 */
	public static long getFileSize(File file) {
		if (!isFileExists(file))
			return 0;
		return file.length();
	}

	/**
	 * �ж��ļ��Ƿ����
	 *
	 * @param file
	 *            �ļ�
	 * @return {@code true}: ����<br>
	 *         {@code false}: ������
	 */
	public static boolean isFileExists(File file) {
		return file != null && file.exists();
	}

	/**
	 * 
	 * describe :
	 * 
	 * @param context
	 * @return 2016��12��14�� ����5:15:49
	 * @author peng_modify
	 */
	public static boolean isOnline(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;

	}

	/**
	 * describe :
	 * 
	 * @param netflag
	 *            2016��12��14�� ����5:20:10
	 * @author peng_modify
	 */
	public static void write_networe_status(String netflag) {
		BufferedWriter bw = null;

		try {
			// �����ļ�·���������������
			bw = new BufferedWriter(new FileWriter("/data/canbot/tmp/network.status"));
			// ������д���ļ���
			bw.write(netflag);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// �ر���
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					bw = null;
				}
			}
		}
	}

	/**
	 * 
	 * describe :
	 * 
	 * @param context
	 * @return 2017��1��5�� ����8:56:37
	 * @author peng_modify
	 */
	public static int get_current_apk_version(Context context) {
		int result = 0;

		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			result = pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * describe :
	 * 
	 * @return 2017��1��5�� ����8:57:48
	 * @author peng_modify
	 */

	public static OperatorInfo get_server_apk_version() {
		OperatorInfo operatorInfo = null;
		
		
		FileInputStream fis = null;
	
		StringBuffer stringBuffer = new StringBuffer();
		Charset charset = Charset.forName("Utf-8");

		try {
			fis = new FileInputStream("/sdcard/canbot/download/allapp");
			
	
			byte[] b= new byte[1024];
			int length = 0;
			while ((length = fis.read(b)) > 0) {
				stringBuffer.append(new String(b, 0, length, charset));
			}
		
			
			
			//��ֹ�ļ�Ϊ��
			debugger("�ӷ���˽��յ�version�ַ���:"+stringBuffer.toString());
			
			if(stringBuffer.toString().length()<1)
				return operatorInfo;
			
			JSONArray jsonArray = new JSONArray(stringBuffer.toString().replace("\\", ""));
			System.out.println(">>>>>>>>>string����ת��json");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject job = jsonArray.getJSONObject(i);// ����jsonarray����,��ÿһ������ת��json����
				if(job.getString("name").equals("Operator")){
					operatorInfo=new OperatorInfo();
					operatorInfo.setVersion(job.getInt("version"));
					operatorInfo.setLength(job.getInt("length"));
					operatorInfo.setUrl(job.getString("url"));
				}
				
			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// �ر��ֽ�����������ܵ�
				fis.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
//		 System.out.println("****"+result);
		return operatorInfo;
	}

	/**
	 * 
	 * describe :
	 * 
	 * @param command
	 * @return 2016��12��14�� ����3:14:01
	 * @author peng_modify
	 */
	public static String getProp(String command) {
		Process process;
		BufferedReader input = null;
		String resultProp = null;
		/*******************************************************
		 *** Java�������linux����getprop��ȡproduct_id,position***
		 *******************************************************/
		try {
			process = Runtime.getRuntime().exec(command);
			InputStreamReader irpid = new InputStreamReader(process.getInputStream());
			input = new BufferedReader(irpid);
			resultProp = input.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultProp;
	}

	/**
	 * 
	 * describe :�õ�����״̬������
	 * 
	 * @return 2016��12��14�� ����5:52:00
	 * @author peng_modify
	 */
	public static String get_network_status() {
		BufferedReader br = null;
		String line = null;

		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream("/data/canbot/tmp/network.status"));
			br = new BufferedReader(isr);
			line = br.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}

		return line;
	}
	
	  public static int  getFilelength(String filepath) {
		  int filelength=0;
		  File file =new File("/sdcard/canbot/download/Operator.apk");
		  
	        if (file.exists() && file.isFile()) {
	       
	            filelength=(int) file.length();
	        }
	        return filelength;
	    }
	  
	 public static void debugger(String str){
		 
		 Log.e("TAG", str);
	 }

}
