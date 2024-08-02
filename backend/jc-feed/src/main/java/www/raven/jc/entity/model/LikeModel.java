package www.raven.jc.entity.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * like model
 *
 * @author 刘家辉
 * @date 2024/02/21
 */
@Data
@Accessors(chain = true)
public class LikeModel {

  @NotBlank(message = "momentId不能为空")
  private String momentId;
  @NotNull(message = "momentUserId不能为空")
  @Min(value = 1, message = "momentUserId最小为1")
  private Integer momentUserId;
  @NotNull(message = "momentTimeStamp不能为空")
  @Min(value = 1, message = "momentTimeStamp最小为1")
  private Long momentTimeStamp;
}
