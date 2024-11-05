package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * roles
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
@TableName(value = "roles", schema = "public")
@Data
@Accessors(chain = true)
public class Role {

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	private String name;
	private String value;
	private Integer userCount;
}
