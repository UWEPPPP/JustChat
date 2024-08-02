package www.raven.jc.event.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * moment notice event
 *
 * @author 刘家辉
 * @date 2024/02/06
 */
@Data
@Accessors(chain = true)
public class MomentNoticeEvent {

  private String momentId;
  private Integer userId;
  private String msg;
}
