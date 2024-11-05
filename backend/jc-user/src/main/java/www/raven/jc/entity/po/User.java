package www.raven.jc.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Date;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@TableName(value = "user", schema = "public")
@Data
@Accessors(chain = true)
public class User {

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
	private String username;
	private String password;
	private String profile;
	private String email;
	private String signature;
	private Date lastOnlineTime;

}
