package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.NoticeMapper;
import www.raven.jc.entity.po.Notice;

/**
 * notice dao
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Repository
public class NoticeDAO extends ServiceImpl<NoticeMapper, Notice> {

}
