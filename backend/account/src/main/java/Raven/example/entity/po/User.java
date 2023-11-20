package Raven.example.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * user
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@TableName
@Data
@Accessors(chain = true)
public class User {
    private Integer id;
    private String username;
    private String password;
    private String profile;
    private String email;
}
