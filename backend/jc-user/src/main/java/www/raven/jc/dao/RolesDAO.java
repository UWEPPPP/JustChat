package www.raven.jc.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;
import www.raven.jc.dao.mapper.RolesMapper;
import www.raven.jc.entity.po.Role;

/**
 * roles dao
 *
 * @author 刘家辉
 * @date 2023/11/30
 */

@Repository
public class RolesDAO extends ServiceImpl<RolesMapper, Role> {

}
