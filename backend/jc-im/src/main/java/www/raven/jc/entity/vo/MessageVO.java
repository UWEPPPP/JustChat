package www.raven.jc.entity.vo;

import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import www.raven.jc.constant.WsMessageHandlerConstant;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Message;
import www.raven.jc.serializable.CommonSerializable;
import www.raven.jc.util.MessageUtil;

/**
 * message vo
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MessageVO extends CommonSerializable {

  private Date time;
  private String text;
  private UserInfoDTO userInfoDTO;
  private Integer belongId;

  public MessageVO(Message message, UserInfoDTO userInfoDTO) {
    this.time = message.getTimestamp();
    this.text = message.getContent();
    this.userInfoDTO = userInfoDTO;
    if (Objects.equals(message.getType(), WsMessageHandlerConstant.FRIEND)) {
      this.belongId = MessageUtil.resolve(message.getReceiverId(), message.getSenderId());
    } else if (Objects.equals(message.getType(), WsMessageHandlerConstant.ROOM)) {
      this.belongId = Integer.parseInt(message.getReceiverId());
    }

  }

}
