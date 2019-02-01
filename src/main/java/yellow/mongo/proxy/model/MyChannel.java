package yellow.mongo.proxy.model;

import java.nio.channels.AsynchronousSocketChannel;

public class MyChannel {

    /**
     * <br>是否正在被使用
     */
    private boolean isUsed;
    
    /**
     * <br>异步通道
     */
    private AsynchronousSocketChannel channel;
    
    /**
     * <br>是否是空闲的
     * @return
     * @author YellowTail
     * @since 2019-01-26
     */
    public boolean isFree() {
        return ! isUsed;
    }

    public void setUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public void setChannel(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

}
