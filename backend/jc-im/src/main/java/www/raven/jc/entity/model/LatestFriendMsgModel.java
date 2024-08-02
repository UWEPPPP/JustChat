package www.raven.jc.entity.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * latest friend msg model
 *
 * @author 刘家辉
 * @date 2024/02/27
 */
@Data
@Accessors(chain = true)
public class LatestFriendMsgModel {

  @NotNull(message = "friendId不能为空")
  @Min(value = 1, message = "friendId最小为1")
  private Integer friendId;
}
