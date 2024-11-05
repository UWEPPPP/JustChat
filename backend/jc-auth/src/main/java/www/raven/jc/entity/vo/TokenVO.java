package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * token vo
 *
 * @author 刘家辉
 * @date 2024/02/24
 */
@Data
@Accessors(chain = true)
public class TokenVO {

	private String token;
	private Long expireTime;
}
