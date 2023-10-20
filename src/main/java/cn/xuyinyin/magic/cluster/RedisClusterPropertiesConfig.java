package cn.xuyinyin.magic.cluster;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author : XuJiaWei
 * @since : 2023-10-19 16:59
 */
@Component
@ConfigurationProperties(prefix = "spring.redis.cluster")
@Data
public class RedisClusterPropertiesConfig {
    //redis集群节点列表
    private String nodes;
    //最大尝试重连数
    private Integer maxRedirects;

    //最大尝试重连数
    @Value("${spring.redis.password}")
    private String password;

}