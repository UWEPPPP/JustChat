package www.raven.jc.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import www.raven.jc.serializable.CommonSerializable;

/**
 * query user info dto
 *
 * @author 刘家辉
 * @date 2023/11/25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class QueryUserInfoDTO extends CommonSerializable {

	private String column;
	private String text;
}
