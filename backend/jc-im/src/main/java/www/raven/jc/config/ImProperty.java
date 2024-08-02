package www.raven.jc.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * mq property
 *
 * @author 刘家辉
 * @date 2024/04/21
 */
@Component
@Data
public class ImProperty {

  @Value("${mq.ws_topic}")
  private String wsTopic;
  @Value("${mq.in_topic}")
  private String inTopic;
}
