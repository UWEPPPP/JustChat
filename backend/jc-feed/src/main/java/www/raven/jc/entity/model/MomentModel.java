package www.raven.jc.entity.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * moment model
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@Data
@Accessors(chain = true)
public class MomentModel {

  @NotBlank(message = "userId不能为空")
  private String text;
  @NotBlank(message = "img不能为空")
  private String img;
}
