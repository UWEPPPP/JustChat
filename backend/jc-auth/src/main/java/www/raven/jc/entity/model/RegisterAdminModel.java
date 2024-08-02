package www.raven.jc.entity.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * register admin model
 *
 * @author 刘家辉
 * @date 2023/11/28
 */

@Data
@Accessors(chain = true)
public class RegisterAdminModel {

  @NotBlank(message = "用户名不能为空")
  private String username;
  @NotBlank(message = "密码不能为空")
  private String password;
  @NotBlank(message = "邮箱不能为空")
  private String email;
  @NotBlank(message = "私钥不能为空")
  private String privateKey;
}
