package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.FriendMapper;
import www.raven.jc.entity.po.Friend;

/**
 * friend dao
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
@Repository
public class FriendDAO extends ServiceImpl<FriendMapper, Friend> {

}
