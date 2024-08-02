package www.raven.jc.ws;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * ws msg
 *
 * @author 刘家辉
 * @date 2024/04/22
 */
@Data
@Accessors(chain = true)
public class WsMsg {

  private String message;
  private List<Integer> to;
}
