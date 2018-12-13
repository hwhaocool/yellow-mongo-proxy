/*
 * The MIT License
 *
 * Copyright 2018 Thibault Debatty.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT,TORT OR OTHERWISE, ARISIFNG FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package yellow.mongo.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import yellow.mongo.proxy.config.Config;

/**
 *
 * @author Thibault Debatty
 */
@Service
public class ProxyServer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(ProxyServer.class);
    
    private static final ExecutorService EXECUTOR_SERVICE = (ExecutorService) Executors.newFixedThreadPool(5);
    
    @Autowired
    private Config config;

    private String mongo_ip = "119.23.235.71";
    private int mongo_port = 27017;

    private final HashMap<String, LinkedList<Listener>> listeners  = new HashMap<>();

    /**
     * Build a mongo proxy, specifying the address of the real mongo server.
     * @param port port on which the proxy will listen.
     * @param mongo_ip IP of the MONGODB database serve.
     * @param mongo_port port of the MONGODB database serve.
     */
    public ProxyServer( final int port, final String mongo_ip, final int mongo_port) {
        this.mongo_ip = mongo_ip;
        this.mongo_port = mongo_port;

    }

    /**
     * Build a mongo proxy using default mongo server (localhost:27017).
     *
     * @param port port on which the proxy will listen.
     */
    public ProxyServer() {
        
    }

    /**
     * Run forever.
     */
    public final void run(String... args) {

        try {
            
            int port = config.getSocketPort();
            
            String mongoUri = config.getMongoClientUri();
            
//            int 

            // Wait for client connection...
            ServerSocket socket = new ServerSocket(port);
            
            logger.info("start port at {}", port);

            while (true) {
                Socket client = socket.accept();
                logger.info("Connected from {}", client.getRemoteSocketAddress());
                
                logger.info("Connected from {}", client.getInetAddress());
                
                EXECUTOR_SERVICE.execute( new ConnectionHandler(client, mongo_ip, mongo_port, listeners));
            }

        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     *
     * @param db name of the database
     * @param collection name of the collection
     * @param listener listener used for notification
     */
    public final void addListener(final String db, final String collection,
            final Listener listener) {

        String collection_request = db + ".$cmd" + collection;

        LinkedList<Listener> collection_listeners
                = listeners.getOrDefault(
                        collection_request, new LinkedList<>());

        collection_listeners.add(listener);
        listeners.put(collection_request, collection_listeners);
    }

}
