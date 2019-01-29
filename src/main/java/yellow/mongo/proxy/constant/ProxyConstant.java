package yellow.mongo.proxy.constant;

public class ProxyConstant {

    /**
     * <br>默认开启 100 个线程
     */
    public static final int DEFAULT_THREAD_NUM = 3;
    
    /**
     * <br>默认 建立 50个 连接
     */
    public static final int DEFAULT_CONN_NUM = 50;
    
    /**
     * <br>默认的 代理端口
     */
    public static final int DEFAULT_PROXY_PORT = 10002;
    
    /**
     * 命令行 参数 
     * @author YellowTail
     * @since 2019-01-26
     */
    public static class ArgName {
        
         public static final String DEPLOY_TYPE = "deployType";
         
         public static final String DST_CON_NUM = "dstConNum";
         
         public static final String DST_ADDRESS = "dstAddress";
         
         public static final String THREADS_NUM = "threadsNum";
         
         public static final String PROXY_PORT = "proxyPort";
    }
}
