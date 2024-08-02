package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.MessageReadAckMapper;
import www.raven.jc.entity.po.MessageReadAck;

@Repository
public class MessageReadAckDAO extends ServiceImpl<MessageReadAckMapper, MessageReadAck> {

}
