package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.ImUserMqConstant;
import www.raven.jc.constant.JwtConstant;
import www.raven.jc.constant.NoticeConstant;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.Notice;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.NoticeService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.RequestUtil;
import www.raven.jc.ws.WebsocketService;

/**
 * notice service impl
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@Service
@Slf4j
public class NoticeServiceImpl implements NoticeService {

  @Autowired
  private HttpServletRequest request;
  @Autowired
  private RedissonClient redissonClient;
  @Autowired
  private NoticeDAO noticeDAO;
  @Autowired
  private UserRpcService userRpcService;

  @Override
  public List<NoticeVO> loadNotice() {
    int userId = RequestUtil.getUserId(request);
    List<Notice> notices = noticeDAO.getBaseMapper()
        .selectList(new QueryWrapper<Notice>().eq("user_id", userId).orderByDesc("timestamp"));
    if (notices.isEmpty()) {
      return new ArrayList<>();
    }
    List<Integer> ids = notices.stream().map(Notice::getSenderId).collect(Collectors.toList());
    RpcResult<List<UserInfoDTO>> usersResult = userRpcService.getBatchInfo(ids);
    Assert.isTrue(usersResult.isSuccess(), "获取用户信息失败");
    Map<Integer, UserInfoDTO> map = usersResult.getData().stream().map(
        user -> new UserInfoDTO().setUserId(user.getUserId()).setUsername(user.getUsername())
            .setProfile(user.getProfile())
    ).collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
    return notices.stream().map(
        notice -> {
          NoticeVO noticeVO = new NoticeVO();
          noticeVO.setNoticeId(notice.getId())
              .setType(notice.getType())
              .setData(notice.getData())
              .setTimestamp(notice.getTimestamp())
              .setSender(map.get(notice.getSenderId()));
          return noticeVO;
        }
    ).collect(Collectors.toList());
  }

  @Transactional(rollbackFor = IllegalArgumentException.class)
  @Override
  public void addFriendApply(String friendName) {
    int applierId = RequestUtil.getUserId(request);

    RpcResult<UserInfoDTO> userResult = userRpcService.getSingleInfoByColumn(friendName);
    Assert.isTrue(userResult.isSuccess(), "用户不存在");
    UserInfoDTO friend = userResult.getData();
    Assert.isNull(noticeDAO.getBaseMapper().selectOne(new QueryWrapper<Notice>().
        eq("user_id", friend.getUserId()).eq("sender_id", applierId).
        eq("type", NoticeConstant.TYPE_ADD_FRIEND_APPLY)), "已经申请过了");
    Notice notice = new Notice().setUserId(friend.getUserId())
        .setData("暂无")
        .setType(NoticeConstant.TYPE_ADD_FRIEND_APPLY)
        .setTimestamp(System.currentTimeMillis())
        .setSenderId(applierId);
    Assert.isTrue(noticeDAO.save(notice), "添加失败");

    RBucket<String> friendBucket = redissonClient.getBucket(JwtConstant.TOKEN + friend.getUserId());
    HashMap<Object, Object> map = new HashMap<>(1);
    map.put("applyId", applierId);
    map.put("type", ImUserMqConstant.TAGS_USER_FRIEND_APPLY);
    if (friendBucket.isExists()) {
      WebsocketService.sendOneMessage(friend.getUserId(), JsonUtil.objToJson(map));
    } else {
      log.info("receiver不在线");
    }
  }

  @Transactional(rollbackFor = IllegalArgumentException.class)
  @Override
  public void deleteNotification(Integer id) {
    Assert.isTrue(noticeDAO.getBaseMapper().deleteById(id) == 1);
  }

}
