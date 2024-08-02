package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user room
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@TableName(value = "user_room", schema = "public")
@Data
@Accessors(chain = true)
public class UserRoom {

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
  private Integer userId;
  private Integer roomId;
  private int status;
  private Date lastAckTime;
}
