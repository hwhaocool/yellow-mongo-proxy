package yellow.mongo.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import yellow.mongo.proxy.config.Config;
import yellow.mongo.proxy.constant.ProxyConstant;
import yellow.mongo.proxy.model.Address;
import yellow.mongo.proxy.model.ProxyConfig;
import yellow.mongo.proxy.service.ConnectionHandlerService;
import yellow.mongo.proxy.service.SocketPoolService;


//@Service
public class ProxyServer implements ApplicationRunner {
    //CommandLineRunner

    private final Logger LOGGER = LoggerFactory.getLogger(ProxyServer.class);
    
//    @Autowired
//    private Config config;
    
    private ProxyConfig config;
    
    private SocketPoolService socketPoolService;

//    private String mongo_ip = "119.23.235.71";
//    private int mongo_port = 27017;


    public ProxyServer() {
        
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        
        handleArgs(args);
        
        //远程链接
        List<Address> addressList = config.getAddressList();
        if (CollectionUtils.isEmpty(addressList)) {
            throw new IllegalArgumentException("addressList is empty");
        }
        
        socketPoolService = new SocketPoolService(addressList, config.getDstConNum(), config.getThreadsNum());
        socketPoolService.connection();
        
        //开启监听端口
        List<Integer> proxyPortList = config.getProxyPortList();
        
        for (Integer port : proxyPortList) {
            new Thread( new ProxyService(port) ).run();
        }
    }
    
    private void handleArgs(ApplicationArguments args) {
        LOGGER.info("ProxyServer started with command-line arguments  {}", Arrays.toString(args.getSourceArgs()));
        LOGGER.info("OptionNames {}", args.getOptionNames());
        
        this.config = new ProxyConfig();

        for (String name : args.getOptionNames()){
            List<String> optionValues = args.getOptionValues(name);
            
            if (CollectionUtils.isEmpty(optionValues)) {
                continue;
            }
            
            LOGGER.info("arg- {} = {}",   name , optionValues);
            
            switch (name) {
            case ProxyConstant.ArgName.DST_ADDRESS:
                config.setAddressList(optionValues);
                break;
            case ProxyConstant.ArgName.DST_CON_NUM:
                config.setDstConNum(Integer.parseInt(optionValues.get(0)));
                break;
            case ProxyConstant.ArgName.THREADS_NUM:
                config.setThreadsNum(Integer.parseInt(optionValues.get(0)));
                break;
            case ProxyConstant.ArgName.PROXY_PORT:
                config.setProxyPortList(optionValues);
                break;

            default:
                break;
            }
        }
        
        LOGGER.info("config is {}", config);
    }
    
    public class ProxyService implements Runnable {
        
        private int listenPort;
        
        public ProxyService(int port) {
            this.listenPort = port;
        }

        @Override
        public void run() {
            
            
            try {

                // Wait for client connection...
                ServerSocket socket = new ServerSocket(listenPort);
                
                LOGGER.info("start listen port at {}", listenPort);

                while (true) {
                    Socket client = socket.accept();
                    LOGGER.info("Connected from {}", client.getRemoteSocketAddress());
                    LOGGER.info("Connected from {}", client.getInetAddress());
                    
                    socketPoolService.proxy(client);
                }

            } catch (IOException ex) {
                LOGGER.error(ex.getMessage());
            }
        }
        
    }

}
