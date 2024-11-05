仿微信项目 参考文章如下：http://www.52im.net/thread-1611-1-1.html     http://www.52im.net/forum.php?mod=viewthread&tid=4564&highlight=%C9%E8%BC%C6     
感谢52IM网站

## 技术栈

| 前端         | 后端                 | 数据库  | 中间件  |
| ------------ | -------------------- | ------- | ------- |
| Vue          | Spring Gateway       | Mysql   | RocketMQ|
| Element Plus | Spring Boot          | Redis   | Nacos   |
|              | Dubbo                | Minio   | XXL-Job |

## 目前功能
(注:前端代码已经滞后最新后端代码，暂无法运行)
1. 登录注册/个人信息CRUD
2. IM功能
   - 群聊/私聊功能
   - 在线/离线消息
   - 消息漫游
   - 消息已读未读
3. 好友功能
4. 通知功能
   - 消息
   - 朋友/群聊申请
   - 朋友圈发布/点赞/评论
5. Feed流-朋友圈功能
   - 发布朋友圈
   - 评论
   - 点赞
   - 回复
## 更新记录
2024-11-05 抽出分布式ID服务,通过自定义负载均衡策略,实现单聊,群聊的消息局部有序性,解决消息时序性问题
