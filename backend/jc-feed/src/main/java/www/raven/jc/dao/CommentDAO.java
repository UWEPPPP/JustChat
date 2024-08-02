package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.CommentMapper;
import www.raven.jc.entity.po.Comment;

/**
 * comment dao
 *
 * @author 刘家辉
 * @date 2024/04/21
 */
@Repository
public class CommentDAO extends ServiceImpl<CommentMapper, Comment> {

}
