package cn.xuyinyin.magic.cluster;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : XuJiaWei
 * @since : 2023-10-19 17:01
 */
@Configuration
public class RedisClusterConfig {
    @Resource
    private RedisClusterPropertiesConfig redisClusterPropertiesConfig;

    @Bean(name = "redisClusterTemplate")
    public RedisTemplate<String, Serializable> redisTemplate() {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        final FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        template.setKeySerializer(fastJsonRedisSerializer);
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setConnectionFactory(myLettuceConnectionFactory());
        return template;
    }

    @Bean
    public RedisConnectionFactory myLettuceConnectionFactory() {
        Map<String, Object> source = new HashMap<String, Object>();
        source.put("spring.redis.cluster.nodes", redisClusterPropertiesConfig.getNodes());
        source.put("spring.redis.cluster.max-redirects", redisClusterPropertiesConfig.getMaxRedirects());
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(new MapPropertySource("RedisClusterConfiguration", source));
        redisClusterConfiguration.setPassword(redisClusterPropertiesConfig.getPassword());
        return new LettuceConnectionFactory(redisClusterConfiguration);

    }
}
