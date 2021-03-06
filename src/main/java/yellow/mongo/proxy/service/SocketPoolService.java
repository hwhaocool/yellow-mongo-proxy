package yellow.mongo.proxy.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yellow.mongo.proxy.model.Address;
import yellow.mongo.proxy.model.MyChannel;

public class SocketPoolService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SocketPoolService.class);

    private static List<Address> addressList;
    
    private static int totalConNum;
    
    private static int threadNum;
    
    private static List<MyChannel> channelList;
    
    /**
     * <br>异步通道组
     */
    private static AsynchronousChannelGroup group;
    
    /**
     * <br>定长线程池
     */
    private static ExecutorService EXECUTOR_SERVICE;
    
    public static List<Address> getAddressList() {
        return addressList;
    }

    public static void setAddressList(List<Address> addressList) {
        SocketPoolService.addressList = addressList;
    }

    public static int getTotalConNum() {
        return totalConNum;
    }

    public static void setTotalConNum(int totalConNum) {
        SocketPoolService.totalConNum = totalConNum;
    }

    public static int getThreadNum() {
        return threadNum;
    }

    public static void setThreadNum(int threadNum) {
        SocketPoolService.threadNum = threadNum;
        
        //线程池
        EXECUTOR_SERVICE = (ExecutorService) Executors.newFixedThreadPool(threadNum);
        
        try {
            group = AsynchronousChannelGroup.withThreadPool(EXECUTOR_SERVICE);
        } catch (IOException e) {
            LOGGER.error("init group occur errors, ", e);
        }
    }

    public static ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }
    
    /**
     * <br>得到 异步通道组
     * @return
     * @author YellowTail
     * @since 2019-02-01
     */
    public static AsynchronousChannelGroup getChannelGroup() {
        return group;
    }

    /**
     * <br>建立连接
     * @author YellowTail
     * @throws IOException 
     * @throws UnknownHostException 
     * @since 2019-01-26
     */
    public static void connection() throws UnknownHostException, IOException {
        //建立连接
        
        channelList = new ArrayList<>();
        
        int addrSize = addressList.size();
        int avgConNum = totalConNum / addrSize;
        
        int firstConNum = totalConNum - (addrSize * avgConNum) + avgConNum;
        
        initChannel(addressList.get(0), firstConNum);
        
        if (1 == addrSize) {
            return;
        }
        
        for (int i = 1; i < addrSize; i++) {
            Address address = addressList.get(i);
            initChannel(address, avgConNum);
        }
        
        LOGGER.info("connections init completed");
        
    }
    
    /**
     * <br>取出一个空闲的 通道
     * @return
     * @author YellowTail
     * @since 2019-01-26
     */
    public static MyChannel getAFreeChannel() {
        
        return channelList.stream()
                .filter(MyChannel::isFree)
                .findAny().orElse(null);
    }
    
    /**
     * <br>随机取一个通道
     * @return
     * @author YellowTail
     * @since 2019-02-01
     */
    public static MyChannel getARandomChannel() {
        
        Random random = new Random();
        int index = Math.abs(random.nextInt()) % 10;
        
        return channelList.get(index);
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
    private static void initChannel(Address address, int num) throws UnknownHostException, IOException {
        LOGGER.info("start init {} connections to {}", num, address);
        
        for (int i = 0; i < num; i++) {
            channelList.add(initMyChannel(address.getIp(), address.getPort()));
        }
    }
    
    private static MyChannel initMyChannel(String ip, int port) throws IOException {
        
        try {
            AsynchronousSocketChannel asc = AsynchronousSocketChannel.open(group);
            
            Future<Void> connect = asc.connect(new InetSocketAddress(ip, port));
            
            //阻塞，直到连接成功
            connect.get();
            
            MyChannel myChannel = new MyChannel();
            myChannel.setChannel(asc);
            myChannel.setUsed(false);
            
            return myChannel;
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("connecte to server occur errors, ", e);
        }
        
        return null;
    }
    
    public void heartBeat() {
        //TODO: 心跳
    }
    
//    public class Allocation implements Runnable {
//
//        @Override
//        public void run() {
//            
//            LOGGER.info("SocketPoolService start run");
//            
//            for(int index = 0;;) {
//                if (0 == index % totalConNum) {
//                    index = 0;
//                }
//                
//                MySocket mySocket = socketList.get(index);
//                if (mySocket.isFree()) {
//                    //get a free server socket
//                    //then get a client
//                    
//                    LOGGER.info("get a free server socket, index {}, now client size is {}", index, clientList.size());
//                    
//                    try {
//                        //this is block func, wait until get value
////                        Socket currentClient = clientList.take();
//                        
//                        LOGGER.info("yyyyyy");
//                        
//                        EXECUTOR_SERVICE.execute( new ConnectionHandlerService(currentClient, mySocket));
//                        
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    
//                }
//                
//                index ++;
//            }
//        }
//        
//    }

    
}
