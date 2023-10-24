# redis-reference


## Redis Cluster

### Redis Cluster Steup 

#### 一、Create a redis configuration template first

> vi redis-conf.tpl
192.168.11.146 为本机 ip
```shell
port ${PORT}
requirepass b1234
masterauth b1234
protected-mode no
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
cluster-announce-ip 192.168.11.146
cluster-announce-port ${PORT}
cluster-announce-bus-port 1${PORT}
appendonly yes
```
#### 二、Create a redis container in docker-compose

First：
> docker pull redis

Second：
> vi createRedis.sh

```shell
for port in `seq 4381 4389`; do \
  mkdir -p ./${port}/conf \
  && PORT=${port} envsubst < ./redis-conf.tpl > ./${port}/conf/redis.conf \
  && mkdir -p ./${port}/data; \
done
```
> chmod +x createRedis.sh && sh createRedis.sh

#### 三、Creating a redis cluster environment

```yaml
version: "3.8"
services:
   redis-4381:
        image: redis
        container_name: redis-4381
        restart: always
        network_mode: "host"
        volumes:
          - ./4381/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4381/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
   redis-4382:
        image: redis
        container_name: redis-4382
        restart: always
        network_mode: "host"
        volumes:
          - ./4382/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4382/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
   redis-4383:
        image: redis
        container_name: redis-4383
        restart: always
        network_mode: "host"
        volumes:
          - ./4383/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4383/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
   redis-4384:
        image: redis
        container_name: redis-4384
        restart: always
        network_mode: "host"
        volumes:
          - ./4384/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4384/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
   redis-4385:
        image: redis
        container_name: redis-4385
        restart: always
        network_mode: "host"
        volumes:
          - ./4385/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4385/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
   redis-4386:
        image: redis
        container_name: redis-4386
        restart: always
        network_mode: "host"
        volumes:
          - ./4386/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4386/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
   redis-4387:
        image: redis
        container_name: redis-4387
        restart: always
        network_mode: "host"
        volumes:
          - ./4387/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4387/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
   redis-4388:
        image: redis
        container_name: redis-4388
        restart: always
        network_mode: "host"
        volumes:
          - ./4388/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4388/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
   redis-4389:
        image: redis
        container_name: redis-4389
        restart: always
        network_mode: "host"
        volumes:
          - ./4389/conf/redis.conf:/usr/local/etc/redis/redis.conf
          - ./4389/data:/data
        command: redis-server /usr/local/etc/redis/redis.conf
```
> docker-compose up -d

Plan to build a three-master and six-slave cluster environment, first specify three masters, 
the normal production environment should be located on different physical machines, otherwise it will affect the election, if the two master nodes exist on the same physical

> docker exec -it redis-4381 redis-cli --cluster create 192.168.11.146:4381 192.168.11.146:4382 192.168.11.146:4383 --cluster-replicas 0 -a b1234

If a slave node is added, the three physical machines (master and 2slave) in the production environment must be staggered.

#### 四、add slave node

docker exec -it redis-4381 bash

redis-cli -a [password] --cluster add-node [old_host:old_port] -a [password] --cluster-slave [old_id]

redis-cli -a b1234 --cluster add-node 192.168.11.146:4384 192.168.11.146:4381 -a b1234 --cluster-slave 9505b1c4ce14a660166fbc972f5de8d02da98dc5

Add 4384-4389 to the cluster one by one

> docker exec -it redis-4381 redis-cli -a b1234 --cluster add-node 192.168.11.146:4384 192.168.11.146:4381 -a b1234 --cluster-slave

#### 五、Viewing Cluster Information
> docker exec -it redis-4381 redis-cli -c -p 4381 -a b1234

> cluster info
```shell
127.0.0.1:4381> cluster info
cluster_state:ok
cluster_slots_assigned:16384
cluster_slots_ok:16384
cluster_slots_pfail:0
cluster_slots_fail:0
cluster_known_nodes:9
cluster_size:3
cluster_current_epoch:3
cluster_my_epoch:1
cluster_stats_messages_ping_sent:1186
cluster_stats_messages_pong_sent:1208
cluster_stats_messages_sent:2394
cluster_stats_messages_ping_received:1201
cluster_stats_messages_pong_received:1186
cluster_stats_messages_meet_received:7
cluster_stats_messages_received:2394
```
> cluster nodes
```shell
192.168.11.146:4382> cluster nodes
a5dfd3f93cbfff6886ddd11d96fae59341e19a3a 192.168.11.146:4384@14384 slave d4304353d07f00324bfab160ca907f065110aaf0 0 1697769161000 1 connected
cc74c4a5f862b60349f73657090c8a7127d5e1cd 192.168.11.146:4386@14386 slave ce8f0b5491b13d30c1e081b8f2d7a472f65143db 0 1697769161242 3 connected
f6b16e14f02eb6ccd843299d465e92ff7f636cbe 192.168.11.146:4387@14387 slave d4304353d07f00324bfab160ca907f065110aaf0 0 1697769162247 1 connected
968a30d49331e9890e795c94b82b4cd9f366d952 192.168.11.146:4388@14388 slave 82f443aa94896593591f70ab38f52a5846f31342 0 1697769161000 2 connected
ce8f0b5491b13d30c1e081b8f2d7a472f65143db 192.168.11.146:4383@14383 master - 0 1697769162549 3 connected 10923-16383
016c452b5bb57686ee37a22d997aac88bc3137bc 192.168.11.146:4389@14389 slave ce8f0b5491b13d30c1e081b8f2d7a472f65143db 0 1697769162750 3 connected
43cdedb07f7dd524d458610a95d368a3b8788014 192.168.11.146:4385@14385 slave 82f443aa94896593591f70ab38f52a5846f31342 0 1697769161000 2 connected
d4304353d07f00324bfab160ca907f065110aaf0 192.168.11.146:4381@14381 master - 0 1697769160537 1 connected 0-5460
82f443aa94896593591f70ab38f52a5846f31342 192.168.11.146:4382@14382 myself,master - 0 1697769161000 2 connected 5461-10922
```

## Redis Sentinel

创建一个目录，用于存放Docker Compose文件和Redis配置文件。

在该目录中创建一个名为**docker-compose.yml**的文件，并添加以下内容：
```yaml
version: '3'

services:
  redis-master:
    image: redis
    command: redis-server --requirepass b1234
    ports:
      - "6379:6379"
    volumes:
      - ./redis-master:/data
    networks:
      - redis-net

  redis-slave2:
    image: redis
    command: redis-server --slaveof 192.168.11.146 6379 --port 6380 --requirepass b1234 --masterauth b1234  --appendonly yes
    ports:
      - "6380:6380"
    volumes:
      - ./redis-slave:/data
    networks:
      - redis-net

  redis-slave3:
    image: redis
    command: redis-server --slaveof 192.168.11.146 6379 --port 6381 --requirepass b1234 --masterauth b1234  --appendonly yes
    ports:
      - "6381:6381"
    volumes:
      - ./redis-slave:/data
    networks:
      - redis-net

  redis-sentinel:
    image: redis
    command: redis-sentinel /data/sentinel.conf
    volumes:
      - ./redis-sentinel:/data
    ports:
      - "26179:26379"
    networks:
      - redis-net

  redis-sentinel2:
    image: redis
    command: redis-sentinel /data/sentinel.conf
    volumes:
      - ./redis-sentinel2:/data
    ports:
      - "26279:26379"
    networks:
      - redis-net

  redis-sentinel3:
    image: redis
    command: redis-sentinel /data/sentinel.conf
    volumes:
      - ./redis-sentinel3:/data
    ports:
      - "26379:26379"
    networks:
      - redis-net
networks:
  redis-net:

```


networks:
redis-net:
该文件定义了6个服务：redis-master、redis-slave和redis-sentinel。其中，redis-master服务是主Redis服务器，redis-slave服务是从Redis服务器，redis-sentinel服务是哨兵。

在该目录中创建一个名为**redis-master/redis.conf**的文件，并添加以下内容：
```
bind 0.0.0.0
protected-mode no
requirepass b1234
```

该文件配置了主Redis服务器的IP地址、密码等参数。

在该目录中创建一个名为**redis-slave/redis.conf**的文件，并添加以下内容：
```
bind 0.0.0.0
protected-mode no
requirepass b1234
slaveof 192.168.11.146 6379
masterauth b123
```
该文件配置了从Redis服务器的IP地址、密码等参数，以及将其指向主Redis服务器。

在该目录中创建一个名为**redis-sentinel/sentinel.conf**的文件，并添加以下内容：
```
bind 0.0.0.0
protected-mode no
sentinel monitor mymaster 192.168.11.146 6379 2
sentinel auth-pass mymaster b1234
sentinel down-after-milliseconds mymaster 5000
sentinel failover-timeout mymaster 60000
```
该文件配置了哨兵的IP地址、密码等参数，以及将其指向主Redis服务器。

复制俩份sentinel文件
> cp redis-sentinel/ redis-sentinel2/
> 
> cp redis-sentinel/ redis-sentinel3/

最终得到
>-rw-rw-r-- 1 xxt xxt 1341 10月 24 17:42 docker-compose.yml
drwxrwxr-x 2 999 xxt 4096 10月 24 17:43 redis-master/
drwxrwxr-x 2 xxt xxt 4096 10月 24 17:43 redis-sentinel/
drwxrwxr-x 2 xxt xxt 4096 10月 24 17:42 redis-sentinel2/
drwxrwxr-x 2 xxt xxt 4096 10月 24 17:43 redis-sentinel3/
drwxrwxr-x 2 999 xxt 4096 10月 24 17:43 redis-slave/

在终端中进入该目录，并运行以下命令启动Docker容器：

>docker-compose up -d

检查哨兵的运行状态。使用命令**docker exec -it <container_id> redis-cli -p 26379 sentinel master mymaster**可以查看主Redis服务器的信息。

进行故障转移测试。停止主Redis服务器，观察哨兵是否能够自动将从Redis服务器升级为新的主服务器。

## Refer to or reading
- [x] [docker-compose搭建redis集群](https://blog.csdn.net/xiongsd/article/details/129356171)
- [x] [docker-compose一键部署redis一主二从三哨兵模式(含密码,数据持久化)](https://www.zbaiquan.cn/archives/docker-compose%E4%B8%80%E9%94%AE%E9%83%A8%E7%BD%B2redis%E4%B8%80%E4%B8%BB%E4%BA%8C%E4%BB%8E%E4%B8%89%E5%93%A8%E5%85%B5%E6%A8%A1%E5%BC%8F%E5%90%AB%E5%AF%86%E7%A0%81%E6%95%B0%E6%8D%AE%E6%8C%81%E4%B9%85%E5%8C%96)
