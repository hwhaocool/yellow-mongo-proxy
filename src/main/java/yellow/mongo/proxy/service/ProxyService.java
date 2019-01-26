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
        
        try {
            
            
            int port = config.getSocketPort();
            
            String mongoUri = config.getMongoClientUri();
            

            // Wait for client connection...
            ServerSocket socket = new ServerSocket(port);
            
            Socket server = new Socket(mongo_ip, mongo_port);
            LOGGER.info("server ip {}, port {}", mongo_ip, mongo_port);
            
            LOGGER.info("start port at {}", port);

            while (true) {
                Socket client = socket.accept();
                LOGGER.info("Connected from {}", client.getRemoteSocketAddress());
                
//                logger.info("Connected from {}", client.getInetAddress());
                
                EXECUTOR_SERVICE.execute( new ConnectionHandlerService(client, server));
                
//                EXECUTOR_SERVICE.execute( new ConnectionHandler(client, mongo_ip, mongo_port, listeners));
            }

        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
}
