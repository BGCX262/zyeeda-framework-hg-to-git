/**
 * 
 */
package com.zyeeda.framework.ftp.internal;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.ServiceId;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zyeeda.framework.config.ConfigurationService;
import com.zyeeda.framework.ftp.FtpService;
import com.zyeeda.framework.service.AbstractService;

/**
 * 
 *
 * @creator Qi Zhao
 * @date 2011-6-24
 *
 * @LastChanged
 * @LastChangedBy $LastChangedBy: $
 * @LastChangedDate $LastChangedDate: $
 * @LastChangedRevision $LastChangedRevision:  $
 */
@ServiceId("commons-ftp-service-provider")
@Marker(Primary.class)
public class CommonsFtpServiceProvider extends AbstractService implements FtpService {

    private static final Logger logger = LoggerFactory.getLogger(CommonsFtpServiceProvider.class);

    private static final String FTP_HOST = "ftpHost";
    private static final String FTP_USER_NAME = "ftpUserName";
    private static final String FTP_PASSWORD = "ftpPassword";
    private static final String FTP_PORT = "ftpPort";

    private static final String DEFAULT_FTP_HOST = "10.118.250.131";
    private static final String DEFAULT_FTP_USER_NAME = "gzsc";
    private static final String DEFAILT_FTP_PASSWORD = "gzsc";
    private static final int DEFAULT_FTP_PORT = 21;

    private String ftpHost;
    private String ftpUserName;
    private String ftpPassword;
    private int ftpPort;

    public CommonsFtpServiceProvider(ConfigurationService configSvc,
            RegistryShutdownHub shutdownHub) {
        super(shutdownHub);
        Configuration config = this.getConfiguration(configSvc);
        this.init(config);
    }

    private void init(Configuration config) {
        this.ftpHost = config.getString(FTP_HOST, DEFAULT_FTP_HOST);
        this.ftpUserName = config.getString(FTP_USER_NAME, DEFAULT_FTP_USER_NAME);
        this.ftpPassword = config.getString(FTP_PASSWORD, DEFAILT_FTP_PASSWORD);
        this.ftpPort = config.getInt(FTP_PORT, DEFAULT_FTP_PORT);
    }

    public FTPClient connectThenLogin() throws IOException,
           FtpConnectionRefusedException, FtpServerLoginFailedException {

               FTPClient ftp = new FTPClient();
               ftp.connect(this.ftpHost, this.ftpPort);
               logger.debug("connected to server = {} ", this.ftpHost);

               String[] messages = ftp.getReplyStrings();
               for (String msg : messages) {
                   logger.debug(msg);
               }

               int replyCode = ftp.getReplyCode();
               if (!FTPReply.isPositiveCompletion(replyCode)) {
                   ftp.disconnect();
                   throw new FtpConnectionRefusedException(
                           "Refused to connect to server " + this.ftpHost + ".");
               }

               boolean sucessful = ftp.login(this.ftpUserName, this.ftpPassword);
               if (!sucessful) {
                   ftp.disconnect();
                   throw new FtpServerLoginFailedException(
                           "Failed to login to server " + this.ftpHost + ".");
               }

               return ftp;
    }

    public void deleteFiles(FTPClient client, FTPFile[] ftpFiles) throws IOException {
        logger.debug("delete file size = {}", ftpFiles.length);

        for (FTPFile ftpFile : ftpFiles) {
            client.deleteFile(ftpFile.getName()); 
        }
    }
}
