package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Like;

/**
 * like vo
 *
 * @author 刘家辉
 * @date 2024/04/21
 */
@Data
@Accessors(chain = true)
public class LikeVO {

  private String id;
  private UserInfoDTO userInfo;
  private Long timestamp;

  public LikeVO(Like like, UserInfoDTO userInfoDTO) {
    this.id = like.getId();
    this.userInfo = userInfoDTO;
    this.timestamp = like.getTimestamp();
  }
}
