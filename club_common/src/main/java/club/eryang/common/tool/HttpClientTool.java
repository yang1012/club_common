package club.eryang.common.tool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.*;

import org.springframework.stereotype.Component;

/**
 * @author Mr.yang
 * @version V1.0
 * @ClassName: HttpClientTool
 * @package club.yang.tools
 * @Description: 基于URLConnection的请求工具类 -
 *               记录了请求/响应的首部，请求的参数列表,响应的消息体。每次发出申请，首先重置这些信息，再进行设置。
 * @date 2016年1月15日 下午10:15:15
 */
@Component
public class HttpClientTool {

    /**
     * certmsgr.msc keytool文件目录为当前用户根目录 keytool -import -file baidu.cer
     * -keystore baidu.keys -storepass baidu -storetype jks -storepass yang123
     */

    /**
     * 空字符串
     */
    private static final String EMPTY                       = "";
    /**
     * 请求方式 - GET
     */
    private static final String METHOD_GET                  = "GET";

    /**
     * 请求方式 - POST
     */
    private static final String METHOD_POST                 = "POST";

    /**
     * 请求方式 - HEAD
     */
    private static final String METHOD_HEAD                 = "HEAD";

    /**
     * 请求方式 - PUT
     */
    private static final String METHOD_PUT                  = "PUT";

    /**
     * 请求方式 - DELETE
     */
    private static final String METHOD_DELETE               = "DELETE";

    /**
     * 请求方式 - TRACE
     */
    private static final String METHOD_TRACE                = "TRACE";

    /**
     * 请求方式 - OPTIONS
     */
    private static final String METHOD_OPTIONS              = "OPTIONS";

    /**
     * MIME内容类型首部key - CONTENT_TYPE
     */
    private static final String CONTENT_TYPE                = "Content-Type";

    /**
     * MIME内容类型 - CONTENT_LENGTH
     */
    private static final String CONTENT_LENGTH              = "Content-length";

    /**
     * MIME内容类型 - MIME_JSON
     */
    private static final String MIME_JSON                   = "application/json";

    /**
     * HOST 主机
     */
    private static String       HOST                        = "Host";

    /**
     * 接收数据首部key - Accept
     */
    private static String       ACCEPT                      = "Accept";

    /**
     * 浏览器首部key - User-Agent
     */
    private static String       USER_AGENT                  = "User-Agent";

    /**
     * 接受的编码格式 - accept-encoding
     */
    private static String       ACCEPT_ENCODING             = "accept-encoding";

    /**
     * cookie 首部
     */
    private static String       SET_COOKIE                  = "Set-Cookie";

    /**
     * gzip 编码
     */
    private final String        HTTPS                       = "https";

    /**
     * 默认连接超时时间 - 3秒
     */
    private static final int    DEFAULT_CONNECTION_TIME_OUT = 5000;

    /**
     * 默认读取超时时间 - 10秒
     */
    private static final int    DEFAULT_SO_TIME_OUT         = 10000;

    /**
     * 请求方式
     */
    private String              method                      = EMPTY;
    /**
     * 默认编码格式 - UTF-8
     */
    private static final String DEFALUT_CHARSET_UTF_8       = "utf-8";

    /**
     * 编码格式 - 默认编码格式 -UTF-8
     */
    private String              charset                     = DEFALUT_CHARSET_UTF_8;

    /**
     * 请求参数列表 k-v
     */
    private Map<String, String> reqParams                   = null;

    /**
     * 响应状态 - 默认200
     */
    private int                 resCode                     = 200;

    /**
     * 响应成功状态 范围起始 start - 200
     */
    public static int           RES_START_SUCCESS           = 200;

    /**
     * 响应成功状态 范围结束 end - 300
     */
    public static int           RES_END_SUCCESS             = 300;

    /**
     * 请求首部
     */
    private Map<String, String> reqHeader                   = null;

    /**
     * 响应首部
     */
    private Map<String, String> resHeader                   = null;

    /**
     * 响应消息体
     */
    private byte[]              resBody                     = null;

    /**
     * 连接超时时间 - 默认连接超时间
     */
    private int                 connectionTimeOut           = DEFAULT_CONNECTION_TIME_OUT;

    /**
     * 读取超时时间 - 默认读取超时时间
     */
    private int                 soTimeOut                   = DEFAULT_SO_TIME_OUT;

    /**
     * 请求成功：true;失败：false - 默认falseP：失败
     */
    private boolean             isSuccess                   = false;

    /**
     * 默认 请求协议的标准名称 TLS
     */
    public static final String  DEFAULT_PROTOCOL_TLS        = "TLS";

    /**
     * 请求协议的标准名称 - 默认 TLS
     */
    private String              protocol                    = DEFAULT_PROTOCOL_TLS;

    /**
     * 默认 密匙库类型 JKS
     */
    public static final String  DEFAULT_KS_TYPE_JKS         = "JKS";

    /**
     * 默认 密匙库类型 JKS
     */
    public static final String  DEFAULT_KS_TYPE_PKCS12      = "PKCS12";

    /**
     * 密匙库类型
     */
    private String              ksType                      = DEFAULT_KS_TYPE_JKS;

    /**
     * 默认算法的标准名称 SunX509
     */
    public static String        DEFAULT_ALGORITHM_SUNX509   = "SunX509";

    /**
     * 所请求算法的标准名称
     */
    private String              algorithm                   = DEFAULT_ALGORITHM_SUNX509;

    /**
     * 安全套接字 SSL或者TLS
     */
    private SSLContext          sslContext                  = null;

    /**
     * 连接对象
     */
    private HttpURLConnection   uc                          = null;

    /**
     * @Title: 构造函数
     * @Description: 初始化连接、读取超时时间-单位毫秒 连接超时时间 读取超时时间
     * @author : Mr.yang
     * @date 2016年1月17日 下午1:49:31
     */
    public HttpClientTool() {

        // 初始化请求信息
        initReqParams();
        // 初始化响应信息
        initResParams();
    }

    /**
     * @param connectionTimeOut
     *            连接超时时间
     * @param soTimeOut
     *            读取超时时间
     * @Title: 构造函数
     * @Description: 初始化连接、读取超时时间-单位毫秒
     * @author : Mr.yang
     * @date 2016年1月17日 下午1:49:31
     */
    public HttpClientTool(int connectionTimeOut, int soTimeOut) {

        this();
        // 连接超时时间
        this.connectionTimeOut = connectionTimeOut;
        // 读取超时时间
        this.soTimeOut = soTimeOut;
    }

    /* GET请求重载 */
    public String doGet(String url) {

        return doGet(url, null, null, this.charset, this.connectionTimeOut, this.soTimeOut);
    }

    public String doGet(String url, Map<String, String> params) {

        return doGet(url, params, null, this.charset, this.connectionTimeOut, this.soTimeOut);
    }

    public String doGet(String url, Map<String, String> params, Map<String, String> reqHeader) {

        return doGet(url, params, reqHeader, this.charset, this.connectionTimeOut, this.soTimeOut);
    }

    public String doGet(String url, Map<String, String> params, String charset, int connectionTimeOut, int soTimeOut) {

        return doGet(url, params, null, charset, connectionTimeOut, soTimeOut);
    }

    public String doGet(String url, Map<String, String> params, Map<String, String> reqHeader, String charset,
            int connectionTimeOut, int soTimeOut) {
        return execute(METHOD_GET, url, params, reqHeader, charset, connectionTimeOut, soTimeOut);
    }

    /* POST请求重载 */
    public String doPost(String url, Map<String, String> params) {

        return doPost(url, params, null);
    }

    public String doPost(String url, Map<String, String> params, Map<String, String> reqHeader) {

        return doPost(url, params, reqHeader, this.charset, this.connectionTimeOut, this.soTimeOut);
    }

    public String doPost(String url, Map<String, String> params, String charset, int connectionTimeOut, int soTimeOut) {

        return doPost(url, params, null, charset, connectionTimeOut, soTimeOut);
    }

    public String doPost(String url, Map<String, String> params, Map<String, String> reqHeader, String charset,
            int connectionTimeOut, int soTimeOut) {

        return execute(METHOD_POST, url, params, reqHeader, charset, connectionTimeOut, soTimeOut);
    }

    /* PUT 请求重载 */
    public String doPut(String url, Map<String, String> params) {

        return doPut(url, params, null);
    }

    public String doPut(String url, Map<String, String> params, Map<String, String> reqHeader) {

        return doPut(url, params, reqHeader, this.charset, this.connectionTimeOut, this.soTimeOut);
    }

    public String doPut(String url, Map<String, String> params, String charset, int connectionTimeOut, int soTimeOut) {

        return doPut(url, params, null, charset, connectionTimeOut, soTimeOut);
    }

    public String doPut(String url, Map<String, String> params, Map<String, String> reqHeader, String charset,
            int connectionTimeOut, int soTimeOut) {

        return execute(METHOD_PUT, url, params, reqHeader, charset, connectionTimeOut, soTimeOut);
    }

    /* DELETE 请求重载 */
    public String doDelete(String url, Map<String, String> params) {

        return doDelete(url, params, null);
    }

    public String doDelete(String url, Map<String, String> params, Map<String, String> reqHeader) {

        return doDelete(url, params, reqHeader, this.charset, this.connectionTimeOut, this.soTimeOut);
    }

    public String doDelete(String url, Map<String, String> params, String charset, int connectionTimeOut,
            int soTimeOut) {

        return doDelete(url, params, null, charset, connectionTimeOut, soTimeOut);
    }

    public String doDelete(String url, Map<String, String> params, Map<String, String> reqHeader, String charset,
            int connectionTimeOut, int soTimeOut) {

        return execute(METHOD_DELETE, url, params, reqHeader, charset, connectionTimeOut, soTimeOut);
    }

    /* HEAD 请求重载 */
    public String doHead(String url, Map<String, String> params) {

        return doHead(url, params, null);
    }

    public String doHead(String url, Map<String, String> params, Map<String, String> reqHeader) {

        return doHead(url, params, reqHeader, this.charset, this.connectionTimeOut, this.soTimeOut);
    }

    public String doHead(String url, Map<String, String> params, String charset, int connectionTimeOut, int soTimeOut) {

        return doHead(url, params, null, charset, connectionTimeOut, soTimeOut);
    }

    public String doHead(String url, Map<String, String> params, Map<String, String> reqHeader, String charset,
            int connectionTimeOut, int soTimeOut) {

        return execute(METHOD_PUT, url, params, reqHeader, charset, connectionTimeOut, soTimeOut);
    }

    /**
     * @param url
     *            连接
     * @param params
     *            请求参数
     * @param reqHeader
     *            首部
     * @param charset
     *            编码
     * @param connectionTimeOut
     *            连接超时时间
     * @param soTimeOut
     *            读取超时时间
     * @return String
     * @Title: execute
     * @Description: 发送请求
     * @author Mr.yang
     * @date 2016年1月17日 下午11:09:07
     */
    public String execute(String method, String url, Map<String, String> params, Map<String, String> reqHeader,
            String charset, int connectionTimeOut, int soTimeOut) {

        // 初始化请求参数信息
        initReqParams();

        // 编码格式
        if (Utils.isNotNull(charset)) {
            this.charset = charset;
        }

        // 请求方式
        this.method = method.toUpperCase();

        // 返回值
        String result = EMPTY;
        try {
            if (this.method.equals(METHOD_GET) || this.method.equals(METHOD_HEAD)) {
                // HEAD、 GET请求
                // 设置请求参数
                url = setParam(url, params);
            }
            // 初始化连接
            initUrlConnection(url);

            // 设置请求首部，连接、读取超时时间
            setReqParam(reqHeader, connectionTimeOut, soTimeOut);

            if (this.method.equals(METHOD_POST) || this.method.equals(METHOD_PUT)
                    || this.method.equals(METHOD_DELETE)) {
                // POST、PUT请求
                // 允许写入
                uc.setDoOutput(true);
                // 设置请求参数
                setParam(params);
            }

            // 读取记录响应信息
            readRes();

            // 响应首部字符编码
            String contentTypeCharset = getContentTypeCharset();
            result = new String(resBody, contentTypeCharset);

        } catch (Exception e) {

            isSuccess = false;
            e.printStackTrace();

        } finally {
            if (Utils.isNotNull(this.uc)) {
                this.uc.disconnect();
            }
        }

        return result.trim();
    }

    /**
     * @Title: initReqParams
     * @Description: 初始化请求信息
     * @author Mr.yang
     * @date 2016年1月17日 下午10:44:49
     */
    private void initReqParams() {
        this.reqHeader = new HashMap<String, String>();
        this.reqParams = new HashMap<String, String>();
    }

    /**
     * @Title: initResParams
     * @Description: 初始化响应信息
     * @author Mr.yang
     * @date 2016年1月17日 下午10:46:38
     */
    private void initResParams() {

        this.resCode = 200;
        this.resHeader = new HashMap<String, String>();
        this.resBody = EMPTY.getBytes();
    }

    /**
     * @return Map<String, String>
     * @Title: getRes_Cookie
     * @Description: 获取响应的cookie
     * @author Mr.yang
     * @date 2016年1月18日 下午4:18:24
     */
    public Map<String, String> getResCookie() {

        Map<String, String> cookie = new HashMap<String, String>();

        cookie.put(SET_COOKIE, resHeader.get(SET_COOKIE));
        return cookie;
    }

    /**
     * @param reqHeader
     *            请求首部
     * @param connectionTimeOut
     *            连接超时时间
     * @param soTimeOut
     *            读取超时时间
     * @throws IOException
     * @Title: setReqParam
     * @Description: 设置请求首部，连接、读取超时时间。读取记录响应信息
     * @author Mr.yang
     * @date 2016年1月17日 下午4:21:43
     */
    private void setReqParam(Map<String, String> reqHeader, int connectionTimeOut, int soTimeOut) throws IOException {

        // 允许读取
        this.uc.setDoInput(true);

        // 请求方式
        this.uc.setRequestMethod(this.method);

        // 设置请求首部信息
        setReqHeader(reqHeader);

        // 设置连接、读取超时时间
        setConnection(connectionTimeOut, soTimeOut);

        // 根据响应首部设置当前对象cookie保持会话连接状态
        Map<String, String> cookie = getResCookie();
        if (Utils.isNotNull(cookie)) {
            setReqHeader(SET_COOKIE, cookie.get(SET_COOKIE));
        }
    }

    /**
     * @return
     * @Title: getContentTypeCharset
     * @Description: 获得响应的MIME类型的编码
     * @author Mr.yang
     * @date 2016年1月17日 下午4:38:40
     */
    private String getContentTypeCharset() {

        // MIME类型编码
        String contentTypeCharset = EMPTY;
        // 记录的响应首部中检索
        if (Utils.isNotNull(resHeader) && resHeader.containsKey(CONTENT_TYPE)) {
            String header = resHeader.get(CONTENT_TYPE);
            if (header.indexOf("charset") != -1) {
                contentTypeCharset = header.substring(header.indexOf("charset")).split("=")[1];
            }
        }
        if (Utils.isNull(contentTypeCharset)) {
            // 如果不存在默认使用-请求传入的字符编码
            contentTypeCharset = charset;
        }
        return contentTypeCharset;
    }

    /**
     * @param field
     * @return String
     * @Title: getReqHeader
     * @Description: 根据head的键获取响应header中对应的值
     * @author Mr.yang
     * @date 2016年1月17日 下午10:16:14
     */
    public String getResHeader(String field) {
        return resHeader.get(field);
    }

    /**
     * @param field
     * @return String
     * @Title: getReqHeader
     * @Description: 根据head的键获取请求header中对应的值
     * @author Mr.yang
     * @date 2016年1月17日 下午10:16:14
     */
    public String getReqHeader(String field) {
        return reqHeader.get(field);
    }

    /**
     * @throws IOException
     * @Title: readRes
     * @Description: 读取记录响应信息
     * @author Mr.yang
     * @date 2016年1月17日 下午4:18:46
     */
    private void readRes() throws IOException {

        // 初始化响应信息
        initResParams();

        // 响应码
        this.resCode = this.uc.getResponseCode();

        // 记录响应的首部信息
        recordResHeader();

        // 读取记录响应消息体
        readResContent();
    }

    /**
     * @Title: recordResHeader
     * @Description: 记录响应的首部信息 连接
     * @author Mr.yang
     * @date 2016年1月17日 下午4:00:12
     */
    private void recordResHeader() {

        // 响应首部
        this.resHeader = new HashMap<String, String>();

        for (int i = 1;; i++) {
            String header = this.uc.getHeaderField(i);
            if (Utils.isNull(header)) {
                // 没有首部信息 - 退出
                break;
            } else {
                // 记录响应的首部信息
                resHeader.put(this.uc.getHeaderFieldKey(i), header);
            }
        }
    }

    /**
     * @throws IOException
     * @Title: readResContent
     * @Description: 读取记录响应信息
     * @author Mr.yang
     * @date 2016年1月17日 下午4:16:15
     */
    private void readResContent() throws IOException {

        // 响应输入流
        InputStream in = null;
        if (this.resCode >= RES_START_SUCCESS && this.resCode <= RES_END_SUCCESS) {
            // 成功响应 - 用inputStream
            in = this.uc.getInputStream();
        } else {
            // 成功响应以外的 - 用errorStream
            in = this.uc.getErrorStream();
        }

        if (Utils.isNotNull(in)) {
            // 响应消息体
            resBody = inputStreamTOByte(in);
            // 关闭输入流
            in.close();
        } else {
            resBody = null;
        }
    }

    /**
     * @param in
     *            输入流
     * @return byte[]
     * @throws IOException
     * @Title: inputStreamTOByte
     * @Description: 读取输入流信息转换成字节数组 注意： 此处不处理输入流的关闭
     * @author Mr.yang
     * @date 2016年1月17日 下午4:08:56
     */
    private byte[] inputStreamTOByte(InputStream in) throws IOException {
        // 缓冲区
        int BUFFER_SIZE = 4096;
        // 字节输出流
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];

        int count;
        // 从输入流中反复读取数据
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
            outStream.write(data, 0, count);
        }

        // 输出流转出成字节数组
        byte[] outByte = outStream.toByteArray();
        // 关闭输出流
        outStream.close();

        return outByte;
    }

    /**
     * @param connectionTimeOut
     *            连接超时时间
     * @param soTimeOut
     *            读取超时时间
     * @Title: setConnection
     * @Description: 设置连接、读取超时时间
     * @author Mr.yang
     * @date 2016年1月17日 下午3:26:40
     */
    private void setConnection(int connectionTimeOut, int soTimeOut) {

        // 连接超时间
        if (Utils.isNotNull(connectionTimeOut)) {
            this.connectionTimeOut = connectionTimeOut;
            this.uc.setConnectTimeout(connectionTimeOut);
        }

        // 读取超时时间
        if (Utils.isNotNull(soTimeOut)) {
            this.soTimeOut = soTimeOut;
            this.uc.setReadTimeout(soTimeOut);
        }
    }

    /**
     * @param url
     *            地址
     * @param params
     *            参数
     * @return String
     * @throws UnsupportedEncodingException
     * @Title: setParam
     * @Description: 设置get请求参数
     * @author Mr.yang
     * @date 2016年1月17日 下午2:07:12
     */
    private String setParam(String url, Map<String, String> params) throws UnsupportedEncodingException {

        // 参数设置
        this.reqParams = params;
        if (Utils.isNotNull(params)) {
            Iterator<String> it = params.keySet().iterator();
            StringBuilder stringBuilder = new StringBuilder();
            while (it.hasNext()) {
                String key = it.next();
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode(key, charset));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(String.valueOf(params.get(key)), charset));
            }
            url += stringBuilder.toString().replaceFirst("&", "?");

        }
        return url;
    }

    /**
     * @param params
     *            请求参数
     * @throws IOException
     * @Title: setParam
     * @Description: 设置POST请求参数
     * @author Mr.yang
     * @date 2016年1月17日 下午10:07:44
     */
    private void setParam(Map<String, String> params) throws IOException {

        /* 设置请求头 Content-Type */
        setReqHeader(CONTENT_TYPE, MIME_JSON + ";" + "charset=" + this.charset);

        this.reqParams = params;
        if (Utils.isNotNull(params)) {
            // 参数转成json
            String jsonParams = JsonTool.toJsonStr(params);

            // 字节数组数据
            byte[] data = jsonParams.getBytes(this.charset);

            // 限制每次读取最大数据长度
            int len = 1024;

            // 数据长度
            int contentLength = data.length;

            // 连接的输出流
            BufferedOutputStream out = new BufferedOutputStream(this.uc.getOutputStream());
            int off = 0;

            // 参数写入输出流
            while (off < contentLength) {
                if (len >= contentLength) {
                    out.write(data, off, contentLength);
                    off += contentLength;
                } else {
                    out.write(data, off, len);
                    off += len;
                    contentLength -= len;
                }

                // 刷新缓冲区
                out.flush();
            }

            // 关闭输出流
            out.close();
        }
    }

    /**
     * @param reqHeader
     *            首部信息
     * @return void
     * @throws UnsupportedEncodingException
     * @Title: setReqHeader
     * @Description: 设置首部信息
     * @author Mr.yang
     * @date 2016年1月17日 下午3:10:30
     */
    private void setReqHeader(Map<String, String> reqHeader) throws UnsupportedEncodingException {
        //
        this.uc.setAllowUserInteraction(false);
        // 不使用缓存
        this.uc.setUseCaches(false);
        // 主机
        this.uc.setRequestProperty(HOST, this.uc.getURL().getHost());
        // 接受的MIME类型
        this.uc.setRequestProperty(ACCEPT, MIME_JSON);
        // 用户浏览器
        // this.uc.setRequestProperty(USER_AGENT, "chrom");
        // 接受的编码格式
        this.uc.setRequestProperty(ACCEPT_ENCODING, this.charset);

        // 循环遍历设置首部信息
        if (Utils.isNotNull(reqHeader)) {
            Iterator<String> it = reqHeader.keySet().iterator();
            String key = EMPTY;

            while (it.hasNext()) {
                key = it.next();
                this.uc.setRequestProperty(key, reqHeader.get(key));
            }

            // 记录请求的首部信息
            this.reqHeader.putAll(reqHeader);
        }
    }

    /**
     * @param filed_key
     *            首部key
     * @param field_value
     *            首部值
     * @Title: setReqHeader
     * @Description: 设置首部信息
     * @author Mr.yang
     * @date 2016年1月17日 下午10:31:27
     */
    private void setReqHeader(String filed_key, String field_value) {
        this.uc.setRequestProperty(filed_key, field_value);
        // 记录请求的首部信息
        this.reqHeader.put(filed_key, field_value);
    }

    /**
     * @param url
     *            请求地址
     * @return URLConnection
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @Title: getUrlConnection
     * @Description: 根据请求地址获取请求链接
     * @author Mr.yang
     * @date 2016年1月17日 下午3:06:35
     */
    private void initUrlConnection(String url) throws IOException, KeyManagementException, NoSuchAlgorithmException {

        URL urlObj = new URL(url);

        this.uc = (HttpURLConnection) urlObj.openConnection();

        if (url.startsWith(this.HTTPS)) {
            if (Utils.isNull(sslContext)) {

                // 内部默认实现的的SSLContext安全套接字实现,信任所有
                initDefalutSSLClient();
            }
            // 安全套接字工厂
            SSLSocketFactory sf = sslContext.getSocketFactory();
            // 设置HttpsURLConnection的安全套接字工厂
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) this.uc;
            httpsURLConnection.setSSLSocketFactory(sf);
            httpsURLConnection.setHostnameVerifier(new TrustAnyHostnameVerifier());
        }
    }

    /**
     * @author Mr.yang
     * @version V1.0
     * @ClassName: TrustAnyHostnameVerifier
     * @package club.yang.tools
     * @Description: 内部类
     * @date 2016年1月29日 下午8:13:35
     */
    private class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    /**
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws Exception
     * @Title: initDefalutSSLClient
     * @Description: 内部默认实现的的SSLContext安全套接字实现, 信任所有
     * @author Mr.yang
     * @date 2016年1月7日 下午3:44:03
     * @version V1.0
     */
    private void initDefalutSSLClient() throws NoSuchAlgorithmException, KeyManagementException {

        // 实例化SSL安全套接字
        sslContext = SSLContext.getInstance(DEFAULT_PROTOCOL_TLS);

        // 初始化SSL上下文
        sslContext.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());

    }

    /**
     * @param ks_path
     *            证书路径
     * @param password
     *            证书公匙
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     * @throws IOException
     * @throws CertificateException
     * @Title: setCustomSSLContext
     * @Description: 初始化SSLContext安全套接字(使用默认属性 TLS, JKS, SunX509)
     *               如果实例化对象为http请求，则执行该方法设置用户自定义的安全套接SSL TLS并不会执行
     * @author Mr.yang
     * @date 2016年1月7日 下午3:57:53
     */
    public void setCustomSSLContext(String ks_path, String password) throws UnrecoverableKeyException,
            KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {

        setCustomSSLContext(ks_path, password, algorithm, protocol, ksType);
    }

    /**
     * @param ksPath
     *            证书路径
     * @param password
     *            证书公匙
     * @param algorithm
     *            密匙管理器算法的标准名称
     * @param protocol
     *            SSLContext 类型 SSL/TLS
     * @param ksType
     *            密匙库类型 JKS
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws UnrecoverableKeyException
     * @throws KeyManagementException
     * @throws IOException
     * @throws CertificateException
     * @Title: setCustomSSLContext
     * @Description: 初始化SSLContext安全套接字，如果实例化对象为http请求，则执行该方法设置用户自定义的安全套接SSL
     *               TLS并不会执行
     * @author Mr.yang
     * @date 2016年1月7日 下午3:57:53
     */
    public void setCustomSSLContext(String ksPath, String password, String algorithm, String protocol, String ksType)
            throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, KeyManagementException,
            CertificateException, IOException {

        // 密匙管理器算法的标准名称
        this.algorithm = algorithm;

        // 实例化密钥库管理器
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);

        // 获得密钥库
        KeyStore keyStore = getKeyStore(ksPath, password, ksType);
        // 初始化密钥库管理器
        keyManagerFactory.init(keyStore, password.toCharArray());

        // 实例化信任库 目前采用默认的算法
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());

        // 初始化信任库管理器
        trustManagerFactory.init(keyStore);
        // 实例化SSL安全套接字
        sslContext = SSLContext.getInstance(protocol);
        // 初始化SSL上下文
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

    }

    /**
     * 获得KeyStore.
     *
     * @param ksPath
     *            密钥库路径
     * @param password
     *            密码
     * @param ksType
     *            keystore 类型
     * @return 密钥库
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    private KeyStore getKeyStore(String ksPath, String password, String ksType)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        // 实例化密钥库
        KeyStore ks = KeyStore.getInstance(ksType);
        // 获得密钥库文件流
        FileInputStream is = new FileInputStream(new File(ksPath));
        // 加载密钥库
        ks.load(is, password.toCharArray());
        // 关闭密钥库文件流
        is.close();
        return ks;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public Map<String, String> getReqHeader() {
        return reqHeader;
    }

    public Map<String, String> getResHeader() {
        return resHeader;
    }

    public void setResHeader(Map<String, String> resHeader) {
        this.resHeader = resHeader;
    }

    public byte[] getResBody() {
        return resBody;
    }

    public void setResBody(byte[] resBody) {
        this.resBody = resBody;
    }

    public static void main(String[] args) {
        HttpClientTool client_tool = new HttpClientTool();
        // String key_path = FileUtils.getClassPath() + "\\conf\\baidu.keys";
        try {
            // client_tool.setCustomSSLContext(key_path, "yang123");
            String url = "https://www.baidu.com/";
            System.out.println(client_tool.doGet(url));
            System.out.println(client_tool.uc.getRequestMethod());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        int temp = 0;
        temp++;
        temp = temp + 1;
        // System.out.println(11%10);
        // System.out.println(StringUtils.join(new
        // String[]{"{1,2}","{2,3}","{4,5}"},","));
    }
}
