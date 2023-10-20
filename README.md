# redis-reference


## Redis Cluster

### Redis Cluster Steup 

#### Prepare

一、Create a redis configuration template first

> vi redis-conf.tpl

```shell
port ${PORT}
requirepass b1234
masterauth b1234
protected-mode no
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
cluster-announce-ip 192.168.94.6
cluster-announce-port ${PORT}
cluster-announce-bus-port 1${PORT}
appendonly yes
```
二、Create a redis container in docker-compose

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
linux中输入
> chmod +x createRedis.sh && sh createRedis.sh

三、Creating a redis cluster environment

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

计划搭建三主六从集群环境，先指定三台master，正常生产环境应位于不同物理机，否则会影响选举，假如2个主节点存在于一个物理机上的情况。

> docker exec -it redis-4381 redis-cli --cluster create 192.168.11.146:4381 192.168.11.146:4382 192.168.11.146:4383 --cluster-replicas 0 -a b1234

增加slave节点，生产环境中(master与2slave)三台物理机要错开。

四、增加slave节点

docker exec -it redis-4381 bash

redis-cli -a [password] --cluster add-node [old_host:old_port] -a [password] --cluster-slave [old_id]

redis-cli -a b1234 --cluster add-node 192.168.11.146:4384 192.168.11.146:4381 -a b1234 --cluster-slave 9505b1c4ce14a660166fbc972f5de8d02da98dc5

依次吧 4384 - 4389 加入到集群中
> docker exec -it redis-4381 redis-cli -a b1234 --cluster add-node 192.168.11.146:4384 192.168.11.146:4381 -a b1234 --cluster-slave

最后效果
进入 docker 容器中
> docker exec -it redis-4381 redis-cli -c -p 4381 -a b1234

输入 cluster info
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
输入 cluster nodes
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

## Refer to or reading
- [x] [docker-compose搭建redis集群](https://blog.csdn.net/xiongsd/article/details/129356171)