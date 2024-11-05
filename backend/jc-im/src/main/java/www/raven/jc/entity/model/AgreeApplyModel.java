package www.raven.jc.entity.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AgreeApplyModel {

	private Integer noticeId;
	private Integer userId;
	private Integer roomId;
}
