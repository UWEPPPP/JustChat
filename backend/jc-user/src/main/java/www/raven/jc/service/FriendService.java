package www.raven.jc.service;

import java.util.List;
import www.raven.jc.dto.UserInfoDTO;

/**
 * friend service
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
public interface FriendService {

  /**
   * get friend infos
   *
   * @param userId user id
   * @return {@link List}<{@link UserInfoDTO}>
   */
  List<UserInfoDTO> getFriendInfos(int userId);

  /**
   * add apply friend
   *
   * @param friendId friend id
   * @param noticeId notice id
   */
  void agreeApplyFromFriend(int friendId, int noticeId);

  /**
   * refused to apply from friend
   *
   * @param noticeId notice id
   */
  void refuseApplyFromFriend(int noticeId);

  /**
   * get friend and me infos
   *
   * @param i i
   * @return {@link List}<{@link UserInfoDTO}>
   */
  List<UserInfoDTO> getFriendAndMeInfos(int i);
}
