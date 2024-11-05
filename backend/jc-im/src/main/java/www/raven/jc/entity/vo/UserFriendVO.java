package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user friend vo
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
@Data
@Accessors(chain = true)
public class UserFriendVO {

	private Integer friendId;
	private String friendName;
	private String friendProfile;
	private String lastMsg;
	private String lastMsgSender;
}
