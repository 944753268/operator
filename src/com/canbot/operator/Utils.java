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
 * describe ： 2016年9月28日 下午3:39:54 Utils.java
 * 
 * @author peng_modify
 */
public class Utils {

	private static final long FILE_SIZE = 1024 * 1024 * 1;
	private static final int PRINT_DEBUG = 0;
	// private static final int PRINT_DEBUG = 1;

	// 获取机器的产品号
	public static String get_machine_number() {

		return android.os.Build.MODEL;
	}

	@SuppressLint("SdCardPath")
	public static void print_data(String str, String path) {
		File file = new File(path);
		long fileSize = getFileSize(file);
		// Log.d("text", "文件大小---" + fileSize);

		if (PRINT_DEBUG == 0) {

			if (fileSize > FILE_SIZE) {
				// 文件大于1M的时候删掉该文件
				file.delete();
				// Log.d("text", "-----delete file-----" + str);
			}
			// 第二个参数意义是说是否以append方式添加内容
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
		// 建立根目录
		String HOME_PATH = "/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/";
		cmd = " cd /sdcard/ai/ && mkdir -p ai05res/a1/res/audio/suspendVoiceLib";
		ExecCmd(cmd);
		// 创建根目录
		cmd = "cd " + HOME_PATH + " && mkdir -p system minglingdiaoyong  news xingzou teshutianqi jieri jieqi bodyInduction ";
		ExecCmd(cmd);

		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/ " + "&&  mkdir -p wakeUp";
		ExecCmd(cmd);

		// cmd ="cd "+ "/sdcard/ai/ai05res/a1/res/audio/wakeUp/ " +"&& mkdir -p
		// jieri jieqi ";
		// ExecCmd(cmd);

		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/wakeUp/" + "&&  mkdir -p jieri";
		ExecCmd(cmd);
		// 建立特殊天气的节日目录
		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/wakeUp/jieri/ "
				+ "&&  mkdir -p chongyangjie chuxi  duanwujie ertongjie funvjie fuqinjie"
				+ " guoqingjie jiandangjie jianjunjie laodongjie longtaitou labajie muqinjie pinganye qingnianjie qingrenjie"
				+ " qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie";
		ExecCmd(cmd);

		// 建立普通推送的节日目录
		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/jieri/ "
				+ " &&  mkdir -p chongyangjie chuxi  duanwujie ertongjie funvjie fuqinjie"
				+ " guoqingjie jiandangjie jianjunjie laodongjie longtaitou muqinjie pinganye qingnianjie qingrenjie"
				+ " qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie ";
		ExecCmd(cmd);

		// 简历普通推送的节气目录
		cmd = "cd " + "/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/jieqi/ "
				+ " && mkdir -p bailu chunfen chushu dahan dashu daxue dongzhi guyu"
				+ " hanlu jingzhe lichun lidong liqiu lixia mangzhong qiufen "
				+ "shuangjiang xiaohan xiaoman xiaoshu xiaoxue xiazhi yushui";
		ExecCmd(cmd);
		// 建立特殊节日

		cmd = "cd " + HOME_PATH + "teshutianqi/ && mkdir -p baoyu bingbao hanchao jiangxue shachenbao taifeng wumai ";
		ExecCmd(cmd);
		// 建立推送文件夹
		cmd = "cd " + HOME_PATH + "news/ " + "&&  mkdir -p xinwenku zhongdianshijian";
		ExecCmd(cmd);
		// 建立行走文件夹
		cmd = "cd " + HOME_PATH + "xingzou/ " + "&&  mkdir -p daogouxingzou daolanxingzou";
		ExecCmd(cmd);

		// 建立普通和特殊语音目录
		/*
		 * cmd="cd "+ HOME_PATH +"daiji/ && mkdir -p putong  teshushijian";
		 * ExecCmd(cmd); //建立特殊节气 节日，天气目录 cmd="cd "+ HOME_PATH +
		 * "daiji/teshushijian/ && mkdir -p jieqi jieri teshutianqi";
		 * ExecCmd(cmd); //建立特殊节气 //建立特殊天气子目录
		 * 
		 * 
		 * //建立唤醒是特殊节日根目录 cmd ="cd "+ HOME_PATH +"huanxingfanku/"+
		 * "  && mkdir -p tssj/jieri"; ExecCmd(cmd);
		 * 
		 * //建立唤醒节日所有子目录 cmd ="cd "+ HOME_PATH +
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
	 * public static void mk_all_dir(){ String cmd=null; //建立根目录 String
	 * HOME_PATH=
	 * "/sdcard/ai/ai05res/a1/res/audio/suspendVoiceLib/u05_yunduan_yuyinku/";
	 * cmd=
	 * " cd /sdcard/ai/ && mkdir -p ai05res/a1/res/audio/suspendVoiceLib/u05_yunduan_yuyinku"
	 * ; ExecCmd(cmd); //建立待机目录 cmd="cd "+ HOME_PATH +
	 * " && mkdir -p daiji  huanxingfanku " +
	 * " minglingdiaoyong  tuisong xingzou "; ExecCmd(cmd); //建立普通和特殊语音目录 cmd=
	 * "cd "+ HOME_PATH +"daiji/ && mkdir -p putong  teshushijian";
	 * ExecCmd(cmd); //建立特殊节气 节日，天气目录 cmd="cd "+ HOME_PATH +
	 * "daiji/teshushijian/ && mkdir -p jieqi jieri teshutianqi"; ExecCmd(cmd);
	 * //建立特殊节气 cmd ="cd "+ HOME_PATH +
	 * "daiji/teshushijian/jieqi/ && mkdir -p bailu chunfen chushu dahan dashu daxue dongzhi guyu"
	 * + " hanlu jingzhe lichun lidong liqiu lixia mangzhong qiufen " +
	 * "shuangjiang xiaohan xiaoman xiaoshu xiaoxue xiazhi yushui";
	 * ExecCmd(cmd); //建立特殊节日 cmd ="cd "+ HOME_PATH +
	 * "daiji/teshushijian/jieri/ && mkdir -p chongyangjie chuxi  duanwujie ertongjie funvjie fuqinjie"
	 * +
	 * " guoqingjie jiandangjie jianjunjie laodongjie longtaitou muqinjie pinganye qingnianjie qingrenjie"
	 * +
	 * " qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie "
	 * ; ExecCmd(cmd); //建立特殊天气子目录 cmd ="cd "+ HOME_PATH +
	 * "daiji/teshushijian/teshutianqi/ && mkdir -p baoyu bingbao hanchao jiangxue shachenbao taifeng wumai "
	 * ; ExecCmd(cmd);
	 * 
	 * //建立唤醒是特殊节日根目录 cmd ="cd "+ HOME_PATH +"huanxingfanku/"+
	 * "  && mkdir -p tssj/jieri"; ExecCmd(cmd); //建立唤醒节日所有子目录 cmd ="cd "+
	 * HOME_PATH +
	 * "huanxingfanku/tssj/jieri/ && mkdir -p chongyangjie chuxi duanwujie ertongjie funvjie fuqinjie"
	 * +
	 * " guoqingjie jiandangjie jianjunjie laodongjie longtaitou muqinjie pinganye qingnianjie"
	 * +
	 * " qingrenjie qingmingjie qixijie shengdanjie wuyanri yuandanjie yuanxiaojie yurenjie zhishujie zhongqiujie "
	 * ; ExecCmd(cmd);
	 * 
	 * //建立推送文件夹 cmd ="cd "+ HOME_PATH +"tuisong/" +
	 * "&&  mkdir -p xinwenku zhongdianshijian"; ExecCmd(cmd); //建立行走文件夹 cmd =
	 * "cd "+ HOME_PATH +"xingzou/" +"&&  mkdir -p daogouxingzou daolanxingzou";
	 * ExecCmd(cmd);
	 * 
	 * }
	 */
	// // 判断文件夹是否存在
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
	 * 获取文件大小
	 * 
	 * @param file
	 *            文件
	 * @return 文件大小
	 */
	public static long getFileSize(File file) {
		if (!isFileExists(file))
			return 0;
		return file.length();
	}

	/**
	 * 判断文件是否存在
	 *
	 * @param file
	 *            文件
	 * @return {@code true}: 存在<br>
	 *         {@code false}: 不存在
	 */
	public static boolean isFileExists(File file) {
		return file != null && file.exists();
	}

	/**
	 * 
	 * describe :
	 * 
	 * @param context
	 * @return 2016年12月14日 下午5:15:49
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
	 *            2016年12月14日 下午5:20:10
	 * @author peng_modify
	 */
	public static void write_networe_status(String netflag) {
		BufferedWriter bw = null;

		try {
			// 根据文件路径创建缓冲输出流
			bw = new BufferedWriter(new FileWriter("/data/canbot/tmp/network.status"));
			// 将内容写入文件中
			bw.write(netflag);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
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
	 * @return 2017年1月5日 下午8:56:37
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
	 * @return 2017年1月5日 下午8:57:48
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
		
			
			
			//防止文件为空
			debugger("从服务端接收的version字符串:"+stringBuffer.toString());
			
			if(stringBuffer.toString().length()<1)
				return operatorInfo;
			
			JSONArray jsonArray = new JSONArray(stringBuffer.toString().replace("\\", ""));
			System.out.println(">>>>>>>>>string结束转换json");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject job = jsonArray.getJSONObject(i);// 遍历jsonarray数组,把每一个对象转成json对象
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
				// 关闭字节流输入输出管道
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
	 * @return 2016年12月14日 下午3:14:01
	 * @author peng_modify
	 */
	public static String getProp(String command) {
		Process process;
		BufferedReader input = null;
		String resultProp = null;
		/*******************************************************
		 *** Java代码调用linux命令getprop获取product_id,position***
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
	 * describe :得到网络状态的数据
	 * 
	 * @return 2016年12月14日 下午5:52:00
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
