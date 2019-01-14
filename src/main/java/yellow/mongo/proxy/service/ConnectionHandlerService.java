package yellow.mongo.proxy.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yellow.mongo.proxy.Listener;

public class ConnectionHandlerService implements Runnable {
    
    private final Logger LOGGER = LoggerFactory.getLogger(ConnectionHandlerService.class);
    
    private static final int DEFAULT_BUFFER_SIZE = 8;
    
    private final Socket client;
//  private final Socket server;
    
    public ConnectionHandlerService (
            final Socket client, final Socket server) {
        this.client = client;
//        this.server = server;
    }

    @Override
    public void run() {
        LOGGER.info("ConnectionHandlerService start run");
        
        try {
            InputStream client_in = client.getInputStream();
            OutputStream client_out = client.getOutputStream();

            // of server
            Socket server = new Socket("119.23.235.71", 27017);
            OutputStream srv_out = server.getOutputStream();
            InputStream srv_in = server.getInputStream();

            while (true) {
                
//              ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf)
                
                if (client.isClosed()) {
                    LOGGER.info("client is close");
                }
                
                LOGGER.info("start copy");
                
                //对于 3T 来说
                // 发送一个4字节的  5 2 0 2
                //需要返回2字节 5 0
                //猜测：开启 proxy 模式
                
                //send(10) 5 1 0 1 77(119) 17(23) eb(235) 47(71) 69(105) 89(137)
                //back(10) 5 0 0 1 0 0 0 0 0 0
                //猜测：请把请求代理到 119.23.235.71 的 27017(16进制69 89 ) 处
                
                //http://www.haproxy.org/download/1.8/doc/proxy-protocol.txt
                
//              byte[] msg = readMessage(client_in);
                
                byte[] msg = toByteArray(client_in);
                
                if (0 != msg.length) {
                    LOGGER.info("msg length is {}", msg.length);
                    LOGGER.info("msg is {}", msg);
                }
                
                if (0 == msg.length) {
                    //输入都没有，把socket关了
                    LOGGER.info("msg len is zero");
                    
                    client_out.write(msg);
                    continue;
                } else if (4 == msg.length) {
                    
                    client_out.write(new  byte[] {5, 0});
                    continue;
                } else if (10 == msg.length) {
                    client_out.write(new  byte[] {5, 0, 0, 1, 0, 0, 0, 0, 0, 0});
                    continue;
                }
                
                //redirect byte to server
                srv_out.write(msg);
                
                //read the data from server return
                
//                if (msg.length <= 12) {
//                  client_out.write(msg);
//              } else {
                    LOGGER.info("start copy2");
                    byte[] response = toByteArray(srv_in);
                    
//                  byte[] response = readMessage(srv_in);
                    
                    LOGGER.info("response length is {}", response.length);
                    LOGGER.info("response is {}", response);
                    
                    client_out.write(response);
//              }
                
            }
        } catch (Exception ex) {
            LOGGER.error("error, ", ex);
        }
    }
    
    public byte[] readMessage(
            final InputStream stream) throws IOException, Exception {
        if (stream == null) {
            throw new Exception("Stream is null!");
        }
        // https://docs.mongodb.com/manual/reference/mongodb-wire-protocol/
        // Header =
        // int32 = 4 Bytes = 32 bits
        // 1. length of message
        int lentgh_1 = stream.read();
        int lentgh_2 = stream.read();
        int lentgh_3 = stream.read();
        int lentgh_4 = stream.read();
        
        // Value is little endian:
        final int msg_length = lentgh_1
                + lentgh_2 * 256 + lentgh_3 * 256 * 256
                + lentgh_4 * 256 * 256 * 256;
        
        LOGGER.info("length is {}", msg_length);
        
        
        // 2. content of message
        byte[] msg = new byte[msg_length];
        int offset = 4;
        while (offset < msg_length) {
            //read the stream and skip the 4 first bytes
            int tmp = stream.read(msg, offset, msg_length - offset);
            
            LOGGER.info("next temp is {}", tmp);
            
            offset += tmp;
        }
        // 3. Fill 4 first Bytes
        msg[0] = (byte) lentgh_1;
        msg[1] = (byte) lentgh_2;
        msg[2] = (byte) lentgh_3;
        msg[3] = (byte) lentgh_4;
        return msg;
    }
    
    public byte[] toByteArray(final InputStream input) throws IOException {
        
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            
            copy(input, output);
            
            return output.toByteArray();
        }
    }
    
    public int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
    
    public long copyLarge(final InputStream input, final OutputStream output)
            throws IOException {
        return copy(input, output, 4096);
    }
    
    public long copy(final InputStream input, final OutputStream output, final int bufferSize)
            throws IOException {
        LOGGER.info("tag 2-1");
        return copyLarge(input, output, new byte[bufferSize]);
    }
    
    public long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer)
            throws IOException {
        LOGGER.info("tag 2-2");
        long count = 1;
        int n;
        
        int first = input.read();
        LOGGER.info("fisrt is {}", first);
        
        if (-1 == first) {
            //empty stream
            return 0;
        }
        
        output.write( first);
        
        while (-1 != (n = input.read(buffer))) {
            LOGGER.info("tag 2-3, n is {}", n);
            
            output.write(buffer, 0, n);
            LOGGER.info("tag 2-4");
            
            count += n;
            
            if (n != DEFAULT_BUFFER_SIZE) {
                //这次没有读满，说明流已经结束了，可以退出了，没有必要等着-1
                break;
            }
        }
        return count;
    }

}
