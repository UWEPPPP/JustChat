package www.raven.jc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.serializable.CommonSerializable;

/**
 * user info vo
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class UserInfoDTO extends CommonSerializable {

	private Integer userId;
	private String username;
	private String profile;
}
