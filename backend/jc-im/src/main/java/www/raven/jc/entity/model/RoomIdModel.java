package www.raven.jc.entity.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * room id model
 *
 * @author 刘家辉
 * @date 2023/12/16
 */
@Data
public class RoomIdModel {

  @NotNull(message = "roomId不能为空")
  @Min(value = 1, message = "roomId最小为1")
  private Integer roomId;
}
