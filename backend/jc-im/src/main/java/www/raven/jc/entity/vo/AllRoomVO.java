package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * all room vo
 *
 * @author 刘家辉
 * @date 2023/11/29
 */

@Data
@Accessors(chain = true)
public class AllRoomVO {

  private Integer roomId;
  private String founder;
  private Integer maxPeople;
  private String description;
}
