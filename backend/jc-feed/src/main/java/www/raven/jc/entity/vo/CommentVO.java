package www.raven.jc.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Comment;

/**
 * comment vo
 *
 * @author 刘家辉
 * @date 2024/04/21
 */
@Data
@Accessors(chain = true)
public class CommentVO {

  @TableId
  private String id;
  private UserInfoDTO userInfo;
  private String content;
  private Long timestamp;
  private String parentId;

  public CommentVO(Comment comment, UserInfoDTO infoDTO) {
    this.id = comment.getId();
    this.userInfo = infoDTO;
    this.content = comment.getContent();
    this.timestamp = comment.getTimestamp();
    this.parentId = comment.getParentId();
  }
}
