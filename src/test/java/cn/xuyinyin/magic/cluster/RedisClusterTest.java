package cn.xuyinyin.magic.cluster;

import io.lettuce.core.codec.CRC16;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author : XuJiaWei
 * @since : 2023-10-19 17:07
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisClusterTest {

    @Autowired
    @Qualifier("redisClusterTemplate")
    private RedisTemplate redisClusterTemplate;


    @Test
    public void test1() {
        System.out.println("Hello world!");
        // output： Hello world!
    }

    @Test
    public void checkRedisCluster() {
        String key = "666";
        // 设置一个 key， 对应的 value 为 hello world
        redisClusterTemplate.opsForValue().set(key, "hello world");

        // 获取一个 key 的值
        String value = (String) redisClusterTemplate.opsForValue().get(key);

        // 模拟计算 key 存放的槽位， 使用的算法为 CRC16 算法
        final int slotNum = CRC16.crc16(key.getBytes()) % 16384;

        System.out.println("key: " + key + " value: " + value + " slot: " + slotNum);
        // output： key: 666 value: hello world slot: 3747
    }
}