package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user role
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
@TableName(value = "user_role", schema = "public")
@Data
@Accessors(chain = true)
public class UserRole {

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
  private Integer userId;
  private Integer roleId;
}
