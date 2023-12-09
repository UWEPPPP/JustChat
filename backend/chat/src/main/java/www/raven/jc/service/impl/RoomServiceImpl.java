package www.raven.jc.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dao.RoomDAO;
import www.raven.jc.dao.UserRoomDAO;
import www.raven.jc.dto.QueryUserInfoDTO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.model.RoomModel;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.Room;
import www.raven.jc.entity.po.UserRoom;
import www.raven.jc.entity.vo.RealRoomVO;
import www.raven.jc.entity.vo.RoomVO;
import www.raven.jc.event.JoinRoomApplyEvent;
import www.raven.jc.feign.UserFeign;
import www.raven.jc.result.CommonResult;
import www.raven.jc.service.RoomService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MqUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * room service impl
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    private RoomDAO roomDAO;
    @Autowired
    private UserRoomDAO userRoomDAO;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private StreamBridge streamBridge;

    @Transactional(rollbackFor = IllegalArgumentException.class)
    @Override
    public void createRoom(RoomModel roomModel) {
        Room room = new Room().setRoomName(roomModel.getName())
                .setRoomDescription(roomModel.getDescription())
                .setMaxPeople(roomModel.getMaxPeople()).
                setFounderId(Integer.parseInt(request.getHeader("userId")));
        Assert.isTrue(roomDAO.getBaseMapper().insert(room) > 0);
    }

    @Override
    public RealRoomVO initUserMainPage(Integer page, Integer size) {
        int userId = Integer.parseInt(request.getHeader("userId"));
        //获取用户加入的聊天室id
        List<UserRoom> roomIdList = userRoomDAO.getBaseMapper().selectList(new QueryWrapper<UserRoom>().eq("user_id", userId));
        List<Integer> roomIds = roomIdList.stream().map(UserRoom::getRoomId).collect(Collectors.toList());
        //分页查询？
        Page<Room> roomsPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, size), new QueryWrapper<Room>().in("room_id", roomIds));
        List<Integer> ids = roomsPage.getRecords().stream().map(Room::getLastMsgId).collect(Collectors.toList());
        //获取所有聊天室的最后一条消息
        List<Message> messages = messageDAO.getBaseMapper().selectBatchIds(ids);
        CommonResult<List<UserInfoDTO>> batchInfo = userFeign.getBatchInfo(messages.stream().map(Message::getSenderId).collect(Collectors.toList()));
        Assert.isTrue(batchInfo.getCode() == 200,"userFeign调用失败");
        Map<Integer, UserInfoDTO> map = batchInfo.getData().stream().collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        Map<Integer, Message> messageMap = messages.stream().collect(Collectors.toMap(Message::getMessageId, Function.identity()));
        List<RoomVO> rooms = roomsPage.getRecords().stream().map(room -> new RoomVO()
                .setRoomId(room.getRoomId())
                .setRoomName(room.getRoomName())
                .setLastMsg(JsonUtil.objToJson(messageMap.get(room.getLastMsgId())))
                .setLastMsgSender(map.get(messageMap.get(room.getLastMsgId()).getSenderId()).getUsername())
                .setRoomProfile(map.get(room.getFounderId()).getProfile())).collect(Collectors.toList());
        return new RealRoomVO().setRooms(rooms).setTotal(roomIds.size());
    }

    @Override
    public RealRoomVO queryLikedRoomList(String column, String text, int page) {
        Long total = roomDAO.getBaseMapper().selectCount(null);
        Page<Room> chatRoomPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, 5), new QueryWrapper<Room>().like(column, text));
        List<RoomVO> rooms = buildRoomVO(chatRoomPage, userFeign.getAllInfo().getData());
        return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
    }

    @Override
    public RealRoomVO queryUserNameRoomList(String column, String text, int page) {
        Long total = roomDAO.getBaseMapper().selectCount(null);
        List<UserInfoDTO> queryList = userFeign.getRelatedInfoList(new QueryUserInfoDTO().setColumn(column).setText(text)).getData();
        List<Integer> userIds = queryList.stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        Page<Room> chatRoomPage = roomDAO.getBaseMapper().selectPage(new Page<>(page, 5), new QueryWrapper<Room>().in("founder_id", userIds));
        List<RoomVO> rooms = buildRoomVO(chatRoomPage, queryList);
        return new RealRoomVO().setRooms(rooms).setTotal(Math.toIntExact(total));
    }

    @Override
    public void applyToJoinRoom(Integer roomId) {
        int userId = Integer.parseInt(request.getHeader("userId"));
        Assert.isNull(userRoomDAO.getBaseMapper().selectOne(new QueryWrapper<UserRoom>().eq("user_id", userId).eq("room_id", roomId)),
                "您已经在这个聊天室了");
        Room room = roomDAO.getBaseMapper().selectById(roomId);
        Integer founderId = room.getFounderId();
        //通知user模块 插入一条申请记录
        streamBridge.send("producer-out-1", MqUtil.createMsg(JsonUtil.objToJson(new JoinRoomApplyEvent(userId, founderId, roomId)), "APPLY"));
    }


    private List<RoomVO> buildRoomVO(Page<Room> chatRoomPage, List<UserInfoDTO> data) {
        Assert.isTrue(chatRoomPage.getTotal() > 0);
        Map<Integer, UserInfoDTO> map = data.stream().collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));
        List<Integer> ids = chatRoomPage.getRecords().stream().map(Room::getLastMsgId).collect(Collectors.toList());
        List<Message> messages = messageDAO.getBaseMapper().selectBatchIds(ids);
        Map<Integer, Message> messageMap = messages.stream().collect(Collectors.toMap(Message::getMessageId, Function.identity()));
        return chatRoomPage.getRecords().stream().map(room -> new RoomVO()
                .setRoomId(room.getRoomId())
                .setRoomName(room.getRoomName())
                .setLastMsg(JsonUtil.objToJson(messageMap.get(room.getLastMsgId())))
                .setRoomProfile(map.get(room.getFounderId()).getProfile())).collect(Collectors.toList());
    }


}
