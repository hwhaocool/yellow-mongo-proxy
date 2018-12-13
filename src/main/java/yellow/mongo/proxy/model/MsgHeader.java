package yellow.mongo.proxy.model;

import yellow.mongo.proxy.utils.Helper;

public class MsgHeader {

    private int messageLength;
    
    private int requestID;
    
    private int responseTo;
    
    private int opCode;
    
    public MsgHeader(byte[] msg) {
        
        this.messageLength = Helper.readInt(msg, 0);
        this.requestID = Helper.readInt(msg, 4);
        this.responseTo = Helper.readInt(msg, 8);
        this.opCode = Helper.readInt(msg, 12);
        
    }

    /**
     * @return 得到 messageLength
     */
    public int getMessageLength() {
        return messageLength;
    }

    /**
     * @param messageLength 设置 messageLength
     */
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    /**
     * @return 得到 requestID
     */
    public int getRequestID() {
        return requestID;
    }

    /**
     * @param requestID 设置 requestID
     */
    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    /**
     * @return 得到 responseTo
     */
    public int getResponseTo() {
        return responseTo;
    }

    /**
     * @param responseTo 设置 responseTo
     */
    public void setResponseTo(int responseTo) {
        this.responseTo = responseTo;
    }

    /**
     * @return 得到 opCode
     */
    public int getOpCode() {
        return opCode;
    }

    /**
     * @param opCode 设置 opCode
     */
    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    @Override
    public String toString() {
        return "MsgHeader [messageLength=" + messageLength + ", requestID=" + requestID + ", responseTo=" + responseTo
                + ", opCode=" + opCode + "]";
    }
    
    
}
