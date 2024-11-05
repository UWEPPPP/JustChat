package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * room vo
 *
 * @author 刘家辉
 * @date 2023/11/24
 */

@Data
@Accessors(chain = true)
public class UserRoomVO {

	private Integer roomId;
	private String roomName;
	/**
	 * 用founder profile 代替
	 */
	private String roomProfile;
	private String lastMsg;
	private String lastMsgSender;
}
