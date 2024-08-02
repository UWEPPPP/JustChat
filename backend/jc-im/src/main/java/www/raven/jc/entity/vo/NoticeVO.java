package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;

/**
 * notice vo
 *
 * @author 刘家辉
 * @date 2023/12/05
 */

@Data
@Accessors(chain = true)
public class NoticeVO {

  private Integer noticeId;
  private String type;
  private String data;
  private Long timestamp;
  private UserInfoDTO sender;
}
