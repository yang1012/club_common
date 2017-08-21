package club.eryang.common.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * FtpClient工厂
 * 
 * @author zpq
 *
 */
//@Component

public class FTPClientFactory implements PoolableObjectFactory<FTPClient> {

	/**
	 * 日志
	 */
	private static Logger logger = LogManager.getLogger(FTPClientFactory.class);

	/**
	 * ftp配置信息
	 */
	@Autowired
	private FTPClientConfigure config;

	public FTPClient makeObject() throws Exception {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setConnectTimeout(config.getClientTimeout());
		try {
			ftpClient.connect(config.getHost(), config.getPort());
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				logger.warn("FTPServer refused connection");
				return null;
			}
			boolean result = ftpClient.login(config.getUsername(),config.getPassword());
			if (!result) {
				throw new Exception("ftpClient登陆失败! userName:"+ config.getUsername() + " ; password:"+ config.getPassword());
			}
			ftpClient.setFileType(config.getTransferFileType());
			ftpClient.setBufferSize(1024);
			ftpClient.setControlEncoding(config.getEncoding());
			if (config.getPassiveMode()) {
				ftpClient.enterLocalPassiveMode();
			}
		} catch (IOException e) {
			logger.error("makeObject IOException", e);
		} catch (Exception e) {
			logger.error("makeObject Exception", e);
		}
		return ftpClient;
	}

	public void destroyObject(FTPClient ftpClient) throws Exception {
		try {
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.logout();
			}
		} catch (IOException ioe) {
			logger.error("destroyObject IOException", ioe);
		} finally {
			// 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
			try {
				ftpClient.disconnect();
			} catch (IOException io) {
				logger.error("destroyObject IOException", io);
			}
		}

	}

	public boolean validateObject(FTPClient ftpClient) {
		try {
			return ftpClient.sendNoOp();
		} catch (IOException e) {
			logger.error("validateObject IOException", e);
		}
		return false;
	}

	public void activateObject(FTPClient ftpClient) throws Exception {

		if (!ftpClient.isAvailable() || !ftpClient.isConnected()) {
			ftpClient=null;
			ftpClient = this.makeObject();
		}
	}

	public void passivateObject(FTPClient obj) throws Exception {

	}

}
