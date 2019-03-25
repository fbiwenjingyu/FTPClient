package org.ftpclient.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.net.UnknownHostException;

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

    /**
     * 创建自定义属性的ftp客户端
     * @param host     机名或者ip地址
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
