package www.raven.jc.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * delete notice event
 *
 * @author 刘家辉
 * @date 2024/02/29
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class DeleteNoticeEvent {

	private int noticeId;
}
