package www.raven.jc.util;

/**
 * mongo util
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
public class MessageUtil {

  private static final int MAX_VALID_FIX_ID_LENGTH = 2;

  public static String concatenateIds(Integer userId, Integer friendId) {
    Integer maxId = Math.max(userId, friendId);
    Integer minId = Math.min(userId, friendId);
    return String.format("%d&%d", minId, maxId);
  }

  public static Integer resolve(String receiverId, int userId) {
    String[] ids = receiverId.split("&");
    if (ids.length != MAX_VALID_FIX_ID_LENGTH) {
      throw new RuntimeException("receiverId is invalid");
    }
    Integer id1 = Integer.parseInt(ids[0]);
    Integer id2 = Integer.parseInt(ids[1]);
    if (id1.equals(id2)) {
      throw new RuntimeException("receiverId is invalid");
    }
    if (id1.equals(userId)) {
      return id2;
    } else {
      return id1;
    }
  }
}
