package www.raven.jc.service;

import java.util.List;
import www.raven.jc.dto.RoleDTO;
import www.raven.jc.dto.UserAuthDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.dto.UserRegisterDTO;

/**
 * info service
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public interface UserService {

  /**
   * update by column update by column
   *
   * @param column column
   * @param value  value
   * @param id     id
   */
  void updateByColumn(Integer id, String column, String value);

  /**
   * query single info
   *
   * @param userId user id
   * @return {@link UserInfoDTO}
   */
  UserInfoDTO querySingleInfo(Integer userId);

  /**
   * query all info
   *
   * @return {@link List}<{@link UserInfoDTO}>
   */
  List<UserInfoDTO> queryAllInfo();

  /**
   * query liked info list query liked info list
   *
   * @param text   text
   * @param column column
   * @return {@link List}<{@link UserInfoDTO}>
   */
  List<UserInfoDTO> queryLikedInfoList(String column, String text);

  /**
   * query single info by column
   *
   * @param column column
   * @param value  value
   * @return {@link UserAuthDTO}
   */
  UserAuthDTO queryAuthSingleInfoByColumn(String column, String value);

  /**
   * query single info by column
   *
   * @param column column
   * @param value  value
   * @return {@link UserAuthDTO}
   */
  UserInfoDTO querySingleInfoByColumn(String column, String value);

  /**
   * query roles by id
   *
   * @param userId user id
   * @return {@link List}<{@link RoleDTO}>
   */
  List<RoleDTO> queryRolesById(Integer userId);

  /**
   * insert
   *
   * @param user user
   * @return {@link UserAuthDTO}
   */
  UserAuthDTO insert(UserRegisterDTO user);

  /**
   * check user exit
   *
   * @param username username
   * @return {@link Boolean}
   */
  Boolean checkUserExit(String username);

  /**
   * query batch info
   *
   * @param userIds user ids
   * @return {@link List}<{@link UserInfoDTO}>
   */
  List<UserInfoDTO> queryBatchInfo(List<Integer> userIds);

  /**
   * save time
   *
   * @param userId user id
   */
  void saveTime(Integer userId);
}
