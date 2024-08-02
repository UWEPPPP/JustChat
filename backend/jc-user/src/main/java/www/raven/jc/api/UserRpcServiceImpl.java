package www.raven.jc.api;

import java.util.List;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.RoleDTO;
import www.raven.jc.dto.UserAuthDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.dto.UserRegisterDTO;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.FriendService;
import www.raven.jc.service.UserService;

/**
 * user dubbo impl
 *
 * @author 刘家辉
 * @date 2024/01/19
 */
@DubboService(interfaceClass = UserRpcService.class, version = "1.0.0", timeout = 15000)
public class UserRpcServiceImpl implements UserRpcService {

  @Autowired
  private UserService userService;
  @Autowired
  private FriendService friendService;

  @Override
  public RpcResult<UserInfoDTO> getSingleInfo(Integer userId) {
    return RpcResult.operateSuccess("查找成功", userService.querySingleInfo(userId));
  }

  @Override
  public RpcResult<List<UserInfoDTO>> getAllInfo() {
    return RpcResult.operateSuccess("查找成功", userService.queryAllInfo());
  }

  @Override
  public RpcResult<List<UserInfoDTO>> getRelatedInfoList(
      QueryUserInfoDTO userInfoDTO) {
    return RpcResult.operateSuccess("查找成功",
        userService.queryLikedInfoList(userInfoDTO.getColumn(), userInfoDTO.getText()));
  }

  @Override
  public RpcResult<UserAuthDTO> insert(UserRegisterDTO user) {
    return RpcResult.operateSuccess("插入成功", userService.insert(user));
  }

  @Override
  public RpcResult<UserAuthDTO> getUserToAuth(String username) {
    UserAuthDTO auth = userService.queryAuthSingleInfoByColumn("username", username);
    if (auth == null) {
      return RpcResult.operateFailure("无对应账户信息");
    }
    return RpcResult.operateSuccess("查找成功", auth);
  }

  @Override
  public RpcResult<List<RoleDTO>> getRolesById(Integer userId) {
    return RpcResult.operateSuccess("查找成功", userService.queryRolesById(userId));
  }

  @Override
  public RpcResult<Boolean> checkUserExit(String username) {
    return RpcResult.operateSuccess("查找成功", userService.checkUserExit(username));
  }

  @Override
  public RpcResult<List<UserInfoDTO>> getBatchInfo(List<Integer> userIds) {
    return RpcResult.operateSuccess("查找成功", userService.queryBatchInfo(userIds));
  }

  @Override
  public RpcResult<Void> saveLogOutTime(Integer userId) {
    userService.saveTime(userId);
    return RpcResult.operateSuccess("登出成功");
  }

  @Override
  public RpcResult<List<UserInfoDTO>> getFriendInfos(int userId) {
    return RpcResult.operateSuccess("查找成功", friendService.getFriendInfos(userId));
  }

  @Override
  public RpcResult<List<UserInfoDTO>> getFriendAndMeInfos(int i) {
    return RpcResult.operateSuccess("查找成功", friendService.getFriendAndMeInfos(i));
  }

  @Override
  public RpcResult<UserInfoDTO> getSingleInfoByColumn(String name) {
    return RpcResult.operateSuccess("查找成功",
        userService.querySingleInfoByColumn("username", name));
  }
}
