package yellow.mongo.proxy.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yellow.mongo.proxy.model.Address;

public class SocketPoolService {

    private List<Address> addressList;
    
    private int totalConNum;
    
    private int threadNum;
    
    private List<Socket> socketList;
    
    /**
     * <br>定长线程池
     */
    private static ExecutorService EXECUTOR_SERVICE;
    
    public SocketPoolService(List<Address> addressList, int conTotalNum, int threadNum) {
        this.addressList = addressList;
        this.totalConNum = conTotalNum;
        
        //线程池
        EXECUTOR_SERVICE = (ExecutorService) Executors.newFixedThreadPool(threadNum);
        
        socketList = new ArrayList<>();
    }
    
    /**
     * <br>建立连接
     * @author YellowTail
     * @throws IOException 
     * @throws UnknownHostException 
     * @since 2019-01-26
     */
    public void connection() throws UnknownHostException, IOException {
        //建立连接
        
        int addrSize = addressList.size();
        int avgConNum = totalConNum / addrSize;
        
        int firstConNum = totalConNum - (addrSize * avgConNum) + avgConNum;
        
        initSocket(addressList.get(0), firstConNum);
        
        if (1 == addrSize) {
            return;
        }
        
        for (int i = 1; i < addrSize; i++) {
            Address address = addressList.get(i);
            initSocket(address, avgConNum);
        }
    }
    
    public void proxy(Socket client) {
        //代理一个 socket
        
//        client.isBound()
        
//      EXECUTOR_SERVICE.execute( new ConnectionHandlerService(client, server));
      
//      EXECUTOR_SERVICE.execute( new ConnectionHandler(client, mongo_ip, mongo_port, listeners));
    }
    
    /**
     * <br>取出一个空闲的 socket
     * @return
     * @author YellowTail
     * @since 2019-01-26
     */
    private Socket getServerSocket() {
        
        
        return null;
    }
    
    /**
     * <br> 建立 socket 
     * @param address
     * @param num
     * @throws UnknownHostException
     * @throws IOException
     * @author YellowTail
     * @since 2019-01-26
     */
    private void initSocket(Address address, int num) throws UnknownHostException, IOException {
        for (int i = 0; i < num; i++) {
            Socket server = new Socket(address.getIp(), address.getPort());
            
            socketList.add(server);
        }
    }
    
    private void heartBeat() {
        //TODO: 心跳
    }
}
