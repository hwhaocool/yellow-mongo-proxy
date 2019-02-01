package yellow.mongo.proxy.handler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ServerWriteHandler implements CompletionHandler<Integer, ByteBuffer>  {
    
    private AsynchronousSocketChannel clientChannel;
    
    private AsynchronousSocketChannel serverChannel;
    
    public ServerWriteHandler(AsynchronousSocketChannel clientChannel, AsynchronousSocketChannel serverChannel) {
        this.clientChannel = clientChannel;
        this.serverChannel = serverChannel;
    }
    

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        // TODO Auto-generated method stub
        
    }

}
