package com.mayikt.util;

import com.mayikt.config.FtpConfig;

import java.io.File;
import java.util.List;

public class FtpFileUtils {

	/** 主机地址 */
	private static String   fileHost;

	/** 主机密码 */
	private static String   fileUser;

	/** 主机密码 */
	private static String   filePwd;

	/** 端口 */
	private static Integer  filePort;

	/** 连接超时时间 */
	private static Integer  timeOut;

	/** ftp配置 */
	private static FtpConfig ftpConfig;

	public static FtpConfig getFtpConfig() {
		return ftpConfig;
	}

	static {
		fileHost = "172.16.0.176";

		fileUser = "testuser";

		filePwd = "liu729754701";

		filePort = 22;

		timeOut = 1000;

		ftpConfig = new FtpConfig(fileHost, fileUser, filePwd, filePort, timeOut);

	}

	public static List<String> listFiles(String path, String suffix, FtpConfig config) {
		return FtpUtil.listFiles(path, suffix, config);
	}

	public static File downLoadFileFromPath(String path, String fileName, FtpConfig config) {
		return FtpUtil.downloadFile(path + fileName, config);
	}
	public static void uploadFileToFtp(File file,FtpConfig config,String uploadPath){
		FtpUtil.uploadFile(file,config,uploadPath);
	}

}
