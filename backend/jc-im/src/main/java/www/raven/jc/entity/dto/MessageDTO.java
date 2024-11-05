package www.raven.jc.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * message dto
 *
 * @author 刘家辉
 * @date 2023/11/24
 */
@Data
@Accessors(chain = true)
public class MessageDTO {
	private String text;
	private Integer userId;
	private Integer belongId;
	private String type;
}
