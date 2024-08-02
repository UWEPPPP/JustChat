package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.FriendChatMapper;
import www.raven.jc.entity.po.FriendChat;

/**
 * friend chat dao
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Repository
public class FriendChatDAO extends ServiceImpl<FriendChatMapper, FriendChat> {

}
