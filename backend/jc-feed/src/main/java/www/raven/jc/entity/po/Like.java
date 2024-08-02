package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.serializable.CommonSerializable;

/**
 * like
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName(value = "like", schema = "public")
public class Like extends CommonSerializable {

  private String id;
  private Integer userId;
  private String momentId;
  private Long timestamp;
}
