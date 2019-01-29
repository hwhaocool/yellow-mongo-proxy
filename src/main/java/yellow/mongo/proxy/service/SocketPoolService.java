package yellow.mongo.proxy.service;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yellow.mongo.proxy.model.Address;
import yellow.mongo.proxy.model.MySocket;

public class SocketPoolService {
    
    private final Logger LOGGER = LoggerFactory.getLogger(SocketPoolService.class);

    private List<Address> addressList;
    
    private int totalConNum;
    
    private int threadNum;
    
    private List<MySocket> socketList;
    
    private LinkedBlockingQueue<Socket> clientList;
    
    /**
     * <br>定长线程池
     */
    private static ExecutorService EXECUTOR_SERVICE;
    
    public SocketPoolService(List<Address> addressList, int conTotalNum, int threadNum) {
        this.addressList = addressList;
        this.totalConNum = conTotalNum;
        
        this.threadNum = threadNum;
        
        //线程池
        EXECUTOR_SERVICE = (ExecutorService) Executors.newFixedThreadPool(this.threadNum);
        
        socketList = new ArrayList<>();
        
        clientList = new LinkedBlockingQueue<>();
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
        
        LOGGER.info("connections init completed");
        
        
        EXECUTOR_SERVICE.execute(new Allocation());
        
        LOGGER.info("go xxxxxxxxxxxx");
    }
    
    public void proxy(Socket client) {
        //代理一个 socket
        
        clientList.add(client);
        
//        client.isBound()new LinkedBlockingQueue<Runnable>()
        
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
        
        MySocket orElse = socketList.stream().filter(MySocket::isFree).findAny().orElse(null);
        
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
        LOGGER.info("start init {} connections to {}", num, address);
        
        for (int i = 0; i < num; i++) {
            Socket server = new Socket(address.getIp(), address.getPort());
            
            socketList.add(new MySocket(server));
        }
    }
    
    private void heartBeat() {
        //TODO: 心跳
    }
    
    public class Allocation implements Runnable {

        @Override
        public void run() {
            
            LOGGER.info("SocketPoolService start run");
            
            for(int index = 0;;) {
                if (0 == index % totalConNum) {
                    index = 0;
                }
                
                MySocket mySocket = socketList.get(index);
                if (mySocket.isFree()) {
                    //get a free server socket
                    //then get a client
                    
                    LOGGER.info("get a free server socket, index {}, now client size is {}", index, clientList.size());
                    
                    try {
                        //this is block func, wait until get value
                        Socket currentClient = clientList.take();
                        
                        LOGGER.info("yyyyyy");
                        
                        EXECUTOR_SERVICE.execute( new ConnectionHandlerService(currentClient, mySocket));
                        
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                }
                
                index ++;
            }
        }
        
    }

    
}
