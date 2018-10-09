package com.canbot.network;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.canbot.operator.Utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ${ping} on 2018/9/21.
 */
public class FTPUtils {
	public static FTPUtils ftpUtils;

	private FTPUtils() {

	}

	public static FTPUtils getInstance() {
		synchronized (FTPUtils.class) {
			if (ftpUtils == null) {
				ftpUtils = new FTPUtils();
			}
		}
		return ftpUtils;
	}

	public void ftpDown(String url, int port, String username, String password, String filePath, String FTP_file,
			String SD_file) {
		FileOutputStream buffOut = null;
		FTPClient ftpClient = new FTPClient();
		try {

			ftpClient.connect(url, 21);
			boolean loginResult = ftpClient.login(username, password);
			int returnCode = ftpClient.getReplyCode();
			if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
																			// ftpClient.enterLocalPassiveMode();
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

				buffOut = new FileOutputStream(filePath + SD_file);
				InputStream input = ftpClient.retrieveFileStream(FTP_file);
				byte[] b = new byte[1024];
				int length = 0;
				while ((length = input.read(b)) != -1) {
					buffOut.write(b, 0, length);
				}
				buffOut.flush();
				buffOut.close();
				input.close();
				
				Thread.sleep(1000);
				Utils.debugger("apk下载完成，准备创建downloadApk.ok文件");
				// 创建下载完成的文件
				File file = new File("/sdcard/canbot/download/downloadApk.ok");
				if (!file.exists()) {
					file.createNewFile();
				}
				ftpClient.logout();

			} else {
			Log.i("TAG", "ftp 登录失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
	}
}
