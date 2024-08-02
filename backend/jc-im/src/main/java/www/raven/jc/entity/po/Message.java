package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.serializable.CommonSerializable;

/**
 * message
 *
 * @author 刘家辉
 * @date 2023/11/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName(value = "message", schema = "public")
public class Message extends CommonSerializable {

  public static final String REDIS_KEY = "message";

  @TableId(value = "id", type = IdType.AUTO)
  private String id;
  private Integer senderId;
  private String content;
  private String type;
  private String receiverId;
  private Date timestamp;
}
