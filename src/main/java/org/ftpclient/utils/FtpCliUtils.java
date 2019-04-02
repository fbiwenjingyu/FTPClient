package org.ftpclient.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ftp客户端工具类
 * Created by Administrator on 2019/3/25.
 */
public class FtpCliUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int DEFAULT_TIMEOUT = 60 * 1000;
    private static final String DAILY_FILE_PATH = "dailyFilePath";
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private FTPClient ftpClient;
    private volatile String ftpBasePath;

    private FtpCliUtils(String host,String username,String password){
        this(host,21,username,password,DEFAULT_CHARSET);
    }

    private FtpCliUtils(String host,int port,String username,String password){
        this(host,port,username,password,DEFAULT_CHARSET);
    }

    private FtpCliUtils(String host,int port,String username,String password,String charset){
        this.ftpClient = new FTPClient();
        this.ftpClient.setControlEncoding(charset);
        this.host = StringUtils.isEmpty(host) ? "localhost" : host;
        this.port = (port < 0) ? 21 : port;
        this.username = StringUtils.isEmpty(username) ? "anonymous" : username;
        this.password = password;
    }

    /**
     * 创建自定义属性的ftp客户端
     * @param host      主机名或者ip地址
     * @param username  ftp用户名
     * @param password  ftp密码
     * @return
     */
    public static FtpCliUtils createFtpCliUtils(String host,String username,String password){
        return new FtpCliUtils(host, username, password);
    }

    public static FtpCliUtils createFtpCliUtils(String host,int port,String username,String password){
        return new FtpCliUtils(host,port,username, password);
    }

    /**
     * 创建自定义属性的ftp客户端
     * @param host     主机名或者ip地址
     * @param port     ftp端口
     * @param username ftp用户名
     * @param password ftp密码
     * @param charset  字符集
     * @return
     */
    public static FtpCliUtils createFtpCliUtils(String host,int port,String username,String password,String charset){
        return createFtpCliUtils(host,port,username,password,charset);
    }

    /**
     * 设置超时时间
     * @param defaultTimeout 超时时间
     * @param connectTimeout 超时时间
     * @param dataTimeout    超时时间
     */
    public void setTimeOut(int defaultTimeout,int connectTimeout,int dataTimeout){
        this.ftpClient.setDefaultTimeout(defaultTimeout);
        this.ftpClient.setConnectTimeout(connectTimeout);
        this.ftpClient.setDataTimeout(dataTimeout);
    }

    public void connect() throws IOException{
        try {
            this.ftpClient.connect(host,port);
        } catch (UnknownHostException e) {
            throw new IOException("Can't find FTP server :" + host);
        }

        int reply = this.ftpClient.getReplyCode();
        if(!FTPReply.isPositiveCompletion(reply)){
            disconnect();
            throw new IOException("Can't connect to server :" + host);
        }

        if(!this.ftpClient.login(username,password)){
            disconnect();
            throw new IOException("Can't login to server :" + host);
        }

        this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        this.ftpClient.enterLocalPassiveMode();
        initFtpBasePath();
    }

    /**
     * <p>Description:[连接ftp时保存刚登陆ftp时的路径]</p>
     * @throws IOException
     */
    private void initFtpBasePath() throws IOException{
        if(StringUtils.isEmpty(this.ftpBasePath)){
            synchronized (this){
                if(StringUtils.isEmpty(this.ftpBasePath)){
                    this.ftpBasePath = ftpClient.printWorkingDirectory();
                }
            }
        }
    }

    /**
     * <p>Description:[ftp是否处于连接状态，是连接状态返回<tt>true</tt>]</p>
     * @return 是连接状态返回<tt>true</tt>
     */
    public boolean isConnected(){
        return ftpClient.isConnected();
    }

    /**
     * <p>Description:[上传文件到对应日期文件下，
     * 如当前时间是2018-06-06，则上传到[ftpBasePath]/[DAILY_FILE_PATH]/2018/06/06/下]</p>
     * Created on 2018/6/6
     *
     * @param fileName    文件名
     * @param inputStream 文件输入流
     * @return java.lang.String
     */
    public String uploadFileToDailyDir(String fileName, InputStream inputStream) throws IOException{
        changeWorkingDirectory(ftpBasePath);
        SimpleDateFormat dateFormat = new SimpleDateFormat("/yyyy/MM/dd");
        String formatDatePath = dateFormat.format(new Date());
        String uploadDir = DAILY_FILE_PATH + formatDatePath;
        makeDirs(uploadDir);
        storeFile(fileName, inputStream);
        return formatDatePath + "/" + fileName;

    }

    /**
     * <p>Description:[根据uploadFileToDailyDir返回的路径，从ftp下载文件到指定输出流中]</p>
     *
     * @param dailyDirFilePath 方法uploadFileToDailyDir返回的路径
     * @param outputStream     输出流
     */
    public void downloadFileFromDailyDir(String dailyDirFilePath, OutputStream outputStream) throws IOException{
        changeWorkingDirectory(ftpBasePath);
        String ftpRealFilePath = DAILY_FILE_PATH + dailyDirFilePath;
        ftpClient.retrieveFile(ftpRealFilePath, outputStream);
    }




    /**
     * <p>Description:[改变工作目录]</p>
     * @param dir ftp服务器上目录
     * @return boolean 改变成功返回true
     */
    private boolean changeWorkingDirectory(String dir) {
        if(!this.ftpClient.isConnected()){
            return false;
        }
        try {
            return this.ftpClient.changeWorkingDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * <p>Description:[获取ftp上指定文件名到输出流中]</p>

     * @param ftpFileName 文件在ftp上的路径  如绝对路径 /home/ftpuser/123.txt 或者相对路径 123.txt
     * @param out         输出流
     */
    public void retrieveFile(String ftpFileName,OutputStream out) throws IOException{
        try {
            FTPFile[] fileInfoArray = this.ftpClient.listFiles(ftpFileName);
            if (fileInfoArray == null || fileInfoArray.length == 0) {
                throw new FileNotFoundException("File '" + ftpFileName + "' was not found on FTP server.");
            }

            FTPFile fileInfo = fileInfoArray[0];
            if (fileInfo.getSize() > Integer.MAX_VALUE) {
                throw new IOException("File '" + ftpFileName + "' is too large.");
            }
            if(!this.ftpClient.retrieveFile(ftpFileName,out)){
                throw new IOException("Error loading file '" + ftpFileName + "' from FTP server. Check FTP permissions and path.");
            }
            out.flush();
        } finally {
            closeStream(out);
        }
    }

    /**
     * <p>Description:[将输入流存储到指定的ftp路径下]</p>
     *
     * @param ftpFileName 文件在ftp上的路径 如绝对路径 /home/ftpuser/123.txt 或者相对路径 123.txt
     * @param in          输入流
     */
    private void storeFile(String ftpFileName, InputStream in) {
        try {
            if(!this.ftpClient.storeFile(ftpFileName,in)){
                throw new IOException("Can't upload file '" + ftpFileName + "' to FTP server. Check FTP permissions and path.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeStream(in);
        }
    }

    /**
     * <p>Description:[根据文件ftp路径名称删除文件]</p>
     * @param ftpFileName 文件ftp路径名称
     * @throws IOException
     */
    public void deleteFile(String ftpFileName) throws IOException{
        if(!this.ftpClient.deleteFile(ftpFileName)){
            throw new IOException("Can't remove file '" + ftpFileName + "' from FTP server.");
        }
    }

    /**
     * <p>Description:[上传文件到ftp]</p>
     * @param ftpFileName 上传到ftp文件路径名称
     * @param localFile   本地文件路径名称
     * @throws IOException
     */
    public void upload(String ftpFileName,File localFile) throws IOException{
        if(!localFile.exists()){
            throw new IOException("Can't upload '" + localFile.getAbsolutePath() + "'. This file doesn't exist.");
        }
        InputStream in = null;
        try{
            in = new BufferedInputStream(new FileInputStream(localFile));
            if(!this.ftpClient.storeFile(ftpFileName,in)){
                throw new IOException("Can't upload file '" + ftpFileName + "' to FTP server. Check FTP permissions and path.");
            }
        }finally {
            closeStream(in);
        }
    }

    public void uploadDir(String remotePath,String localPath) throws  IOException{
        localPath = localPath.replaceAll("\\\\","/");
        File file = new File(localPath);
        if(file.exists()){
            if(!this.ftpClient.changeWorkingDirectory(remotePath)){
                this.ftpClient.makeDirectory(remotePath);
                this.ftpClient.changeWorkingDirectory(remotePath);
            }
            File[] files = file.listFiles();
            if(files != null){
                for (File f:files) {
                    if(f.isDirectory() && !f.getName().equals(".") && !f.getName().equals("..")){
                        uploadDir(remotePath + "/" + f.getName(), f.getPath());
                    }else {
                        upload(remotePath + "/" + f.getName(),f);
                    }
                }
            }

        }
    }

    /**
     * <p>Description:[下载ftp服务器下文件夹到本地]</p>
     * @param remotePath ftp上文件夹路径名称
     * @param localPath  本地上传的文件夹路径名称
     * @return void
     */
    public void downLoadDir(String remotePath,String localPath) throws IOException{
        localPath = localPath.replaceAll("\\\\","/");
        File file = new File(localPath);
        if(!file.exists()){
            file.mkdirs();
        }
        FTPFile[] ftpFiles = this.ftpClient.listFiles(remotePath);
        for (FTPFile ftpFile:ftpFiles) {
            if(ftpFile != null){
                if(ftpFile.isDirectory() && !ftpFile.getName().equals(".") && !ftpFile.getName().equals("..")){
                    downLoadDir(remotePath + "/" + ftpFile.getName(),localPath + "/" + ftpFile.getName());
                }else{
                    download(remotePath + "/" + ftpFile.getName(),new File(localPath + "/" + ftpFile.getName()));
                }
            }
        }

    }

    /**
     * <p>Description:[下载ftp文件到本地上]</p>
     *
     * @param ftpFileName ftp文件路径名称
     * @param localFile   本地文件路径名称
     */
    public void download(String ftpFileName, File localFile) throws IOException {
        OutputStream out = null;
        try {
            FTPFile[] fileInfoArray = ftpClient.listFiles(ftpFileName);
            if (fileInfoArray == null || fileInfoArray.length == 0) {
                throw new FileNotFoundException("File " + ftpFileName + " was not found on FTP server.");
            }

            FTPFile fileInfo = fileInfoArray[0];
            if (fileInfo.getSize() > Integer.MAX_VALUE) {
                throw new IOException("File " + ftpFileName + " is too large.");
            }

            out = new BufferedOutputStream(new FileOutputStream(localFile));
            if (!ftpClient.retrieveFile(ftpFileName, out)) {
                throw new IOException("Error loading file " + ftpFileName + " from FTP server. Check FTP permissions and path.");
            }
            out.flush();
        } finally {
            closeStream(out);
        }
    }

    /**
     * <p>Description:[列出ftp上文件目录下的文件]</p>
     * @param filePath ftp上文件目录
     * @return java.util.List<java.lang.String>
     */
    public List<String> listFileNames(String filePath) throws IOException{
        FTPFile[] ftpFiles = this.ftpClient.listFiles(filePath);
        List<String> fileList = new ArrayList<>();
        if(ftpFiles != null){
            for (FTPFile ftpFile: ftpFiles) {
                if(ftpFile.isFile()){
                    fileList.add(ftpFile.getName());
                }
            }
        }
        return fileList;
    }

    /**
     * <p>Description:[发送ftp命令到ftp服务器中]</p>
     * @param args ftp命令
     */
    public void sendSiteCommand(String args) throws IOException{
        if(!this.ftpClient.isConnected()){
            this.ftpClient.sendSiteCommand(args);
        }
    }

    /**
     * <p>Description:[获取当前所处的工作目录]</p>
     * @return java.lang.String 当前所处的工作目录
     */
    public String printWorkingDirectory(){
        if(!this.ftpClient.isConnected()){
            return "";
        }
        try {
            return this.ftpClient.printWorkingDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * <p>Description:[切换到当前工作目录的父目录下]</p>
     *
     * @return boolean 切换成功返回true
     */
    public boolean changeToParentDirectory(){
        if(!this.ftpClient.isConnected()){
            return false;
        }
        try {
            return this.ftpClient.changeToParentDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * <p>Description:[返回当前工作目录的上一级目录]</p>
     * @return java.lang.String 当前工作目录的父目录
     */
    public String printParentDirectory(){
        if(!this.ftpClient.isConnected()){
            return "";
        }
        String w = printWorkingDirectory();
        changeToParentDirectory();
        String p = printWorkingDirectory();
        changeWorkingDirectory(w);
        return p;
    }

    /**
     *  <p>Description:[创建目录]</p>
     * @param pathname 路径名
     * @return 创建成功返回true
     * @throws IOException
     */
    public boolean makeDirectory(String pathname) throws  IOException{
        return this.ftpClient.makeDirectory(pathname);
    }

    /**
     * <p>Description:[创建多个目录]</p>
     *
     * @param pathName 路径名
     */
    public void makeDirs(String pathName) throws IOException{
        pathName = pathName.replaceAll("\\\\","/");
        String[] pathNameArray = pathName.split("/");
        for (String each : pathNameArray) {
            if(StringUtils.isNotEmpty(each)){
                this.ftpClient.makeDirectory(each);
                this.ftpClient.changeWorkingDirectory(each);
            }
        }
    }


    /**
     * <p>Description:[关闭流]</p>
     * @param stream 流
     */
    private static void closeStream(Closeable stream){
        if(stream != null){
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * <p>Description:[关闭ftp连接]</p>
     */
    public void disconnect() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                this.ftpClient.logout();
                this.ftpClient.disconnect();
            } catch (IOException ex) {
                // do nothing
            }
        }
    }





}
