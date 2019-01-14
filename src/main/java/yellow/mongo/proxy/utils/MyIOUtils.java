package yellow.mongo.proxy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 专用的 IO 工具类
 * <br>代码基本是抄的 commons-io 的代码
 * <br>做了一点修改
 * 
 * @author YellowTail
 * @since 2019-01-14
 */
public class MyIOUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MyIOUtils.class);
    
    /**
     * <br>每次读取的字节数
     */
    private static final int DEFAULT_BUFFER_SIZE = 8;

    public static byte[] toByteArray(final InputStream input) throws IOException {
        
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            
            copy(input, output);
            
            return output.toByteArray();
        }
    }
    
    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
    
    public static long copyLarge(final InputStream input, final OutputStream output)
            throws IOException {
        return copy(input, output, 4096);
    }
    
    public static long copy(final InputStream input, final OutputStream output, final int bufferSize)
            throws IOException {
        LOGGER.info("tag 2-1");
        return copyLarge(input, output, new byte[bufferSize]);
    }
    
    public static  long copyLarge(final InputStream input, final OutputStream output, final byte[] buffer)
            throws IOException {
        LOGGER.info("tag 2-2");
        long count = 1;
        int n;
        
        int first = input.read();
        LOGGER.info("fisrt is {}", first);
        
        if (-1 == first) {
            //empty stream
            return 0;
        }
        
        output.write( first);
        
        while (-1 != (n = input.read(buffer))) {
            LOGGER.info("tag 2-3, n is {}", n);
            
            output.write(buffer, 0, n);
            LOGGER.info("tag 2-4");
            
            count += n;
            
            if (n != DEFAULT_BUFFER_SIZE) {
                //这次没有读满，说明流已经结束了，可以退出了，没有必要等着-1
                break;
            }
        }
        return count;
    }
}
