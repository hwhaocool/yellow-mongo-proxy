package yellow.mongo.proxy.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import yellow.mongo.proxy.constant.ProxyConstant;

public class ProxyConfig {

    private String deployType;
    
    /**
     * <br>和目标 地址 建立的连接数
     */
    private int dstConNum;
    
    /**
     * <br>目标 地址 列表(必填)
     */
    private List<Address> addressList;
    
    /**
     * <br>线程池 数量
     */
    private int threadsNum;
    
    /**
     * <br>代理 服务 开启的端口列表
     */
    private List<Integer> proxyPortList;
    
    public ProxyConfig() {
        dstConNum = ProxyConstant.DEFAULT_CONN_NUM;
        threadsNum = ProxyConstant.DEFAULT_THREAD_NUM;
        proxyPortList = Arrays.asList(ProxyConstant.DEFAULT_PROXY_PORT);
    }

    public String getDeployType() {
        return deployType;
    }

    public void setDeployType(String deployType) {
        this.deployType = deployType;
    }

    public int getDstConNum() {
        return dstConNum;
    }

    public void setDstConNum(int dstConNum) {
        if (0 >= dstConNum) {
            throw new IllegalArgumentException("dstConNum is illegal");
        }
        
        this.dstConNum = dstConNum;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> strList) {
        
        this.addressList = new ArrayList<>();
        
        for (String string : strList) {
            int indexOf = string.indexOf(':');
            
            if (-1 == indexOf) {
                throw new IllegalArgumentException("address is illegal, ',' not found");
            }
            
            String ip = string.substring(0, indexOf);
            String port = string.substring(indexOf + 1);
            
            addressList.add(new Address(ip, Integer.parseInt(port)));
        }
    }

    public int getThreadsNum() {
        return threadsNum;
    }

    public void setThreadsNum(int threadsNum) {
        if (0 >= threadsNum) {
            throw new IllegalArgumentException("threadsNum is illegal");
        }
        
        this.threadsNum = threadsNum;
    }

    public List<Integer> getProxyPortList() {
        return proxyPortList;
    }

    public void setProxyPortList(List<String> proxyPortList) {
        this.proxyPortList = proxyPortList.stream()
                .map(Integer::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "ProxyConfig [deployType=" + deployType + ", dstConNum=" + dstConNum + ", addressList=" + addressList
                + ", threadsNum=" + threadsNum + ", proxyPortList=" + proxyPortList + "]";
    }
    
}
