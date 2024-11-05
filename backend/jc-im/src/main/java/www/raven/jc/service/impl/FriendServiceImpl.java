package www.raven.jc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.PagesFriendMsgModel;
import www.raven.jc.entity.po.FriendChat;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.entity.vo.UserFriendVO;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.FriendService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MessageUtil;
import www.raven.jc.util.RequestUtil;

/**
 * friend service impl
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
@Service
@Slf4j
public class FriendServiceImpl implements FriendService {

	@Resource
	private HttpServletRequest request;
	@Resource
	private FriendChatDAO friendChatDAO;
	@Resource
	private MessageDAO messageDAO;
	@Resource
	private UserRpcService userRpcService;

	@Override
	public List<UserFriendVO> initUserFriendPage() {
		int userId = RequestUtil.getUserId(request);
		//获得好友id
		List<UserInfoDTO> friends = userRpcService.getFriendInfos(userId).getData();
		if (friends.isEmpty()) {
			return new ArrayList<>();
		}
		List<Integer> ids = friends.stream().map(UserInfoDTO::getUserId).toList();
		List<String> fixedFriendIds = new ArrayList<>();
		for (Integer friendId : ids) {
			fixedFriendIds.add(MessageUtil.concatenateIds(userId, friendId));
		}
		List<FriendChat> friendChats = friendChatDAO.getBaseMapper()
				.selectList(new QueryWrapper<FriendChat>().in("fix_id", fixedFriendIds));
		//获取好友的最后一条消息id
		List<String> idsMsg = friendChats.stream().map(FriendChat::getLastMsgId)
				.collect(Collectors.toList());
		//获取好友的最后一条消息
		List<Message> messages = idsMsg.isEmpty() ? new ArrayList<>()
				: messageDAO.getBaseMapper().selectList(new QueryWrapper<Message>().in("id", idsMsg));

		//将好友id和好友的最后一条消息id对应起来
		Map<Integer, Message> messageMap = messages.stream()
				.collect(Collectors.toMap(
						message -> message.getSenderId().equals(userId) ? MessageUtil.resolve(
								message.getReceiverId(), userId) : message.getSenderId(),
						Function.identity(),
						(oldValue, newValue) -> newValue
				));
		return friends.stream().map(friend -> {
			Message message = messageMap.get(friend.getUserId());
			return new UserFriendVO()
					.setFriendId(friend.getUserId())
					.setFriendName(friend.getUsername())
					.setFriendProfile(friend.getProfile())
					.setLastMsg(message == null ? "" : JsonUtil.objToJson(message))
					.setLastMsgSender(message == null ? ""
							: message.getSenderId().equals(userId) ? "我" : friend.getUsername());
		}).collect(Collectors.toList());
	}

	@Override
	public List<MessageVO> getFriendMsgPages(PagesFriendMsgModel model) {
		int userId = RequestUtil.getUserId(request);
		String fixId = MessageUtil.concatenateIds(userId, model.getFriendId());
		Page<Message> messagePage = messageDAO.getBaseMapper()
				.selectPage(new Page<>(model.getPage(), model.getSize()),
						new QueryWrapper<Message>().eq("fix_id", fixId).orderByDesc("timestamp")
								.last("limit 10"));
		RpcResult<List<UserInfoDTO>> batchInfo = userRpcService.getBatchInfo(
				List.of(model.getFriendId(), userId));
		Map<Integer, UserInfoDTO> map = batchInfo.getData().stream()
				.collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
		return messagePage.getRecords().stream().map(message -> {
			UserInfoDTO user = map.get(message.getSenderId());
			return new MessageVO(message, user);
		}).collect(Collectors.toList());
	}

}
