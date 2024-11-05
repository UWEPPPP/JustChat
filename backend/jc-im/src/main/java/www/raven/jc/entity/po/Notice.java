package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * notification
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@TableName(value = "notice", schema = "public")
@Data
@Accessors(chain = true)
public class Notice {

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	private Integer userId;
	private String type;
	private String data;
	private Integer senderId;
	private Long timestamp;
}