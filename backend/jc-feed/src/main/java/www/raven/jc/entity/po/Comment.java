package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.serializable.CommonSerializable;

/**
 * comment
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName(value = "comment", schema = "public")
public class Comment extends CommonSerializable {

  @TableId
  private String id;
  private Integer userId;
  private String momentId;
  private String content;
  private Long timestamp;
  private String parentId;
}
