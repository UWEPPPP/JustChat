package www.raven.jc.entity.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * friend msg model
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
@Data
@Accessors(chain = true)
public class PagesFriendMsgModel {

	@NotNull(message = "friendId不能为空")
	@Min(value = 1, message = "friendId最小为1")
	private Integer friendId;
	@NotNull(message = "page不能为空")
	@Min(value = 0, message = "page最小为0")
	private Integer page;
	@NotNull(message = "size不能为空")
	@Min(value = 1, message = "size最小为1")
	private Integer size;
}
