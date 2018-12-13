package yellow.mongo.proxy.model;

import yellow.mongo.proxy.utils.Helper;

public class OpQuery {

    private MsgHeader header;
    
    private int flags;
    
    private String fullCollectionName;
    
    private int numberToSkip;
    
    private int numberToReturn;
    
    private int docLen;
    
    private String collectionName;
    
    public OpQuery(byte[] msg) {
        this.header = new MsgHeader(msg);
        
        this.flags = Helper.readInt(msg, 16);
        
        this.fullCollectionName = Helper.readCString(msg, 20);
        
        int index = 20 + fullCollectionName.length() + 1;
        
        this.numberToSkip = Helper.readInt(msg, index);
        index += 4;
        
        this.numberToReturn = Helper.readInt(msg, index);
        index += 4;
        
        
        //doc lenght, 4
        this.docLen = Helper.readInt(msg, index);
        index += 4;
        
        int valueType = Helper.readByte(msg, index);
        index++;
        
        String queryFieldName = Helper.readCString(msg, index);
        index += queryFieldName.length() + 1;
        
        if ("find".equals(queryFieldName)) {
            
            String xxx = Helper.readCString(msg, index + 1);
            System.out.println(xxx);
        }
        
//        this.numberToSkip = Helper.readInt(msg,16);
    }

    /**
     * @return 得到 header
     */
    public MsgHeader getHeader() {
        return header;
    }

    /**
     * @param header 设置 header
     */
    public void setHeader(MsgHeader header) {
        this.header = header;
    }

    /**
     * @return 得到 flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @param flags 设置 flags
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }

    /**
     * @return 得到 fullCollectionName
     */
    public String getFullCollectionName() {
        return fullCollectionName;
    }

    /**
     * @param fullCollectionName 设置 fullCollectionName
     */
    public void setFullCollectionName(String fullCollectionName) {
        this.fullCollectionName = fullCollectionName;
    }

    /**
     * @return 得到 numberToSkip
     */
    public int getNumberToSkip() {
        return numberToSkip;
    }

    /**
     * @param numberToSkip 设置 numberToSkip
     */
    public void setNumberToSkip(int numberToSkip) {
        this.numberToSkip = numberToSkip;
    }

    /**
     * @return 得到 numberToReturn
     */
    public int getNumberToReturn() {
        return numberToReturn;
    }

    /**
     * @param numberToReturn 设置 numberToReturn
     */
    public void setNumberToReturn(int numberToReturn) {
        this.numberToReturn = numberToReturn;
    }
    
    public boolean isHeartBeat() {
        if ("admin.$cmd".equals(fullCollectionName)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "OpQuery [header=" + header + ", flags=" + flags + ", fullCollectionName=" + fullCollectionName
                + ", numberToSkip=" + numberToSkip + ", numberToReturn=" + numberToReturn + "]";
    }
    
    
}
