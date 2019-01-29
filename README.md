# yellow-mongo-proxy
MongoDB Proxy

命令行参数配置

`--deployType=` `jump` (jump server, 跳板机) `lan` (局域网代理)

`--proxyPort=` `port1` 打开的代理端口，支持多个 `--proxyPort=1 --proxyPort=2`
`--dstConNum=` 和目标节点一次性建立的连接数（对于跳板机来说，就是和数据库建立连接，对于局域网代理机器来说，就是和跳板机建立连接）
`--dstAddress=` `ip:port` 远端ip和端口，英文逗号隔开
`--threadsNum=` 线程池数量
