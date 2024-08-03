收发信息 <br/>逻辑A用户发送消息->IM-Server <br/>
IM-Server将消息临时存于Redis的hash结构中,同时给收到用户的收件箱插入消息ID,并向DB插入消息回执的初始化状态 <br/>
然后通过MQ广播消息在多个IM实例中查找对应在线用户进行消息推送<br/>
最后通过异步将消息持久化到DB中 <br/>
B用户收到信息返回deliver_ack->IM-Server <br/>
IM-Server确定该ack后从redis中清空其收件箱的对应消息ID<br/>
已读回执逻辑<br/>
接收者确认已读后返回read_ack->IM-Server<br/>
IM-Server更新DB里对应的read_ack数据<br/>
发送者通过轮询拉取已读回执(即IfRead == true)<br/>
拉取成功后删除对应已读回执<br/>
消息持久化<br/>
通过定时任务从redis中批量持久化临时消息至mysql
