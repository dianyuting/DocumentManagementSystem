package edu.wtbu.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Data
@Slf4j
@Service
public class FTPUtil {
    @Value("${ftp_ip}")
    private String ftpIp;
    @Value("${ftp_port}")
    private Integer ftpPort;
    @Value("${ftp_user}")
    private String ftpUser;
    @Value("${ftp_pass}")
    private String ftpPass;

    private FTPClient ftpClient;

    private boolean connectServer(String ip, int port, String user, String pwd) {
        ftpClient = new FTPClient();
        Boolean isSuccess = false;
        try {
            //ftp默认为ISO-8859-1，设置UTF-8需在建立连接之前
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, pwd);
            //设置文件传输模式为二进制，保证传输内容不变
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //设置为被动模式
            ftpClient.enterLocalPassiveMode();
        } catch (IOException e) {
            log.error("连接ftp服务器失败");
        }
        return isSuccess;
    }

    //创建文件夹
    public boolean createFolder(String remotePath) throws IOException {
        boolean folder = true;
        if (connectServer(ftpIp, ftpPort, ftpUser, ftpPass)) {
            try {
                //创建文件夹
                ftpClient.makeDirectory(remotePath);
            } catch (IOException e) {
                log.error("创建文件夹异常", e);
                folder = false;
            } finally {
                //登出
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
        return folder;
    }

    //上传多个文件
//    public  boolean uploadFiles(String remotePath, List<File> fileList) throws IOException {
//        boolean upload = true;
//        FileInputStream fileInputStream = null;
//        if(connectServer(ftpIp,ftpPort,ftpUser,ftpPass)){
//            try {
//                //切换当前工作目录
//                ftpClient.changeWorkingDirectory(remotePath);
//                ftpClient.setBufferSize(1024);
//                //遍历文件并上传文件，上传后的文件名，输入流
//                for (File fileItem:fileList){
//                    fileInputStream = new FileInputStream(fileItem);
//                    ftpClient.storeFile(fileItem.getName(),fileInputStream);
//                }
//            }catch (IOException e){
//                log.error("上传文件异常",e);
//                upload = false;
//            }finally {
//                fileInputStream.close();
//                //登出
//                ftpClient.logout();
//                ftpClient.disconnect();
//            }
//        }
//        return upload;
//    }
    public boolean uploadFiles(String remotePath, MultipartFile[] fileList) throws IOException {
        boolean upload = true;
        if (connectServer(ftpIp, ftpPort, ftpUser, ftpPass)) {
            try {
                //切换当前工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                //遍历文件并上传文件，上传后的文件名，输入流
                for (MultipartFile fileItem : fileList) {
                    ftpClient.storeFile(fileItem.getOriginalFilename(), fileItem.getInputStream());
                }
            } catch (IOException e) {
                log.error("上传文件异常", e);
                upload = false;
            } finally {
                //登出
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
        return upload;
    }

    //上传单个文件
//    public boolean uploadToFtp(String remotePath, File file) throws IOException {
//        boolean upload = true;
//        FileInputStream fileInputStream = null;
//        if(connectServer(ftpIp,ftpPort,ftpUser,ftpPass)){
//            try {
//                //切换当前工作目录
//                ftpClient.changeWorkingDirectory(remotePath);
//                ftpClient.setBufferSize(1024);
//
//                //上传文件，上传后的文件名，输入流
//                upload = ftpClient.storeFile(file.getName(),new FileInputStream(file));
//            }catch (IOException e){
//                log.error("上传文件异常",e);
//                upload = false;
//            }finally {
//                if(fileInputStream != null) fileInputStream.close();
//                //登出
//                ftpClient.logout();
//                ftpClient.disconnect();
//            }
//        }
//        return  upload;
//    }
    public boolean uploadToFtp(String remotePath, String name, InputStream file) throws IOException {
        boolean upload = true;
        if (connectServer(ftpIp, ftpPort, ftpUser, ftpPass)) {
            try {
                //切换当前工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                //上传文件，上传后的文件名，输入流
                upload = ftpClient.storeFile(name, file);
            } catch (IOException e) {
                log.error("上传文件异常", e);
                upload = false;
            } finally {
                //登出
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
        return upload;
    }

    //判断路径/文件是否存在
    public boolean existFile(String path) throws IOException {
        boolean flag = true;
        if (connectServer(ftpIp, ftpPort, ftpUser, ftpPass)) {
            try {
                ftpClient.changeWorkingDirectory(path);
                if (ftpClient.getReplyCode() == 550) return false;
            } catch (IOException e) {
                log.error("获取文件异常", e);
                flag = false;
            } finally {
                //登出
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
        return flag;
    }

    public boolean existFile(String path, String fileName) throws IOException {
        boolean flag = false;
        if (connectServer(ftpIp, ftpPort, ftpUser, ftpPass)) {
            try {
                ftpClient.changeWorkingDirectory(path);
                if (ftpClient.getReplyCode() == 550) return false;
                FTPFile[] ftpFiles = ftpClient.listFiles(path);
                for (FTPFile file : ftpFiles) {
                    if (file.getName().equals(fileName)) return true;
                }
            } catch (IOException e) {
                log.error("获取文件异常", e);
                flag = false;
            } finally {
                //登出
                ftpClient.logout();
                ftpClient.disconnect();
            }
        }
        return flag;
    }

    //读取ftp文件流，返回字符流
//    public InputStream downloadFileToFtp(String fileName, String ftpFilePath) {
//        InputStream is = null;
//        if(connectServer(ftpIp,ftpPort,ftpUser,ftpPass)){
//            try {
//                //切换当前工作目录
//                ftpClient.changeWorkingDirectory(ftpFilePath);
//                is = ftpClient.retrieveFileStream(fileName);
//            } catch (Exception e) {
//                log.error("下载文件异常",e);
//            } finally {
//                if (ftpClient.isConnected()) {
//                    if (is != null) {
//                        try {
//                            is.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    try {
//                        //登出
//                        ftpClient.logout();
//                        ftpClient.disconnect();
//                    } catch (IOException ioe) {
//                        log.error(ioe.toString());
//                    }
//                }
//            }
//        }
//        return is;
//    }
    public File downloadFileToFtp(String fileName, String ftpFilePath) {
        InputStream is = null;
        File file = new File(fileName);
        if (connectServer(ftpIp, ftpPort, ftpUser, ftpPass)) {
            try {
                //切换当前工作目录
                ftpClient.changeWorkingDirectory(ftpFilePath);
                is = ftpClient.retrieveFileStream(fileName);
                FileUtil.inputStreamToFile(is, file);
            } catch (Exception e) {
                log.error("下载文件异常", e);
            } finally {
                if (ftpClient.isConnected()) {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        //登出
                        ftpClient.logout();
                        ftpClient.disconnect();
                    } catch (IOException ioe) {
                        log.error(ioe.toString());
                    }
                }
            }
        }
        return file;
    }

    public boolean deleteFileToFtp(String ftpFilePath, String name) {
        boolean flag = false;
        if (connectServer(ftpIp, ftpPort, ftpUser, ftpPass)) {
            try {
//                if (!StringUtils.isBlank(fileName)) {
//                    flag = ftpClient.deleteFile(ftpFilePath + "/" + fileName);
//                } else {
//                    FTPFile[] fileList = ftpClient.listFiles(ftpFilePath);
//                    for (FTPFile file : fileList) {
//                        if (file.isDirectory() && ftpClient.listFiles(ftpFilePath + "/" + file.getName() + "/").length > 0){
//                            flag = deleteFileToFtp(ftpFilePath + "/" + file.getName(),null);
//                        }else {
//                            flag = ftpClient.deleteFile(ftpFilePath + "/" + file.getName());
//                        }
//                    }
//                }
                FTPFile[] fileList = ftpClient.listFiles(ftpFilePath);
                ftpClient.changeWorkingDirectory(ftpFilePath);
                for (FTPFile f : fileList) {
                    if (f.getName().equals(name)) {
                        if (f.isDirectory()) flag = ftpClient.removeDirectory(name);
                        else flag = ftpClient.deleteFile(name);
                        return flag;
                    }
                }
                ftpClient.logout();
            } catch (Exception e) {
                log.error("FTP文件删除失败:{}", e);
            } finally {
                if (ftpClient.isConnected()) {
                    try {
                        //登出
                        ftpClient.logout();
                        ftpClient.disconnect();
                    } catch (IOException ioe) {
                        log.error(ioe.toString());
                    }
                }
            }
        }
        return flag;
    }

}
