package yellow.mongo.proxy.model;

public class Address {

    /**
     * <br>ip 或者 host
     */
    private String ip;
    
    /**
     * <br>端口
     */
    private int port;
    
    public Address() {
        
    }
    
    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return 得到 port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port 设置 port
     */
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Address [ip=" + ip + ", port=" + port + "]";
    }
}
