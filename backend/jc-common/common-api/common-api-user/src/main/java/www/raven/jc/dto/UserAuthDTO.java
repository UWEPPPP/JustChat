package www.raven.jc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.serializable.CommonSerializable;

/**
 * user dto
 *
 * @author 刘家辉
 * @date 2023/12/02
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UserAuthDTO extends CommonSerializable {

	private Integer userId;
	private String username;
	private String password;
}
