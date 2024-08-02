package www.raven.jc.serializable;

import java.io.Serial;
import java.io.Serializable;

/**
 * common serializable
 *
 * @author Rawven
 * @date 2024/08/02
 */
public class CommonSerializable implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  public CommonSerializable() {
  }

  @Override
  public String toString() {
    return "CommonSerializable{}";
  }
}
