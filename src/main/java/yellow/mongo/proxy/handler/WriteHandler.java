package yellow.mongo.proxy.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteHandler implements CompletionHandler<Integer, ByteBuffer>  {
    
    private final Logger LOGGER = LoggerFactory.getLogger(WriteHandler.class);
    
    private AsynchronousSocketChannel channel;
    
    public WriteHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }
    
    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        LOGGER.info("write complete");
        
        if (attachment.hasRemaining()) {
            //没有写完
            channel.write(attachment, attachment, this);
        } else {
            
            //创建新的Buffer
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            
            //异步读  第三个参数为接收消息回调的业务Handler
            channel.read(readBuffer, readBuffer, new ReadHandler(channel));
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        LOGGER.info("write fail");
    }

}
