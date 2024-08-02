package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.RoomMapper;
import www.raven.jc.entity.po.Room;

/**
 * @author 刘家辉
 * @date 2023/12/01
 */
@Repository
public class RoomDAO extends ServiceImpl<RoomMapper, Room> {

}
