package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApplyFriendModel {

	private Integer friendId;
	private Integer noticeId;
}
