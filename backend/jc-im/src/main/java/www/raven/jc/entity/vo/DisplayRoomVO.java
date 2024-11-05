package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * display room vo
 *
 * @author 刘家辉
 * @date 2023/12/11
 */

@Data
@Accessors(chain = true)
public class DisplayRoomVO {

	private Integer roomId;
	private String roomName;
	private String roomDescription;
	private String founderName;
	private Integer maxPeople;
}
