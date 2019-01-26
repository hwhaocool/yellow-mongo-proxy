package yellow.mongo.proxy.model;

import java.net.Socket;

public class MySocket {

    /**
     * <br>是否正在被使用
     */
    private boolean isUsed;
    
    private Socket socket;

    public boolean isUsed() {
        return isUsed;
    }
    
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    
}
