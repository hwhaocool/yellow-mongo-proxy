package yellow.mongo.proxy.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProxyService implements Runnable {

    private String ip ;
    
    private int port;
    
    public ProxyService(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    @Override
    public void run() {
        
        
    }
}
