package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * friend entity
 *
 * @author 刘家辉
 * @date 2023/12/16
 */
@Data
@Accessors(chain = true)
@TableName(value = "friend", schema = "public")
public class Friend {

  @TableId(value = "id", type = IdType.AUTO)
  private Long id;
  private Long userId;
  private Long friendId;
}