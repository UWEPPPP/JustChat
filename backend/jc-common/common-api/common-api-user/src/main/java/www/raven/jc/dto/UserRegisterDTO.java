package www.raven.jc.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.serializable.CommonSerializable;

/**
 * user register dto
 *
 * @author 刘家辉
 * @date 2023/12/02
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UserRegisterDTO extends CommonSerializable {

  private String username;
  private String password;
  private String email;
  private String profile;
  private List<Integer> roleIds;
}
