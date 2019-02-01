package yellow.mongo.proxy.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yellow.mongo.proxy.handler.ReadHandler;

public class ConnectionHandlerServiceV2 implements CompletionHandler<AsynchronousSocketChannel, Void> {
    
    private final Logger LOGGER = LoggerFactory.getLogger(ConnectionHandlerServiceV2.class);
    
    private AsynchronousServerSocketChannel server;
    
    public void setServer(AsynchronousServerSocketChannel server) {
        this.server = server;
    }

    @Override
    public void completed(AsynchronousSocketChannel result, Void attachment) {
        //客户端 发送数据成功，代码就进入这里
        try {
            LOGGER.info("completed {}", result.getLocalAddress());
        } catch (IOException e) {
            LOGGER.error("getLocalAddress error, ", e);
        }
        
        // 接受下一个连接
        server.accept(null, this);
        
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        
        result.read(byteBuffer, byteBuffer, new ReadHandler(result));
        
    }

    @Override
    public void failed(Throwable exc, Void attachment) {
        // TODO Auto-generated method stub
        LOGGER.info("failed xxx");
        
        
    }

    
}
