Note
---

## 1. AIO 的client 连不上
报错为 java.io.IOException: 指定的网络名不再可用

## 2. AIO 的 server 收不到 客户端的 第二次 请求
使用BIO client 发送第一次请求，可以收到
但是发送第二次的时候，就收不到

`原因` server 用的不是 异步方式，用的是阻塞(future.get) ，导致后续请求来的时候，操作系统不知道该通知谁

