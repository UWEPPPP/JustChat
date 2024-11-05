package www.raven.jc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.serializable.CommonSerializable;

/**
 * user role info
 *
 * @author 刘家辉
 * @date 2023/11/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UserRoleInfo extends CommonSerializable {

	/**
	 * user id
	 */
	private Integer userId;
	/**
	 * user name
	 */
	private String username;
	/**
	 * user password
	 */
	private String password;
	/**
	 * user role
	 */
	private String role;
}
