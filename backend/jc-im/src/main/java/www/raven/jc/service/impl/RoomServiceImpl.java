package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.api.UserRpcService;
import www.raven.jc.constant.ApplyStatusConstant;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.NoticeDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.PageGroupMsgModel;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.Room;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.entity.vo.DisplayRoomVO;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.entity.vo.RealRoomVO;
import www.raven.jc.entity.vo.UserRoomVO;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.MessageService;
import www.raven.jc.service.RoomService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.RequestUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * room service impl
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

	@Autowired
	private RoomDAO roomDAO;
	@Autowired
	private UserRoomDAO userRoomDAO;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private UserRpcService userRpcService;
	@Autowired
	private MessageDAO messageDAO;
	@Autowired
	private RedissonClient redissonClient;
	@Autowired
	private MessageService messageService;
	@Autowired
	private NoticeDAO noticeDAO;

	@Transactional(rollbackFor = IllegalArgumentException.class)
	@Override
	public void createRoom(RoomModel roomModel) {
		Room room = new Room().setRoomName(roomModel.getName())
				.setRoomDescription(roomModel.getDescription())
				.setMaxPeople(roomModel.getMaxPeople()).
				setFounderId(Integer.parseInt(request.getHeader("userId")));
		Assert.isTrue(roomDAO.getBaseMapper().insert(room) > 0, "创建失败");
		Assert.isTrue(userRoomDAO.getBaseMapper().
						insert(new UserRoom().setRoomId(room.getRoomId()).setUserId(room.getFounderId())
								.setStatus(ApplyStatusConstant.APPLY_STATUS_AGREE)) > 0,
				"创建失败");
	}

	@Override
	public List<UserRoomVO> initUserMainPage() {
		//获取用户加入的聊天室id
		int userId = RequestUtil.getUserId(request);
		//获取用户加入的聊天室
		List<Room> rooms = roomDAO.getBaseMapper().selectRoomByUserId(userId);
		if (rooms.isEmpty()) {
			return new ArrayList<>();
		}
		//获取所有聊天室的最后一条消息id
		List<String> idsMsg = rooms.stream()
				.map(Room::getLastMsgId)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		//获取所有聊天室的最后一条消息
		List<Message> messages = idsMsg.isEmpty() ? new ArrayList<>()
				: messageDAO.getBaseMapper().selectList(new QueryWrapper<Message>().in("id", idsMsg));
		//获取聊天室聊天室创建者的用户信息
		Set<Integer> collect = rooms.stream().map(Room::getFounderId).collect(Collectors.toSet());
		messages.stream().map(Message::getSenderId).forEach(collect::add);
		RpcResult<List<UserInfoDTO>> batchInfo = userRpcService.getBatchInfo(collect.stream().toList());
		Assert.isTrue(batchInfo.isSuccess(), "user模块调用失败");
		Map<Integer, UserInfoDTO> userInfoMap = batchInfo.getData().stream()
				.collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
		Map<String, Message> messageMap = messages.stream()
				.collect(Collectors.toMap(Message::getId, Function.identity()));
		return rooms.stream().map(room -> {
			UserRoomVO userRoomVO = new UserRoomVO()
					.setRoomId(room.getRoomId())
					.setRoomName(room.getRoomName())
					.setRoomProfile(userInfoMap.get(room.getFounderId()).getProfile());
			if (room.getLastMsgId() != null) {
				Message message = messageMap.get(room.getLastMsgId());
				userRoomVO.setLastMsg(JsonUtil.objToJson(message));
				userRoomVO.setLastMsgSender(userInfoMap.get(message.getSenderId()).getUsername());
			} else {
				userRoomVO.setLastMsg(""); // 或者一些默认值
				userRoomVO.setLastMsgSender(""); // 或者一些默认值
			}
			return userRoomVO;
		}).collect(Collectors.toList());
	}

	@Override
	public RealRoomVO queryLikedRoomList(String column, String text, int page) {
		Long total = roomDAO.getBaseMapper().selectCount(null);
		Page<Room> chatRoomPage = roomDAO.getBaseMapper()
				.selectPage(new Page<>(page, 5), new QueryWrapper<Room>().like(column, text));
		List<DisplayRoomVO> rooms = buildRoomVO(chatRoomPage, userRpcService.getAllInfo().getData());
		return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
	}

	@Override
	public RealRoomVO queryUserNameRoomList(String column, String text,
	                                        int page) {
		Long total = roomDAO.getBaseMapper().selectCount(null);
		List<UserInfoDTO> queryList = userRpcService.getRelatedInfoList(
				new QueryUserInfoDTO().setColumn(column).setText(text)).getData();
		List<Integer> userIds = queryList.stream().map(UserInfoDTO::getUserId)
				.collect(Collectors.toList());
		Page<Room> chatRoomPage = roomDAO.getBaseMapper()
				.selectPage(new Page<>(page, 5), new QueryWrapper<Room>().in("founder_id", userIds));
		List<DisplayRoomVO> rooms = buildRoomVO(chatRoomPage, queryList);
		return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
	}

	@Override
	public void applyToJoinRoom(Integer roomId) {
		int userId = RequestUtil.getUserId(request);
		Assert.isNull(userRoomDAO.getBaseMapper()
						.selectOne(new QueryWrapper<UserRoom>().eq("user_id", userId).eq("room_id", roomId)),
				"您已经申请过这个聊天室了");
		UserRoom userRoom = new UserRoom().setUserId(userId).setRoomId(roomId)
				.setStatus(ApplyStatusConstant.APPLY_STATUS_APPLY);
		int insert = userRoomDAO.getBaseMapper().insert(userRoom);
		Assert.isTrue(insert > 0, "插入失败");
		messageService.sendNotice(roomId, userId);
	}

	@Transactional(rollbackFor = IllegalArgumentException.class)
	@Override
	public void agreeApply(Integer roomId, Integer userId, int noticeId) {
		Room room = roomDAO.getBaseMapper().selectById(roomId);
		Message message = messageDAO.getBaseMapper().selectById(room.getLastMsgId());
		int update = userRoomDAO.getBaseMapper().update(
				new UserRoom().setLastAckTime(message.getUpdateAt())
						.setStatus(ApplyStatusConstant.APPLY_STATUS_AGREE),
				new QueryWrapper<UserRoom>().eq("user_id", userId).eq("room_id", roomId));
		Assert.isTrue(update > 0, "更新失败");
		Assert.isTrue(noticeDAO.getBaseMapper().deleteById(noticeId) > 0, "删除失败");
	}

	@Override
	public void refuseApply(Integer roomId, Integer userId, int noticeId) {
		int update = userRoomDAO.getBaseMapper()
				.update(new UserRoom().setStatus(ApplyStatusConstant.APPLY_STATUS_REFUSE),
						new QueryWrapper<UserRoom>().eq("user_id", userId).eq("room_id", roomId));
		Assert.isTrue(update > 0, "更新失败");
		Assert.isTrue(noticeDAO.getBaseMapper().deleteById(noticeId) > 0, "删除失败");
	}

	@Override
	public RealRoomVO queryListPage(int page, int size) {
		Long total = roomDAO.getBaseMapper().selectCount(null);
		if (total == 0) {
			return new RealRoomVO().setRooms(new ArrayList<>()).setTotal(0);
		}
		Page<Room> chatRoomPage = roomDAO.getBaseMapper()
				.selectPage(new Page<>(page, size), new QueryWrapper<>());
		List<DisplayRoomVO> rooms = buildRoomVO(chatRoomPage, userRpcService.getAllInfo().getData());
		return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
	}

	@Override
	public List<MessageVO> getGroupMsgPages(PageGroupMsgModel model) {
		Page<Message> messagePage = messageDAO.getBaseMapper()
				.selectPage(new Page<>(model.getPage(), model.getSize()),
						new QueryWrapper<Message>().eq("receiver_id", model.getRoomId().toString())
								.orderByDesc("timestamp").last("limit 10"));
		//给messages排序 从小到大
		messagePage.getRecords().sort(Comparator.comparingLong(o -> o.getUpdateAt().getTime()));
		List<Integer> ids = messagePage.getRecords().stream().map(Message::getSenderId).toList();
		RpcResult<List<UserInfoDTO>> batchInfo = userRpcService.getBatchInfo(ids);
		Assert.isTrue(batchInfo.isSuccess(), "user模块调用失败");
		Map<Integer, UserInfoDTO> map = batchInfo.getData().stream()
				.collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
		return messagePage.getRecords().stream().map(vo -> new MessageVO(vo, map.get(vo.getSenderId())))
				.collect(Collectors.toList());
	}

	private List<DisplayRoomVO> buildRoomVO(Page<Room> chatRoomPage,
	                                        List<UserInfoDTO> data) {
		Assert.isTrue(chatRoomPage.getTotal() > 0);
		Map<Integer, UserInfoDTO> map = data.stream()
				.collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
		return chatRoomPage.getRecords().stream().map(room -> new DisplayRoomVO()
				.setRoomId(room.getRoomId())
				.setRoomName(room.getRoomName())
				.setFounderName(map.get(room.getFounderId()).getUsername())
				.setRoomDescription(room.getRoomDescription())
				.setMaxPeople(room.getMaxPeople())
		).collect(Collectors.toList());
	}

}
