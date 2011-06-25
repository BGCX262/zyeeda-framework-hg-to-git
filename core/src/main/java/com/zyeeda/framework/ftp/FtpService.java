/**
 * 
 */
package com.zyeeda.framework.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

import com.zyeeda.framework.ftp.internal.FtpConnectionRefusedException;
import com.zyeeda.framework.ftp.internal.FtpServerLoginFailedException;
import com.zyeeda.framework.service.Service;

/**
 * 
 *
 * @creator Qi Zhao
 * @date 2011-6-23
 *
 * @LastChanged
 * @LastChangedBy $LastChangedBy: $
 * @LastChangedDate $LastChangedDate: $
 * @LastChangedRevision $LastChangedRevision:  $
 */
public interface FtpService extends Service {

	public FTPClient connectThenLogin() throws IOException, FtpConnectionRefusedException, FtpServerLoginFailedException;
}
