package www.raven.jc.api;

import java.util.List;

import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.RoleDTO;
import www.raven.jc.dto.UserAuthDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.dto.UserRegisterDTO;
import www.raven.jc.result.HttpResult;
import www.raven.jc.result.RpcResult;

/**
 * account api
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
public interface UserRpcService {

	/**
	 * get single info
	 *
	 * @param userId user id
	 * @return {@link HttpResult}<{@link UserInfoDTO}>
	 */
	RpcResult<UserInfoDTO> getSingleInfo(Integer userId);

	/**
	 * get all info
	 *
	 * @return {@link HttpResult}<{@link List}<{@link UserInfoDTO}>>
	 */
	RpcResult<List<UserInfoDTO>> getAllInfo();

	/**
	 * get related info list
	 *
	 * @param userInfoDTO user info dto
	 * @return {@link HttpResult}<{@link List}<{@link UserInfoDTO}>>
	 */
	RpcResult<List<UserInfoDTO>> getRelatedInfoList(
			QueryUserInfoDTO userInfoDTO);

	/**
	 * insert
	 *
	 * @param user user
	 * @return {@link HttpResult}<{@link UserAuthDTO}>
	 */
	RpcResult<UserAuthDTO> insert(UserRegisterDTO user);

	/**
	 * get user to auth
	 *
	 * @param username username
	 * @return {@link HttpResult}<{@link UserAuthDTO}>
	 */
	RpcResult<UserAuthDTO> getUserToAuth(String username);

	/**
	 * get roles by id
	 *
	 * @param userId user id
	 * @return {@link HttpResult}<{@link List}<{@link RoleDTO}>>
	 */
	RpcResult<List<RoleDTO>> getRolesById(Integer userId);

	/**
	 * check user exit
	 *
	 * @param username username
	 * @return {@link HttpResult}<{@link Boolean}>
	 */
	RpcResult<Boolean> checkUserExit(String username);

	/**
	 * get batch info
	 *
	 * @param userIds user ids
	 * @return {@link HttpResult}<{@link List}<{@link UserInfoDTO}>>
	 */
	RpcResult<List<UserInfoDTO>> getBatchInfo(List<Integer> userIds);

	/**
	 * user logout
	 *
	 * @param userId user id
	 * @return {@link HttpResult}<{@link Void}>
	 */
	RpcResult<Void> saveLogOutTime(Integer userId);

	/**
	 * get friend ids
	 *
	 * @param userId user id
	 * @return {@link RpcResult}<{@link List}<{@link Integer}>>
	 */
	RpcResult<List<UserInfoDTO>> getFriendInfos(int userId);

	/**
	 * get friend and me infos
	 *
	 * @param i i
	 * @return {@link RpcResult}<{@link List}<{@link UserInfoDTO}>>
	 */
	RpcResult<List<UserInfoDTO>> getFriendAndMeInfos(int i);

	/**
	 * get single info by column
	 *
	 * @param name name
	 * @return {@link RpcResult}<{@link UserInfoDTO}>
	 */
	RpcResult<UserInfoDTO> getSingleInfoByColumn(String name);
}