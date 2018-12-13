package yellow.mongo.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class Config {

    private Integer socketPort;
    
    private Integer socketCount;
    
    private String  mongoClientUri;

    /**
     * @return 得到 socketPort
     */
    public Integer getSocketPort() {
        return socketPort;
    }

    /**
     * @param socketPort 设置 socketPort
     */
    public void setSocketPort(Integer socketPort) {
        this.socketPort = socketPort;
    }

    /**
     * @return 得到 socketCount
     */
    public Integer getSocketCount() {
        return socketCount;
    }

    /**
     * @param socketCount 设置 socketCount
     */
    public void setSocketCount(Integer socketCount) {
        this.socketCount = socketCount;
    }

    /**
     * @return 得到 mongoClientUri
     */
    public String getMongoClientUri() {
        return mongoClientUri;
    }

    /**
     * @param mongoClientUri 设置 mongoClientUri
     */
    public void setMongoClientUri(String mongoClientUri) {
        this.mongoClientUri = mongoClientUri;
    }
    
    
}
