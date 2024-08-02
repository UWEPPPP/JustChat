package www.raven.jc.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * join room apply event
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class RoomApplyEvent {

  private Integer applyId;
  private Integer founderId;
  private Integer roomId;
}
