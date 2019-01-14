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
import yellow.mongo.proxy.utils.Helper;

public class ConnectionHandlerService implements Runnable {
    
    private final Logger LOGGER = LoggerFactory.getLogger(ConnectionHandlerService.class);
    
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
                
                //这里其实是 Sock5 协议
                //https://www.ietf.org/rfc/rfc1928.txt
                //https://blog.csdn.net/cszhouwei/article/details/74362427
                
//              byte[] msg = readMessage(client_in);
                
                byte[] msg = toByteArray(client_in);
                
                if (0 != msg.length) {
                    LOGGER.info("msg length is {}", msg.length);
                    LOGGER.info("msg is {}", msg);
                }
                
                if (0 == msg.length) {
                    //输入都没有，把socket关了
                    //注意：可能是心跳
                    LOGGER.info("msg len is zero");
                    
                    client_out.write(msg);
                    continue;
                } else if (4 >= msg.length) {
                    //长度小于4个字节
                    //根据 Sock5 协议，此处返回 05 00，因为我们只支持 无鉴权的模式
                    
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
    
    
    private String getIp(byte[] msg) {
        int ip_1 = Integer.valueOf("" + (msg[4]&0xff) ,16);
        int ip_2 = Integer.valueOf("" + (msg[5]&0xff) ,16);
        int ip_3 = Integer.valueOf("" + (msg[6]&0xff) ,16);
        int ip_4 = Integer.valueOf("" + (msg[7]&0xff) ,16);
        
        return String.format("%d.%d.%d.%d", ip_1, ip_2, ip_3, ip_4);
    }
    
    private int getPort(byte[] msg) {
        return (msg[8] & 0xff) << 8 | (msg[9] & 0xff); 
    }

}
