package club.eryang.common.ftp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * ftp配置信息
 *
 * @author zpq
 */
@Component
public class FTPClientConfigure implements Serializable {

    private static final long serialVersionUID = -8880489730915081938L;
    /**
     * 域名
     */
    @Value("${ftp.host}")
    private String host;
    /**
     * 端口号
     */
    @Value("${ftp.port}")
    private int port;
    /**
     * 用户名
     */
    @Value("${ftp.username}")
    private String username;
    /**
     * 密码
     */
    @Value("${ftp.password}")
    private String password;
    /**
     * 模式 主动模式  服务器主动链接客服端数据传输接口。需客户端可接受高位链接，没有防火墙关闭。
     * 被动模式  服务器不主动链接，被动接口，客户端主动链接。url 方式如ftp://是采用被动。
     */
    @Value("${ftp.passiveMode}")
    private boolean passiveMode;
    /**
     * 文件编码
     */
    @Value("${ftp.encoding}")
    private String encoding;
    /**
     * 客户端超时时间
     */
    @Value("${ftp.clientTimeout}")
    private int clientTimeout;
    /**
     * 线程数
     */
    @Value("${ftp.threadNum}")
    private Integer threadNum;
    /**
     * 传输文件类型
     */
    @Value("${ftp.transferFileType}")
    private int transferFileType;
    /**
     * 是否重命名
     */
    @Value("${ftp.renameUploaded}")
    private boolean renameUploaded;
    /**
     * 重试次数
     */
    private int retryTimes;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public Integer getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(Integer threadNum) {
        this.threadNum = threadNum;
    }

    public int getTransferFileType() {
        return transferFileType;
    }

    public void setTransferFileType(int transferFileType) {
        this.transferFileType = transferFileType;
    }

    public boolean isRenameUploaded() {
        return renameUploaded;
    }

    public void setRenameUploaded(boolean renameUploaded) {
        this.renameUploaded = renameUploaded;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    @Override
    public String toString() {
        return "FTPClientConfig [host=" + host + "\n port=" + port + "\n username=" + username + "\n password=" + password + "\n passiveMode=" + passiveMode
                + "\n encoding=" + encoding + "\n clientTimeout=" + clientTimeout + "\n threadNum=" + threadNum + "\n transferFileType="
                + transferFileType + "\n renameUploaded=" + renameUploaded + "\n retryTimes=" + retryTimes + "]";
    }

}
