package com.mayikt;

import com.mayikt.config.FtpConfig;
import com.mayikt.util.DownloadAndParsingFileUtils;
import com.mayikt.util.FtpFileUtils;
import com.mayikt.util.FtpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: liuwq
 * @date: 2019/8/20 0020 上午 11:41
 * @version: V1.0
 */
@Slf4j
public class Test {

    public static void main(String[] args) {
        log.info("大地影院用户同步定时任务开始");
        // 从download读取带有前缀dadi_users的文件
        String prefix = "testuser_";
        String downloadPath = "/download/";
        String uploadPath = "/upload/";
        FtpConfig ftpConfig = FtpFileUtils.getFtpConfig();
        List<String> fileNames = FtpFileUtils.listFiles(downloadPath, prefix, ftpConfig);
        if (CollectionUtils.isEmpty(fileNames)) {
            log.info("没有大地影院用户数据同步文件");
            return;
        }
        for (String fileName : fileNames) {
            System.out.println(fileName);
        }

        String pattern = prefix + ".*";
        List<Map<String, String>> contentAndFileNames = DownloadAndParsingFileUtils.parsingAndReturnFileName(fileNames,
                pattern, downloadPath, ftpConfig, "utf-8", true);
        if (CollectionUtils.isEmpty(contentAndFileNames)) {
            log.info("没有大地影院用户数据同步文件");
            return;
        }

        for (int i = 0; i < contentAndFileNames.size(); i++) {
            log.info("大地影院用户数据同步定时任务第{}批次文件处理开始", i + 1);
            Map<String, String> map = contentAndFileNames.get(i);
            String fileName = map.get("fileName");
            String content = map.get("content");
            String tempFilePath = map.get("tempFilePath");
            // 空文件处理
            if (StringUtils.isBlank(content)) {
                moveFile2Back(downloadPath, fileName, tempFilePath);
                continue;
            }

            // 封装处理数据
//            Map<String, Object> resultMap = parseOneFile(conetent);
//            Integer count = (Integer) resultMap.get("count");
            String[] arr = content.split("\n");
            System.out.println(arr.length);

            moveFile2Back(downloadPath, fileName, tempFilePath);
        }

    }

    private static void moveFile2Back(String downloadPath, String fileName, String tempFilePath) {
        // 将文件上传到back目录
//        File tempFile = FtpFileUtils.downLoadFileFromPath(downloadPath, fileName, FtpFileUtils.getFtpConfig());
        File tempFile = new File(tempFilePath);
        File newFile = new File(tempFile.getParent(), fileName);
        // 把临时文件重命名
        tempFile.renameTo(newFile);
        FtpFileUtils.uploadFileToFtp(newFile, FtpFileUtils.getFtpConfig(),downloadPath + "/" + "back" + "/");
//        tempFile.delete();
        newFile.delete();
        FtpUtil.deleteFile(downloadPath + "/" + fileName, FtpFileUtils.getFtpConfig());
    }

}
