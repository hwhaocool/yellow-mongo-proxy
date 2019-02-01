package yellow.mongo.proxy.utils;

import java.nio.ByteBuffer;

public class ByteBufferUtils {

    public static void print(ByteBuffer byteBuffer) {
        byteBuffer.flip();
        
        byte[] msg = new byte[byteBuffer.remaining()];
        
        for (byte b : msg) {
            System.out.print(b);
            System.out.print(", ");
        }
        
        System.out.println();
    }
}
