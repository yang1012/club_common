package club.eryang.common.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class FTPClientPool implements ObjectPool<FTPClient> {

    private static Logger logger = LogManager.getLogger(FTPClientPool.class);
    @Autowired
    private FTPClientConfigure ftpClientConfigure;

    /**
     * 默认连接池大小
     */
    private static final int DEFAULT_POOL_SIZE = 50;

    /**
     * 最大连接池大小
     */
    private static final int MAX_POOL_SIZE = 150;
    private BlockingQueue<FTPClient> pool;

    @Autowired
    private FTPClientFactory factory;

    @PostConstruct
    public void init() {
        logger.info("FTPClientPooled init start");
        int poolSize = (ftpClientConfigure.getThreadNum() == null || ftpClientConfigure.getThreadNum().intValue() == 0) ? DEFAULT_POOL_SIZE
                : ftpClientConfigure.getThreadNum().intValue() > MAX_POOL_SIZE ? MAX_POOL_SIZE : ftpClientConfigure.getThreadNum().intValue();
        pool = new ArrayBlockingQueue<FTPClient>(poolSize);
        try {
            this.initPool(poolSize);
        } catch (Exception e) {
            logger.error("init Exception", e);
        }
        logger.info("FTPClientPooled init end");
    }


    /**
     * 初始化连接池，需要注入一个工厂来提供FTPClient实例
     *
     * @param maxPoolSize
     * @throws Exception
     */
    private void initPool(int maxPoolSize) throws Exception {
        for (int i = 0; i < maxPoolSize; i++) {
            addObject();
        }
    }

    public FTPClient borrowObject() throws Exception {
        FTPClient client = null;
        //防止获取次数太多，造成死循环
        int times = pool.size();
        do {
            times--;
            if (null != client) {
                //使对象在池中失效
                invalidateObject(client);
            }
            client = pool.take();
            //制造并添加新对象到池中
            addObject();
        } while (!factory.validateObject(client) && times > 0);//验证不通过

        return client;
    }

    public void returnObject(FTPClient client) throws Exception {

        if ((client != null) && !pool.offer(client, 3, TimeUnit.SECONDS)) {
            try {
                factory.destroyObject(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void invalidateObject(FTPClient client) throws Exception {
        //移除无效的客户端
        pool.remove(client);
    }

    public void addObject() throws Exception {
        //插入对象到队列
        pool.offer(factory.makeObject(), 3, TimeUnit.SECONDS);
    }

    public int getNumIdle() throws UnsupportedOperationException {

        return 0;
    }

    public int getNumActive() throws UnsupportedOperationException {

        return 0;
    }

    public void clear() throws Exception {

    }

    public void close() throws Exception {
        while (pool.iterator().hasNext()) {
            FTPClient client = pool.take();
            factory.destroyObject(client);
        }

    }

    public void setFactory(PoolableObjectFactory<FTPClient> factory) throws IllegalStateException, UnsupportedOperationException {
        if (factory instanceof FTPClientFactory) {
            this.factory = (FTPClientFactory) factory;
        }
    }


}
