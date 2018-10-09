/**
 * 
 */
package com.canbot.operator;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.collections.map.StaticBucketMap;

import com.canbot.network.FTPUtils;
import com.canbot.network.HttpUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;

public class OperatorService extends Service {

	private static int APKVERSION = 9;

	private static String PACKAGE_NAME = "com.canbot.operator";
	private static String PATH_Dir_CLIENT = "/sdcard/canbot/bin/DirClient.apk";
	private static String PATH_ADVERT_CLIENT = "/sdcard/canbot/bin/AdvertClient.apk";
	private static String PATH_FAULT_CLIENT = "/sdcard/canbot/bin/FaultReport.apk";
	private static String PATH_STATUS_CLIENT = "/sdcard/canbot/bin/StatusClient.apk";
	private static String PATH_ARCHIVE_CLIENT = "/sdcard/canbot/bin/ArchiveClient.apk";
	private static String PATH_NEWS_CLIENT = "/sdcard/canbot/bin/NewsClient.apk";

	private static final String PATH_LOG = "/sdcard/canbot/log/OperatorService.log";

	private static String PATH_DROPBEAR = "/system/xbin/dropbear";
	private static String PATH_OPENVPN = "/system/bin/openvpn";
	private static String PATH_CHECKSTATUS = "/sdcard/canbot/bin/checkstatus";

	private static String PATH_COLLECTLOG = "/data/canbot/bin/collectlog";
	private static String PATH_LOG_CLIENT = "/sdcard/canbot/bin/logClient.apk";

	private static String PATH_READSQLITE = "/sdcard/canbot/bin/readsqlite";
	private static String PATH_TIMEAXIS_CLIENT = "/sdcard/canbot/bin/TimeaxisClient.apk";

	public static final String VERSIONURL = "http://canbot.net.cn:8083/api/v2/version/Operator";

	public static final String DOWNDIR = "/sdcard/canbot/download/";

	public static final String USER = "liguolin";
	public static final String PASSW = "Liguolin123";
	public static final String OPERATOR_APK_PATH = "/sdcard/canbot/download/Operator.apk";

	/******************************/
	OperatorService operatorService;

	@Override
	public void onCreate() {
		/**
		 * 
		 * 建目录 bin tmp var advert
		 * 
		 */

		/*********** 建立U05s的版本 ****************/
		if (Utils.get_machine_number().equals("rk3288")) {
			// if(!Utils.DirExists())
			/**
			 * 
			 * 修复网络系统bug
			 * 
			 */
			ExecCmd(" ip rule add from all lookup main pref 9999 ");
			Utils.mk_all_dir();

		}

		Utils.print_data("建立所有文件", PATH_LOG);
		ExecCmd("mkdir -p /data/canbot && mkdir -p /sdcard/canbot/bin && mkdir -p /data/canbot/tmp && mkdir -p /data/canbot/var && mkdir -p /data/canbot/bin ");
		ExecCmd("chmod 777 /sdcard/canbot/bin/");// 初始化bin目录
		ExecCmd("chmod 777 /data/canbot/bin/");// 初始化bin目录
		ExecCmd("chmod -R 777 /data/canbot");
		ExecCmd("mkdir -p /sdcard/canbot/advert");
		ExecCmd("chmod 777 /sdcard/canbot/advert");
		ExecCmd("mkdir -p /sdcard/canbot/download");
		ExecCmd("chmod 777 /sdcard/canbot/download");
		ExecCmd("mkdir -p /sdcard/canbot/log");
		ExecCmd("chmod 777 /sdcard/canbot/log");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/******************************/
		operatorService = this;

		Utils.print_data("启动服务**********************************", PATH_LOG);
		Utils.print_data("没有开机启动版本**********", PATH_LOG);
		Utils.print_data("当前的版本是" + APKVERSION + "**********************************", PATH_LOG);

		/**
		 *
		 * 判断网络线程
		 *
		 */
		/*************************************************/
		/********************** 开启判断连网线程 ********************/
		/*************************************************/
		ExecCmd("touch /data/canbot/tmp/network.status");
		ExecCmd("chown root:root /data/canbot/tmp/network.status");
		ExecCmd("chmod 666 /data/canbot/tmp/network.status");
		NetStatus netStatus = new NetStatus();
		netStatus.start();

		/**
		 *
		 * 启动目录服务器
		 *
		 */
		// new Thread(new Runnable() {
		new Thread() {
			public void run() {

				/********* 判断是否连网 ************/
				String online_flag = null;

				while (true) {
					try {
						Thread.sleep(15 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					online_flag = Utils.get_network_status();
					Utils.print_data("online_flag = " + online_flag, PATH_LOG);
					if (online_flag.equals("1"))
						break;
					Utils.print_data("断网状态中.............", PATH_LOG);
				}

				/********************/
				/********************/
				/****** UPDATE ********/
				/********************/
				/********************/
				/********************/
				int currentApkVersion = 0;
				int serverApkVersion = 0;
				String VER = Utils.getProp("getprop persist.operator_version");
				Utils.print_data("apk VERSION=" + VER, PATH_LOG);
				if (VER.length() < 1) {
					currentApkVersion = 0;
					ExecCmd("setprop persist.operator_version " + APKVERSION);
				} else
					currentApkVersion = Integer.parseInt(VER);

				/************************ 检查版本号 *********************/
				ExecCmd("rm -f /sdcard/canbot/download/*");
				// ExecCmd("cd /sdcard/canbot/download && busybox wget
				// http://120.76.133.10/rpp_v4/getAllVersion");
				// ExecCmd("cd /sdcard/canbot/download && busybox wget
				// http://139.196.46.141:8080/rpp_v4/allapp && touch
				// downloadVersion.ok");
				// 下载版本号到文件
				new Thread() {
					public void run() {
						try {
							HttpUtils.getInstance().downLoadFromUrl(DOWNDIR, "allapp", VERSIONURL);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					};
				}.start();

				int download_version_times = 0;

				OperatorInfo operatorInfo = null;
				while (true) {
					download_version_times++;
					try {
						if (new File("/sdcard/canbot/download/downloadVersion.ok").exists() == true) {
							operatorInfo = Utils.get_server_apk_version();
							if (operatorInfo != null)
								serverApkVersion = operatorInfo.getVersion();
							break;
						}
						if (download_version_times > 18)
							break;
						sleep(10 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				/******************** 判断当前版本号 *******************************/

				Utils.print_data(
						"currentApkVersion===" + currentApkVersion + "，serverApkVersion====" + serverApkVersion,
						PATH_LOG);
				/** &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& **/
				/** &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& **/
				/** &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& **/
				/** &&&&&&&&&&&&&&关键判断&&&&&&&&&&&&&&& **/
				/** &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& **/
				/** &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& **/
				/** &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& **/
				Utils.debugger("server_version:"+serverApkVersion);
				
				if ((currentApkVersion < serverApkVersion) && (currentApkVersion != 0) && operatorInfo != null) {
					/*************************** 下载版本 *************************/
					// ExecCmd("cd /sdcard/canbot/download && busybox wget
					// http://139.196.46.141:8080/rpp_v4/app/Operator.apk");
					// ExecCmd("cd /sdcard/canbot/download && busybox wget
					// http://139.196.46.141:8080/rpp_v4/app/Operator.apk &&
					// touch downloadApk.ok");
					/********************** 下载服务端apk **************/
					// 截取字符串
					final String ftpfile = operatorInfo.getUrl().substring(operatorInfo.getUrl().indexOf("u"));
					Utils.debugger("ftp 服务器下载地址：" + ftpfile);

					new Thread() {
						public void run() {
							FTPUtils.getInstance().ftpDown("canbot.net.cn", 21, USER, PASSW, DOWNDIR, ftpfile,
									"Operator.apk");
						};
					}.start();

					int download_Apk_times = 0;
					while (true) {
						download_Apk_times++;
						try {
							sleep(10 * 1000);
							if (new File("/sdcard/canbot/download/downloadApk.ok").exists() == true)
								break;
							if (download_Apk_times > 18)
								break;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// 获取本地文件的长度
					int operatorLength = Utils.getFilelength(OPERATOR_APK_PATH);
					// 当本地文件的长度小于服务器文件的长度的时候，更新
					if (operatorInfo.getLength() != 0 && operatorInfo.getLength() > operatorLength) {

						Utils.print_data("文件下载不完全", PATH_LOG);
						
						return;

					}
					/********* 开始下载文件 ******************/

					if (new File("/sdcard/canbot/download/downloadApk.ok").exists() == true) {
						ExecCmd("mount -o remount /system");

						/*************************** 重新安装 *************************/
						Utils.print_data("000001", PATH_LOG);
						ExecCmd("/system/bin/sh /system/bin/clean_all.sh");
						Utils.print_data("0000011111", PATH_LOG);
						try {
							sleep(15 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ExecCmd("/system/bin/sh /system/bin/clean_all.sh");
						Utils.print_data("000002", PATH_LOG);
						try {
							sleep(15 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
						/********
						 * @@@@@@@@@@更新、安装、启动operator@@@@@@@@@@@@
						 *********/
						/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
						ExecCmd("setprop persist.operator_version " + serverApkVersion);
						Utils.print_data("0000033333 restart restart ...", PATH_LOG);
						// 2017 11 22
						// ExecCmd("pm uninstall com.canbot.operator;sleep
						// 10;echo 'uninstall ok.' "
						// + "&& pm install
						// /sdcard/canbot/download/Operator.apk;sleep 20;echo
						// 'new install ok.' "
						// + "&& am startservice -n
						// com.canbot.operator/.OperatorService &");
						ExecCmd("pm uninstall com.canbot.operator && sleep 1 && echo 'uninstall ok.'  > /sdcard/canbot/download/update.log "
								+ "&& pm install /sdcard/canbot/download/Operator.apk && sleep 1 && echo 'new install ok.' >> /sdcard/canbot/download/update.log "
								+ "&& am startservice -n com.canbot.operator/.OperatorService &");

						// try {
						// sleep(30 * 1000);
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
						// ExecCmd("pm install
						// /sdcard/canbot/download/Operator.apk");
						// Utils.print_data("install &&&&&&&&&&&&&", PATH_LOG);
						// try {
						// sleep(20 * 1000);
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }

						// /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
						// /******** @@@@@@@@@@apk重启@@@@@@@@@@@@ *********/
						// /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
						// ExecCmd("setprop persist.operator_version " +
						// serverApkVersion);
						// Utils.print_data("00000444 restart restart ...",
						// PATH_LOG);
						// try {
						// sleep(10 * 1000);
						// } catch (InterruptedException e) {
						// e.printStackTrace();
						// }
						// Intent service = new Intent(OperatorService.this,
						// OperatorService.class);
						// Utils.print_data("000004", PATH_LOG);
						// startService(service);
						// Utils.print_data("fireware update ok, restart...",
						// PATH_LOG);

					} else {
						Utils.print_data("Operator.apk 下载失败 !!!!!!!!!!!!", PATH_LOG);
					}

				} else {

					/********************/
					/********************/
					/****** START ********/
					/********************/
					/********************/
					/********************/
					/****** ##########以下所有共用的context########### ******/
					Context context;
					try {
						context = operatorService.createPackageContext(PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
						/**
						 * 
						 * 目录服务器
						 * 
						 */
						ExecCmd("mount -o remount /system");
						/*****************************************************************************************
						 * #########目录服务器客户端dirclient#########
						 *****************************************************************************************/
						final boolean dir_is_exist = fileIsExist(PATH_Dir_CLIENT);
						Utils.print_data("dir_is_exist======" + dir_is_exist, PATH_LOG);
						if (!dir_is_exist) {
							/************************ 产生客户端文件 *********************/
							CopyAssets(context, PATH_Dir_CLIENT, "DirClient.apk");
							// ExecCmd("chown root:root
							// /sdcard/canbot/bin/DirClient.apk");
							ExecCmd("chmod 666 /sdcard/canbot/bin/DirClient.apk");
							ExecCmd("mount -o remount /system");
							Utils.print_data("产生了目录客户端文件================", PATH_LOG);
							// ExecCmd("cp /sdcard/canbot/bin/DirClient.apk
							// /system/app/");
							// ExecCmd("chmod 644 /system/app/DirClient.apk");
						}

						/**
						 * 
						 * openvpn服务器
						 * 
						 */
						/*****************************************************************************************
						 * #########通信服务器客户端 openvpn,dropbear产生与安装#########
						 *****************************************************************************************/
						final boolean openvpn_is_exist = fileIsExist(PATH_OPENVPN);
						if (!openvpn_is_exist) {
							/************************** 拷贝openvpn并安装 ********************/
							ExecCmd("mkdir /data/canbot/openvpn");
							ExecCmd("chmod 777 /data/canbot/openvpn");

							if (Utils.get_machine_number().equals("rk3288")) {
								CopyAssets(context, "/data/canbot/openvpn/openvpn", "openvpn/openvpn-u05");

							} else {
								CopyAssets(context, "/data/canbot/openvpn/openvpn", "openvpn/openvpn-u03");
							}
							CopyAssets(context, "/data/canbot/openvpn/client.conf", "openvpn/client.conf");
							CopyAssets(context, "/data/canbot/openvpn/client.crt", "openvpn/client.crt");
							CopyAssets(context, "/data/canbot/openvpn/client.key", "openvpn/client.key");
							CopyAssets(context, "/data/canbot/openvpn/ca.crt", "openvpn/ca.crt");
							// ExecCmd("mount -o remount /system");
							ExecCmd("chown root:root /data/canbot/openvpn/*;"
									+ "chmod 755 /data/canbot/openvpn/openvpn;" + "chmod 666  /data/canbot/openvpn/c*");
							Utils.print_data("准备拷贝文件*******************", PATH_LOG);
							ExecCmd("cp /data/canbot/openvpn/openvpn /system/bin/openvpn;chmod 755 /system/bin/openvpn");
							Utils.print_data("准备拷贝文件完成*******************", PATH_LOG);
							ExecCmd("mkdir /etc/openvpn && " + "cp /data/canbot/openvpn/client.conf /etc/openvpn/;"
									+ "cp /data/canbot/openvpn/c* /etc/openvpn/");
							ExecCmd("chmod -R 666 /etc/openvpn");
							Utils.print_data("openvpn文件拷贝成功************************", PATH_LOG);
						}
						/************************** 拷贝dropbear并安装 ********************/
						final boolean dropbear_is_exist = fileIsExist(PATH_DROPBEAR);
						if (!dropbear_is_exist) {
							ExecCmd("mkdir /data/canbot/dropbear");
							ExecCmd("chmod 777 /data/canbot/dropbear");
							CopyAssets(context, "/data/canbot/dropbear/dropbear", "dropbear/dropbear");
							CopyAssets(context, "/data/canbot/dropbear/dropbearkey", "dropbear/dropbearkey");
							CopyAssets(context, "/data/canbot/dropbear/dropbearconvert", "dropbear/dropbearconvert");
							CopyAssets(context, "/data/canbot/dropbear/scp", "dropbear/scp");
							CopyAssets(context, "/data/canbot/dropbear/ssh", "dropbear/ssh");
							ExecCmd("chown root:root /data/canbot/dropbear/*;" + "chmod 755 /data/canbot/dropbear/*");
							ExecCmd("cp /data/canbot/dropbear/* /system/xbin/ &&"
									+ "chmod 755 /system/xbin/dropbear* /system/xbin/scp /system/xbin/ssh");
							/**************************
							 * openvpn产出关联的ifconfig、route命令
							 ****************************************/
							ExecCmd("mkdir /system/xbin/bb && " + "cd /system/xbin/bb && "
									+ "ln -s /system/bin/toolbox ifconfig && "
									+ "ln -s /system/bin/toolbox route && cd");
							/**************************
							 * dropb ear产出root账号、密钥
							 ****************************************/
							ExecCmd("mkdir /data/dropbear && " + "mkdir /data/dropbear/.ssh && "
									+ "cd /data/dropbear && " + "dropbearkey -t rsa -f dropbear_rsa_host_key && "
									+ "dropbearkey -t dss -f dropbear_dss_host_key && " + "cd");
							ExecCmd("echo root:x:0:0::/root:/system/bin/sh > /etc/passwd && "
									+ "echo root::14531:0:99999:7::: > /etc/shadow && "
									+ "echo root:x:0: > /etc/group && " + "echo root:!:: > /etc/gshadow && "
									+ "echo /system/bin/sh > /etc/shells && "
									+ "echo PATH=\"/sbin:/system/xbin:/system/bin\" > /etc/profile && "
									+ "echo export PATH >> /etc/profile");
							ExecCmd("chmod 666 /etc/passwd /etc/shadow /etc/group /etc/gshadow /etc/shells /etc/profile");
							Utils.print_data("dropbear文件拷贝成功************************", PATH_LOG);

						}

						/**
						 * 
						 * 通信服务器
						 * 
						 */
						/*****************************************************************************************
						 * #########通信服务器客户端statusclient#########
						 *****************************************************************************************/
						/************************
						 * 产生checkstatus c++文件
						 *********************/
						final boolean checkstatus_is_exist = fileIsExist(PATH_CHECKSTATUS);
						Utils.print_data("checkstatus_is_exist================" + checkstatus_is_exist, PATH_LOG);
						if (!checkstatus_is_exist) {
							/************************
							 * 产生C++文件
							 *********************/
							CopyAssets(context, PATH_CHECKSTATUS, "checkstatus");

							// ExecCmd("chown root:root
							// /sdcard/canbot/bin/checkstatus");
							ExecCmd("chmod 755 /sdcard/canbot/bin/checkstatus");
							Utils.print_data("产生checkstatus客户端******************", PATH_LOG);
						}
						/************************ 产生客户端文件 *********************/
						final boolean status_is_exist = fileIsExist(PATH_STATUS_CLIENT);
						if (!status_is_exist) {
							CopyAssets(context, PATH_STATUS_CLIENT, "StatusClient.apk");
							// ExecCmd("chown root:root
							// /sdcard/canbot/bin/StatusClient.apk");
							ExecCmd("chmod 666 /sdcard/canbot/bin/StatusClient.apk");
							// ExecCmd("cp /sdcard/canbot/bin/StatusClient.apk
							// /system/app/");
							// ExecCmd("chmod 644
							// /system/app/StatusClient.apk");
							Utils.print_data("产生StatusClient.apk客户端******************", PATH_LOG);
						}

						/**
						 * 
						 * 广告客户端
						 * 
						 */
						final boolean advert_is_exist = fileIsExist(PATH_ADVERT_CLIENT);
						if (!advert_is_exist) {
							CopyAssets(context, PATH_ADVERT_CLIENT, "AdvertClient.apk");
							// ExecCmd("chown root:root
							// /sdcard/canbot/bin/AdvertClient.apk");
							ExecCmd("chmod 666 /sdcard/canbot/bin/AdvertClient.apk");
							Utils.print_data("产生AdvertClient.apk客户端******************", PATH_LOG);
						}

						/**
						 * 
						 * 日志服务器
						 * 
						 */
						/*****************************************************************************************
						 * #########日志服务器客户端logclient#########
						 *****************************************************************************************/
						/************************
						 * 产生collectlog c++文件
						 *********************/
						final boolean collectlog_is_exist = fileIsExist(PATH_COLLECTLOG);
						Utils.print_data("collectlog_is_exist================" + collectlog_is_exist, PATH_LOG);
						if (!collectlog_is_exist) {
							/************************
							 * 产生C++文件
							 *********************/
							CopyAssets(context, PATH_COLLECTLOG, "collectlog");
							// ExecCmd("chown root:root
							// /data/canbot/bin/collectlog");
							ExecCmd("chmod 755 /data/canbot/bin/collectlog");
							Utils.print_data("产生collectlog c++文件******************", PATH_LOG);
						}

						/************************ 产生客户端文件 *********************/
						final boolean log_is_exist = fileIsExist(PATH_LOG_CLIENT);
						if (!log_is_exist) {
							CopyAssets(context, PATH_LOG_CLIENT, "logClient.apk");
							// ExecCmd("chown root:root
							// /sdcard/canbot/bin/logClient.apk");
							ExecCmd("chmod 666 /sdcard/canbot/bin/logClient.apk");
							Utils.print_data("产生logClient.apk客户端******************", PATH_LOG);
						}
						/***************** 产生fault客户端文件 ***************************/
						final boolean fault_is_exist = fileIsExist(PATH_FAULT_CLIENT);
						if (!fault_is_exist) {
							CopyAssets(context, PATH_FAULT_CLIENT, "FaultReport.apk");
							ExecCmd("chmod 666 /sdcard/canbot/bin/FaultReport.apk");
							Utils.print_data("产生Faultclient.apk客户端******************", PATH_LOG);
						}

						/***************** 产生archive客户端文件 ***************************/
//						final boolean archive_is_exist = fileIsExist(PATH_ARCHIVE_CLIENT);
//						if (!archive_is_exist) {
//							CopyAssets(context, PATH_ARCHIVE_CLIENT, "ArchiveClient.apk");
//							ExecCmd("chmod 666 /sdcard/canbot/bin/ArchiveClient.apk");
//							Utils.print_data("产生Archiveclient.apk客户端******************", PATH_LOG);
//						}
						/***************** 产生news客户端文件 ***************************/
						final boolean newsPush_is_exist = fileIsExist(PATH_NEWS_CLIENT);
						if (!newsPush_is_exist) {
							CopyAssets(context, PATH_NEWS_CLIENT, "NewsClient.apk");
							ExecCmd("chmod 666 /sdcard/canbot/bin/NewsClient.apk");
							Utils.print_data("产生Newsclient.apk客户端******************", PATH_LOG);
						}

						// 大优安装时间轴客户端

						if (Utils.get_machine_number().equals("rk3288")) {
							final boolean readsqlite_is_exist = fileIsExist(PATH_READSQLITE);
							Utils.print_data("readsqlite_is_exist================" + readsqlite_is_exist, PATH_LOG);
							if (!readsqlite_is_exist) {
								/************************
								 * 产生C++文件
								 *********************/
								CopyAssets(context, PATH_READSQLITE, "readsqlite");
								Utils.print_data(">>>>>>>>>>>1--readsqlite", PATH_LOG);
								ExecCmd("chmod 755 /sdcard/canbot/bin/readsqlite");
								System.out.println("产生readsqlitec++文件******************");
							}

							/************************ 产生客户端文件 *********************/
							final boolean timeaxis_is_exist = fileIsExist(PATH_TIMEAXIS_CLIENT);
							if (!timeaxis_is_exist) {
								CopyAssets(context, PATH_TIMEAXIS_CLIENT, "TimeaxisClient.apk");
								Utils.print_data(">>>>>>>>>>>1--timeaxisclient", PATH_LOG);
								ExecCmd("chmod 666 /sdcard/canbot/bin/TimeaxisClient.apk");
								System.out.println("产生TimeaxisClient.apk客户端******************");
							}
						}

						/************************ 产生clean_all文件 *********************/
						ExecCmd("mount -o remount /system");
						final boolean cleanall_is_exist = fileIsExist("/sdcard/canbot/bin/clean_all.sh");
						Utils.print_data("cleanall_is_exist================" + cleanall_is_exist, PATH_LOG);
						/*******************************/
						/***** 2017-12-27 ping_modify ******************/
						/************************
						 * 产生C++文件
						 *********************/
						CopyAssets(context, "/sdcard/canbot/bin/clean_all.sh", "clean_all.sh");

						ExecCmd("chmod 755 /sdcard/canbot/bin/clean_all.sh");

						ExecCmd("cp /sdcard/canbot/bin/clean_all.sh /system/bin/clean_all.sh");
						ExecCmd("chmod 755 /system/bin/clean_all.sh");
						Utils.print_data("产生clean_all 文件******************", PATH_LOG);

					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					/********************/
					/********************/
					/****** END ********/
					/********************/
					/********************/
					/********************/

					/************************ 安装目录客户端 *********************/
					boolean dirInstalled = isAppInstalled("com.canbot.dirclient");
					boolean dir_is_exist_2 = fileIsExist(PATH_Dir_CLIENT);
					Utils.print_data("dirInstalled================" + dirInstalled + "dir_isexist ============ "
							+ dir_is_exist_2, PATH_LOG);
					if (!dirInstalled && dir_is_exist_2) {
						SilentInstall silentInstallHelper = new SilentInstall();
						Utils.print_data("进入安装中*******************************", PATH_LOG);
						silentInstallHelper.install(PATH_Dir_CLIENT);
						Utils.print_data("初始化**PATH_Dir_CLIENT****************************", PATH_LOG);
						boolean resultDir = silentInstallHelper.install(PATH_Dir_CLIENT);
						if (resultDir) {
							Utils.print_data(">>>>>>>>>>>dirclient安装成功", PATH_LOG);
						} else {
							Utils.print_data(">>>>>>>>>>>dirclient安装失败", PATH_LOG);
						}
						Utils.print_data("dirclient安装完成===================", PATH_LOG);
					}
					Utils.print_data("启动目录服务器***************************", PATH_LOG);
					Utils.print_data("am startservice -n com.canbot.dirclient/.DirClient &", PATH_LOG);
					ExecCmd("am startservice -n com.canbot.dirclient/.DirClient &");

					boolean statusInstalled = isAppInstalled("com.canbot.statusclient");
					boolean status_is_exist_2 = fileIsExist(PATH_STATUS_CLIENT);
					Utils.print_data(">>>>>>>>>>>status_installed = " + statusInstalled + "----------status_isexist = "
							+ status_is_exist_2, PATH_LOG);
					if (!statusInstalled && status_is_exist_2) {
						SilentInstall silentInstallHelper1 = new SilentInstall();
						silentInstallHelper1.install(PATH_STATUS_CLIENT);
						boolean resultStatus = silentInstallHelper1.install(PATH_STATUS_CLIENT);
						if (resultStatus) {
							Utils.print_data("statusclient客户端安装成功了*************************", PATH_LOG);
						} else {

							Utils.print_data("statusclient客户端安装失败了*************************", PATH_LOG);
						}
					}
					/************** 开启StatusClient.apk任务 ********************/
					Utils.print_data("am startservice -n com.canbot.statusclient/.StatusClient &", PATH_LOG);
					ExecCmd("am startservice -n com.canbot.statusclient/.StatusClient &");

					/************************ 安装并开启广告客户端 *********************/
					boolean advertInstalled = isAppInstalled("com.canbot.advertclient");
					boolean advert_is_exist_2 = fileIsExist(PATH_ADVERT_CLIENT);
					Utils.print_data(">>>>>>>>>>>status_installed = " + statusInstalled + "----------status_isexist = "
							+ status_is_exist_2, PATH_LOG);
					if (!advertInstalled && advert_is_exist_2) {
						SilentInstall silentInstallHelper1 = new SilentInstall();
						silentInstallHelper1.install(PATH_ADVERT_CLIENT);
						boolean resultAdvert = silentInstallHelper1.install(PATH_ADVERT_CLIENT);
						if (resultAdvert) {
							Utils.print_data("advertclient客户端安装成功了*************************", PATH_LOG);
						} else {
							Utils.print_data("advertclient客户端安装失败了*************************", PATH_LOG);
						}
					}
					Utils.print_data("am startservice -n com.canbot.advertclient/.AdvertClient &", PATH_LOG);
					ExecCmd("am startservice -n com.canbot.advertclient/.AdvertClient &");

					/* 安装logClient客户端 */
					boolean logInstalled = isAppInstalled("com.canbot.logclient");
					boolean log_is_exist_2 = fileIsExist(PATH_LOG_CLIENT);
					Utils.print_data(
							">>>>>>>>>>>log_installed = " + logInstalled + "----------log_isexist = " + log_is_exist_2,
							PATH_LOG);
					if (!logInstalled && log_is_exist_2) {
						SilentInstall silentInstallHelper2 = new SilentInstall();
						silentInstallHelper2.install(PATH_LOG_CLIENT);
						boolean resultLog = silentInstallHelper2.install(PATH_LOG_CLIENT);
						if (resultLog) {
							Utils.print_data("Logclient客户端安装成功了*************************", PATH_LOG);
						} else {
							Utils.print_data("logclient客户端安装失败了*************************", PATH_LOG);
						}
					}
					/************** 开启logClient.apk任务 ********************/
					Utils.print_data("am startservice -n com.canbot.logclient/.LogClient &", PATH_LOG);
					ExecCmd("am startservice -n com.canbot.logclient/.LogClient &");

					/* 安装archiveClient客户端 */
//					boolean archiveInstalled = isAppInstalled("com.canbot.archiveclient");
//					boolean archive_is_exist_2 = fileIsExist(PATH_ARCHIVE_CLIENT);
//					Utils.print_data(">>>>>>>>>>>archive_installed = " + archiveInstalled
//							+ "----------archive_isexist = " + archive_is_exist_2, PATH_LOG);
//					if (!archiveInstalled && archive_is_exist_2) {
//						SilentInstall silentInstallHelper2 = new SilentInstall();
//						silentInstallHelper2.install(PATH_ARCHIVE_CLIENT);
//						boolean resultArchive = silentInstallHelper2.install(PATH_ARCHIVE_CLIENT);
//						if (resultArchive) {
//							Utils.print_data("archiveclient客户端安装成功了*************************", PATH_LOG);
//						} else {
//							Utils.print_data("archiveclient客户端安装失败了*************************", PATH_LOG);
//						}
//					}
//					/************** 开启archiveClient.apk任务 ********************/
//					Utils.print_data("am startservice -n com.canbot.archiveclient/.ArchiveClient &", PATH_LOG);
//					ExecCmd("am startservice -n com.canbot.archiveclient/.ArchiveClient &");

					/* 安装newsClient客户端 */
					boolean newsInstalled = isAppInstalled("com.canbot.newsclient");
					boolean news_is_exist_2 = fileIsExist(PATH_NEWS_CLIENT);
					Utils.print_data(">>>>>>>>>>>news_installed = " + newsInstalled + "----------news_isexist = "
							+ news_is_exist_2, PATH_LOG);
					if (!newsInstalled && news_is_exist_2) {
						SilentInstall silentInstallHelper2 = new SilentInstall();
						silentInstallHelper2.install(PATH_NEWS_CLIENT);
						boolean resultNews = silentInstallHelper2.install(PATH_NEWS_CLIENT);
						if (resultNews) {
							Utils.print_data("newsclient客户端安装成功了*************************", PATH_LOG);
						} else {
							Utils.print_data("newsclient客户端安装失败了*************************", PATH_LOG);
						}
					}
					/************** 开启newsClient.apk任务 ********************/
					Utils.print_data("am startservice -n com.canbot.newsclient/.PushClient &", PATH_LOG);
					ExecCmd("am startservice -n com.canbot.newsclient/.PushClient &");

					/*********************** 安装faultreprot客户端 **************************/
					/*************************** 当机器型号是小优时才安装 ********/
					if (Utils.get_machine_number().equals("rk3188")) {
						boolean faultInstalled = isAppInstalled("com.canbot.faultreport");
						boolean fault_is_exist_2 = fileIsExist(PATH_FAULT_CLIENT);
						Utils.print_data(">>>>>>>>>>>fault_installed = " + faultInstalled + "----------fault_isexist = "
								+ fault_is_exist_2, PATH_LOG);
						if (!faultInstalled && fault_is_exist_2) {
							SilentInstall silentInstallHelper1 = new SilentInstall();
							silentInstallHelper1.install(PATH_FAULT_CLIENT);
							boolean resultfault = silentInstallHelper1.install(PATH_FAULT_CLIENT);
							if (resultfault) {
								Utils.print_data("advertclient客户端安装成功了*************************", PATH_LOG);
							} else {
								Utils.print_data("advertclient客户端安装失败了*************************", PATH_LOG);
							}
						}
					}

					// /* 安装TimeaxisClient客户端 */
					// boolean timeaxisInstalled =
					// isAppInstalled("com.canbot.timeaxisclient");
					// boolean timeaxis_is_exist_2 =
					// fileIsExist(PATH_TIMEAXIS_CLIENT);
					// Utils.print_data(">>>>>>>>>>>timeaxis_installed = " +
					// timeaxisInstalled
					// + "----------timeaxis_isexist = " + timeaxis_is_exist_2,
					// PATH);
					// if (!timeaxisInstalled && timeaxis_is_exist_2) {
					// SilentInstall silentInstallHelper3 = new SilentInstall();
					// silentInstallHelper3.install(PATH_TIMEAXIS_CLIENT);
					// boolean resultTimeaxis =
					// silentInstallHelper3.install(PATH_TIMEAXIS_CLIENT);
					// if (resultTimeaxis) {
					// Utils.print_data(">>>>>>>>>>>timeaxisclient安装成功", PATH);
					// System.out.println("timeaxisclient客户端安装成功了*************************");
					// } else {
					// Utils.print_data(">>>>>>>>>>>timeaxisclient安装失败", PATH);
					// System.out.println("timeaxisclient客户端安装失败了*************************");
					// }
					// }
					// /************** 开启TimeaxisClient.apk任务
					// ********************/
					// System.out.println("am startservice -n
					// com.canbot.timeaxisclient/.TimeaxisClient &");
					// ExecCmd("am startservice -n
					// com.canbot.timeaxisclient/.TimeaxisClient &");

				}
			}
		}.start();

		return super.onStartCommand(intent, flags, startId);
	}

	/*****************************************************************/
	/******************** 判断连网线程，改写network.status文件 **********************/
	/*****************************************************************/
	class NetStatus extends Thread {
		@Override
		public void run() {

			boolean network_is_normal = false;
			int network_oldflag = -1;
			int process_number = 0;

			/******************* 判断第一次 *********************/
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			network_is_normal = Utils.isOnline(getApplicationContext());
			Utils.print_data("打印是否连网===============" + network_is_normal, PATH_LOG);
			if (network_is_normal == true) {
				/***************** 网络连接 *****************/
				Utils.write_networe_status("1");
				Utils.print_data("写进文件1", PATH_LOG);
				network_oldflag = 1;
			} else {
				/***************** 网络端开 *****************/
				Utils.write_networe_status("0");
				Utils.print_data("写进文件0", PATH_LOG);
				network_oldflag = 0;
			}

			/******************* 循环判断 *********************/
			while (true) {
				try {
					Thread.sleep(3 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				network_is_normal = Utils.isOnline(getApplicationContext());
				Utils.print_data("打印是否连网===============" + network_is_normal, PATH_LOG);
				if (network_is_normal == true) {
					/***************** 网络连接 *****************/
					if (network_oldflag == 0) {
						Utils.write_networe_status("1");
						Utils.print_data("写进文件1", PATH_LOG);
						network_oldflag = 1;
					}
				} else {
					/***************** 网络端开 *****************/
					if (network_oldflag == 1) {
						Utils.write_networe_status("0");
						Utils.print_data("写进文件0", PATH_LOG);
						network_oldflag = 0;
					}
				}
			}

		}
	}

	/**
	 * describe : 2016年8月15日 下午3:13:05
	 * 
	 * @author peng_modify
	 */
	private void ExecCmd(String cmd) {
		Process process = null;
		DataOutputStream dataOutputStream = null;
		try {
			process = Runtime.getRuntime().exec("su");
			dataOutputStream = new DataOutputStream(process.getOutputStream());
			dataOutputStream.writeBytes(cmd + "\n");
			dataOutputStream.writeBytes("exit\n");
			dataOutputStream.flush();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				process.destroy();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 复制asset文件到指定目录
	 * 
	 * @param targetName
	 *            asset下的路径
	 * @param toPath
	 *            保存路径
	 */
	public void CopyAssets(Context context, String toPath, String targetName) {
		InputStream input = null;
		OutputStream output = null;
		try {
			File file = new File(toPath);
			if (!file.exists()) {

				file.createNewFile();
			}
			input = context.getAssets().open(targetName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int readLength = 0;
			while ((readLength = input.read(buffer)) != -1) {
				output.write(buffer, 0, readLength);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null || output != null) {
					input.close();
					output.close();
					input = null;
					output = null;
				}
			} catch (Exception e2) {
			}
		}
	}

	/**
	 * describe : 判断应用是否已经安装 2016年8月15日 下午7:23:58
	 * 
	 * @author peng_modify
	 * @return
	 */
	private boolean isAppInstalled(String uri) {
		PackageManager pManager = getPackageManager();
		boolean installed = false;

		try {
			pManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			installed = true;

		} catch (NameNotFoundException e) {
			installed = false;
		}

		return installed;

	}

	/**
	 * describe : 判断文件是否存在 2016年8月15日 下午7:06:41
	 * 
	 * @author peng_modify
	 * @return
	 */
	private boolean fileIsExist(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists() || file.length() < 0) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		stopSelf();
		System.exit(0);
	}

}
