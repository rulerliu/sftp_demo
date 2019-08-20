package com.mayikt.util;

import com.mayikt.config.FtpConfig;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <br>
 * 标题: <br>
 * 描述: <br>
 * 公司: www.tydic.com<br>
 *
 * @author linyujia
 * @time 2018/6/13 19:11
 */
public class DownloadAndParsingFileUtils {
    private static final Logger log = LoggerFactory.getLogger(DownloadAndParsingFileUtils.class);

    public static List<String> parsing(List<String> fileNames, String pattern, String path, FtpConfig ftpConfig) {
        if (CollectionUtils.isEmpty(fileNames)) {
            return null;
        }

        List<String> contents = new ArrayList<String>(fileNames.size());

        for (String fileName : fileNames) {
            if (Pattern.matches(pattern, fileName)) {
                // 下载文件并读取内容
                try {
                    File file = downloadFile(path, fileName, ftpConfig);

                    String content = getFileContent(file, ftpConfig);
                    if (StringUtils.isNotBlank(content)) {
                        contents.add(content);
                    }
                } catch (Exception e) {
                    log.error("下载并读取文件：" + fileName + "内容失败：" + e.getMessage());
                }
            }
        }

        return contents;
    }

    public static List<Map<String, String>> parsingAndReturnFileName(List<String> fileNames, String pattern, String path,
                                                                     FtpConfig ftpConfig, String encoding) {
        return parsingAndReturnFileName(fileNames, pattern, path, ftpConfig, encoding, false);
    }

    public static List<Map<String, String>> parsingAndReturnFileName(List<String> fileNames, String pattern, String path,
                                                                     FtpConfig ftpConfig, String encoding, boolean includeEmptyFile) {
        if (CollectionUtils.isEmpty(fileNames)) {
            return null;
        }
        List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
        for (String fileName : fileNames) {
            if (Pattern.matches(pattern, fileName)) {
                // 下载文件并读取内容
                try {
                    File file = downloadFile(path, fileName, ftpConfig);

                    String content = getFileContent(file, encoding);
                    if (!StringUtils.isBlank(content) || includeEmptyFile) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("content", content.replace("\r", ""));
                        map.put("fileName", fileName);
                        map.put("tempFilePath", file.getPath());
                        mapList.add(map);
                    }
                } catch (Exception e) {
                    log.error("下载并读取文件：" + fileName + "内容失败：" + e.getMessage());
                }
            }
        }
        return mapList;
    }

    /**
     *
     * @param path:  /download/   fileName:
     * @param fileName: testuser_0001.txt
     * @param ftpConfig:
     * @return C:\Users\ADMINI~1\AppData\Local\Temp\cd16c3cf-df04-4a44-a2dc-d20502c87abf.txt
     */
    public static File downloadFile(String path, String fileName, FtpConfig ftpConfig) {
        log.info("下载文件:{}", fileName);
        // 下载文件到C:\Users\ADMINI~1\AppData\Local\Temp\cd16c3cf-df04-4a44-a2dc-d20502c87abf.txt路径
        File file = FtpFileUtils.downLoadFileFromPath(path, fileName, ftpConfig);
        log.info("下载完毕，文件路径=" + file.getPath());
        return file;
    }


    public static List<String> parsing(List<String> fileNames, String pattern, String path, String bakdir, FtpConfig ftpConfig) {
        if (CollectionUtils.isEmpty(fileNames)) {
            return null;
        }

        List<String> contents = new ArrayList<String>(fileNames.size());

        for (String fileName : fileNames) {
            if (Pattern.matches(pattern, fileName)) {
                // 下载文件并读取内容
                try {
                    String content = getFileContent(path, fileName, bakdir, ftpConfig);
                    if (StringUtils.isNotBlank(content)) {
                        contents.add(content);
                    }
                } catch (Exception e) {
                    log.error("下载并读取文件：" + fileName + "内容失败：" + e.getMessage());
                }
            }
        }

        return contents;
    }

    private static String getFileContent(File file, FtpConfig ftpConfig) {
        return getFileContent(file, "utf-8");
    }

    /**
     * <br>
     * 适用场景:下载并读取文件内容	<br>
     * 调用方式:	<br>
     * 业务逻辑说明<br>
     *
     * @param file
     * @return java.lang.String
     * @author linyujia
     * @time 18:58 2018/6/13
     */
    private static String getFileContent(File file, String encoding) {
        return FileUtils.loadBigFile(file.getPath(), encoding);
    }

    private static String getFileContent(String path, String fileName, String bakdir, FtpConfig ftpConfig) {
        log.info("下载文件:{}", fileName);
        // 下载文件
        File file = FtpFileUtils.downLoadFileFromPath(path, fileName, ftpConfig);
        log.info("下载完毕，文件路径=" + file.getPath());
        String str = FileUtils.loadBigFile(file.getPath(), "utf-8");
        //备份下载的文件
        try {
            File bakdirFile = new File(bakdir);
            if (!bakdirFile.exists()) {
                bakdirFile.mkdirs();
            }
            File newFile = new File(bakdirFile, fileName);
            FileCopyUtils.copy(file, newFile);
        } catch (IOException e) {
            log.error("文件从临时目录拷贝到目标目录出错" + e.getMessage(), e);
        }

        return str;
    }
}