package yellow.mongo.proxy.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yellow.mongo.proxy.model.MsgHeader;
import yellow.mongo.proxy.model.MyChannel;
import yellow.mongo.proxy.model.OpCode;
import yellow.mongo.proxy.service.SocketPoolService;
import yellow.mongo.proxy.utils.ByteBufferUtils;

public class ClientReadHandler implements CompletionHandler<Integer, ByteBuffer> {

    private final Logger LOGGER = LoggerFactory.getLogger(ClientReadHandler.class);

    // 用于读取半包消息和发送应答
    private AsynchronousSocketChannel channel;

    public ClientReadHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {

        byteBuffer.flip();

        byte[] msg = new byte[byteBuffer.remaining()];

        byteBuffer.get(msg);

        int readNum = msg.length;

        byte firstByte = msg[0];
        LOGGER.info("firstByte  is {}", firstByte);

        if (4 >= readNum) {
            // 长度小于4个字节
            // 此处是 客户端和代理服务器 认证协商过程
            // 根据 Sock5 协议，此处返回 05 00，因为我们只支持 无鉴权的模式

            ByteBuffer writeBuffer = ByteBuffer.wrap(new byte[] { 5, 0 });

            channel.write(writeBuffer, writeBuffer, new ClientWriteHandler(channel));

        } else if (10 == readNum) {
            // 如果是 10个字节，代表此处是 客户端开始发送具体的请求
            // 拿到ip 和端口

            String ip = getIp(msg);
            int clientUseDstPort = getPort(msg);

            ByteBuffer writeBuffer = ByteBuffer.wrap(new byte[] { 5, 0, 0, 1, 0, 0, 0, 0, 0, 0 });

            LOGGER.info("remote ip is {}, port is {}", ip, clientUseDstPort);

            channel.write(writeBuffer, writeBuffer, new ClientWriteHandler(channel));

        } else {
            // 正常的消息
            MsgHeader header = new MsgHeader(msg);
            int opcode = header.getOpCode();
            LOGGER.info("Opcode {}, header is {}", OpCode.getOpcodeName(opcode), header);
            
            //转发到 服务器，然后得到服务器的返回值，再返回给 客户端
            exchangeMessage(msg);
            
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        LOGGER.info("read fail");

        ByteBufferUtils.print(attachment);
    }
    
    /**
     * <br>把数据转发到 服务器，然后得到服务器的返回值，再返回给 客户端
     * @param msg
     * @author YellowTail
     * @since 2019-02-01
     */
    private void exchangeMessage(byte[] msg) {
        MyChannel myChannel = SocketPoolService.getAFreeChannel();
        
        if (null == myChannel) {
            //没有空闲的，随机挑一个，排队
            myChannel = SocketPoolService.getARandomChannel();
        }
        
        AsynchronousSocketChannel serverChannel = myChannel.getChannel();
        
        ByteBuffer writeBuffer = ByteBuffer.wrap(msg);
        
        //直接写，暂时不关心何时写完
        channel.write(writeBuffer);
        
        channel.write(writeBuffer, writeBuffer, new ServerWriteHandler(channel, serverChannel));
    }

    /**
     * <br>
     * 得到 IP 地址
     * 
     * @param msg
     * @return
     * @author YellowTail
     * @since 2019-01-15
     */
    private String getIp(byte[] msg) {
        int ip_1 = Integer.valueOf("" + (msg[4] & 0xff), 16);
        int ip_2 = Integer.valueOf("" + (msg[5] & 0xff), 16);
        int ip_3 = Integer.valueOf("" + (msg[6] & 0xff), 16);
        int ip_4 = Integer.valueOf("" + (msg[7] & 0xff), 16);

        return String.format("%d.%d.%d.%d", ip_1, ip_2, ip_3, ip_4);
    }

    /**
     * <br>
     * 得到 端口号
     * 
     * @param msg
     * @return
     * @author YellowTail
     * @since 2019-01-15
     */
    private int getPort(byte[] msg) {
        return (msg[8] & 0xff) << 8 | (msg[9] & 0xff);
    }

}
