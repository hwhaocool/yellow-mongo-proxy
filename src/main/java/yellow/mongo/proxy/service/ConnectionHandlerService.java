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
import yellow.mongo.proxy.model.MsgHeader;
import yellow.mongo.proxy.model.MySocket;
import yellow.mongo.proxy.utils.Helper;
import yellow.mongo.proxy.utils.MyIOUtils;

public class ConnectionHandlerService implements Runnable {
    
    private final Logger LOGGER = LoggerFactory.getLogger(ConnectionHandlerService.class);
    
    private final Socket client;
  private final MySocket mySocket;
    
    public ConnectionHandlerService (
            final Socket client, final MySocket mySocket) {
        this.client = client;
        this.mySocket = mySocket;
    }

    @Override
    public void run() {
        LOGGER.info("ConnectionHandlerService start run, temp port is {}", client.getPort());
        
        Socket server = mySocket.getSocket();
        mySocket.setUsed(true);
        
        try {
            InputStream client_in = client.getInputStream();
            OutputStream client_out = client.getOutputStream();

            // of server
//            Socket server = new Socket("119.23.235.71", 27017);
            OutputStream srv_out = server.getOutputStream();
            InputStream srv_in = server.getInputStream();

            while (true) {
                
//              ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf)
                
                if (client.isClosed()) {
                    LOGGER.info("client is close");
                }
                
//                LOGGER.info("start copy");
                
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
                
                byte[] msg = MyIOUtils.toByteArray(client_in);
                
                if (0 != msg.length) {
//                    LOGGER.info("msg length is {}", msg.length);
//                    LOGGER.info("msg is {}", msg);
                }
                
                if (0 == msg.length) {
                    //输入都没有，把socket关了
                    //注意：可能是心跳
//                    LOGGER.info("msg len is zero");
//                    LOGGER.info("heartbeat...");
                    
                    client_out.write(msg);
                    continue;
                } else if (4 >= msg.length) {
                    //长度小于4个字节
                    //此处是 客户端和代理服务器 认证协商过程
                    //根据 Sock5 协议，此处返回 05 00，因为我们只支持 无鉴权的模式
                    
                    LOGGER.info("socks 5 first");
                    
                    client_out.write(new  byte[] {5, 0});
                    continue;
                } else if (10 == msg.length) {
                    //如果是 10个字节，代表此处是 客户端开始发送具体的请求
                    //拿到ip 和端口
                    String ip = getIp(msg);
                    int port = getPort(msg);
                    
                    LOGGER.info("remote ip is {}, port is {}", ip, port);
                    
                    client_out.write(new  byte[] {5, 0, 0, 1, 0, 0, 0, 0, 0, 0});
                    continue;
                }
                
                //redirect byte to server
                srv_out.write(msg);
                
                //read the data from server return
                byte[] response = MyIOUtils.toByteArray(srv_in);
                
                MsgHeader header = new MsgHeader(msg);
                MsgHeader header2 = new MsgHeader(response);
                LOGGER.info("request is {}, reponse is {}", header, header2);
                    
                client_out.write(response);
                
            }
        } catch (Exception ex) {
            LOGGER.error("error, ", ex);
            
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            mySocket.setUsed(false);
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
    
    
    /**
     * <br>得到 IP 地址
     * @param msg
     * @return
     * @author YellowTail
     * @since 2019-01-15
     */
    private String getIp(byte[] msg) {
        int ip_1 = Integer.valueOf("" + (msg[4]&0xff) ,16);
        int ip_2 = Integer.valueOf("" + (msg[5]&0xff) ,16);
        int ip_3 = Integer.valueOf("" + (msg[6]&0xff) ,16);
        int ip_4 = Integer.valueOf("" + (msg[7]&0xff) ,16);
        
        return String.format("%d.%d.%d.%d", ip_1, ip_2, ip_3, ip_4);
    }
    
    /**
     * <br>得到 端口号
     * @param msg
     * @return
     * @author YellowTail
     * @since 2019-01-15
     */
    private int getPort(byte[] msg) {
        return (msg[8] & 0xff) << 8 | (msg[9] & 0xff); 
    }

}
