package com.mayikt.util;

/**
 * @description:
 * @author: liuwq
 * @date: 2019/8/20 0020 上午 9:40
 * @version: V1.0
 */

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.mayikt.config.FtpConfig;
import com.mayikt.exception.ResourceException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class FtpUtil {

    public static void uploadFile(File file, FtpConfig ftp, String uploadPath) {
        String fileName = file.getName();

        String newFileName = fileName;

        InputStream fis = null;
        SessionChannelUtil scu = null;
        ChannelSftp channelSftp = null;
        try {
            scu = new SessionChannelUtil();
            fis = new FileInputStream(file);
            Session session = scu.sessionConnect(ftp.getFileUser(), ftp.getFileHost(), ftp.getFilePort().intValue(), ftp.getFilePwd(), ftp.getTimeOut().intValue());
            channelSftp = scu.channelSftpConnect(session, ftp.getTimeOut().intValue());
            try {
                Vector content = channelSftp.ls(uploadPath);
                if (content == null) {
                    channelSftp.mkdir(uploadPath);
                }

            } catch (SftpException e) {
                channelSftp.mkdir(uploadPath);
            }

//            channelSftp.put(fis, uploadPath + newFileName + ".tmp", 0);
            channelSftp.put(fis, uploadPath + newFileName, 0);
            try {
                scu.closeSession();
                fis.close();
            } catch (Exception e) {
                log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
                throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】同时检查文件路径【" + uploadPath + "是否有权限】", e);
            }
        } catch (Exception e) {
        } finally {
            if (scu != null)
                try {
                    scu.closeSession();
                    fis.close();
                } catch (Exception e) {
                    log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
                    throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】同时检查文件路径【" + uploadPath + "是否有权限】", e);
                }
        }
    }

    public static void uploadFileByInputStream(String fileName, FtpConfig ftp, InputStream fis, String uploadPath) {
        String newFileName = fileName;

        SessionChannelUtil scu = null;
        ChannelSftp channelSftp = null;
        try {
            scu = new SessionChannelUtil();
            Session session = scu.sessionConnect(ftp.getFileUser(), ftp.getFileHost(), ftp.getFilePort().intValue(), ftp.getFilePwd(), ftp.getTimeOut().intValue());
            channelSftp = scu.channelSftpConnect(session, ftp.getTimeOut().intValue());
            try {
                Vector content = channelSftp.ls(uploadPath);
                if (content == null)
                    channelSftp.mkdir(uploadPath);

            } catch (SftpException e) {
                channelSftp.mkdir(uploadPath);
            }

            channelSftp.put(fis, uploadPath + newFileName, 0);
            try {
                scu.closeSession();
            } catch (Exception e) {
                log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
                throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】同时检查文件路径【" + uploadPath + "是否有权限】", e);
            }
        } catch (Exception e) {
        } finally {
            if (scu != null)
                try {
                    scu.closeSession();
                } catch (Exception e) {
                    log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
                    throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】同时检查文件路径【" + uploadPath + "是否有权限】", e);
                }
        }
    }

    public static File downloadFile(String filePath, FtpConfig ftp) {
        SessionChannelUtil scu = new SessionChannelUtil();
        Session session = null;

        String fileName = UUID.randomUUID() + filePath.substring(filePath.lastIndexOf("."), filePath.length());
        try {
            session = scu.sessionConnect(ftp.getFileUser(), ftp.getFileHost(), ftp.getFilePort().intValue(), ftp.getFilePwd(), ftp.getTimeOut().intValue());
            ChannelSftp channelSftp = scu.channelSftpConnect(session, ftp.getTimeOut().intValue());
            String dir = System.getProperty("java.io.tmpdir");
//            String dir = "E:/test2";
            File dirFile = new File(dir);
            if (!(dirFile.exists())) {
                dirFile.mkdirs();
            }

            fileName = dir + File.separator + fileName;
            channelSftp.get(filePath, fileName);
            try {
                scu.closeSession();
            } catch (Exception e) {
                log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
                throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
            }
        } catch (SftpException e) {
        } catch (Exception e) {
            throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
        } finally {
            try {
                scu.closeSession();
            } catch (Exception e) {
                log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
                throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
            }
        }
        return new File(fileName);
    }

    public static List<String> listFiles(String filePath, String suffix, FtpConfig ftp) {
        SessionChannelUtil scu = new SessionChannelUtil();
        Session session = null;
        Vector files = null;
        List fileList = new ArrayList();
        try {
            session = scu.sessionConnect(ftp.getFileUser(), ftp.getFileHost(), ftp.getFilePort().intValue(), ftp.getFilePwd(), ftp.getTimeOut().intValue());
            ChannelSftp channelSftp = scu.channelSftpConnect(session, ftp.getTimeOut().intValue());
            files = channelSftp.ls(filePath);
            for (Iterator localIterator = files.iterator(); localIterator.hasNext(); ) {
                Object object = localIterator.next();
                String curStr = object.toString();
                curStr = curStr.substring(curStr.lastIndexOf(" ") + 1);

                if (curStr.contains(suffix)) {
                    fileList.add(curStr);
                }
            }
        } catch (Exception e) {
            log.error("连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】同时检查文件路径【" + filePath + "是否存在】", e);
        } finally {
            try {
                scu.closeSession();
            } catch (Exception e) {
                log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
            }
        }

        return fileList;
    }

    public static void deleteFile(String filePath, FtpConfig ftp) {
        SessionChannelUtil scu = new SessionChannelUtil();
        Session session = null;
        try {
            session = scu.sessionConnect(ftp.getFileUser(), ftp.getFileHost(), ftp.getFilePort().intValue(), ftp.getFilePwd(), ftp.getTimeOut().intValue());
            ChannelSftp channelSftp = scu.channelSftpConnect(session, ftp.getTimeOut().intValue());
            channelSftp.rm(filePath);
            try {
                scu.closeSession();
            } catch (Exception e) {
                log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
                throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
            }
        } catch (Exception e) {
            throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
        } finally {
            try {
                scu.closeSession();
            } catch (Exception e) {
                log.error("关闭远程连接出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
                throw new ResourceException("F007", "连接远程服务器出错！请检查主机配置【host:" + ftp.getFileHost() + ",用户名:" + ftp.getFileUser() + ",端口:" + ftp.getFilePort() + "】", e);
            }
        }
    }

}