package org.ftpclient;

import org.ftpclient.utils.FtpCliUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * <p>Description: [类功能描述]</p>
 * @version 1.0
 */
public class FtpCliTest {
    FtpCliUtils ftpCli;
    File file;

    @Before
    public void before() throws IOException {
        ftpCli = FtpCliUtils.createFtpCliUtils("192.168.11.63", "user1", "123456");
        ftpCli.connect();

        String fileName = "testFtp.tmp";
        file = new File(fileName);
        file.createNewFile();
    }

    @After
    public void after() {
        ftpCli.disconnect();
    }

    @Test
    public void uploadFileToDailyDir() throws IOException {
        String path = ftpCli.uploadFileToDailyDir(file.getName(), new FileInputStream(file));
        ftpCli.downloadFileFromDailyDir(path , new FileOutputStream(new File("testFtp.txt")));
    }
}