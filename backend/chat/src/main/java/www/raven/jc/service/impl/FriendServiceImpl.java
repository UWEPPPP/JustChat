package www.raven.jc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import www.raven.jc.api.UserDubbo;
import www.raven.jc.dao.FriendChatDAO;
import www.raven.jc.dao.MessageDAO;
import www.raven.jc.dto.UserInfoDTO;
import www.raven.jc.entity.po.FriendChat;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.entity.vo.UserFriendVO;
import www.raven.jc.result.RpcResult;
import www.raven.jc.service.FriendService;
import www.raven.jc.util.JsonUtil;
import www.raven.jc.util.MongoUtil;

/**
 * friend service impl
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
@Service
@Slf4j
public class FriendServiceImpl implements FriendService {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private FriendChatDAO friendChatDAO;
    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private UserDubbo userDubbo;

    @Override
    public List<UserFriendVO> initUserFriendPage() {
        int userId = Integer.parseInt(request.getHeader("userId"));
        //获得好友id
        List<UserInfoDTO> friends = userDubbo.getFriendInfos(userId).getData();
        if (friends.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> ids = friends.stream().map(UserInfoDTO::getUserId).collect(Collectors.toList());
        List<String> fixedFriendIds = new ArrayList<>();
        for (Integer friendId : ids) {
            fixedFriendIds.add(MongoUtil.concatenateIds(userId, friendId));
        }
        List<FriendChat> friendChats = friendChatDAO.getBaseMapper().selectList(new QueryWrapper<FriendChat>().in("fix_id", fixedFriendIds));
        //获取好友的最后一条消息id
        List<ObjectId> idsMsg = friendChats.stream()
            .map(chat -> new ObjectId(chat.getLastMsgId()))
            .collect(Collectors.toList());
        //获取好友的最后一条消息
        List<Message> messages = messageDAO.getBatchIds(idsMsg);
        //将好友id和好友的最后一条消息id对应起来
        Map<Integer, Message> messageMap = messages.stream()
            .collect(Collectors.toMap(
                message -> message.getSenderId().equals(userId) ? MongoUtil.resolve(message.getReceiverId(), userId) : message.getSenderId(),
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
                .setLastMsgSender(message == null ? "" : message.getSenderId().equals(userId) ? "我" : friend.getUsername());
        }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "friendHistory", key = "#friendId")
    public List<MessageVO> restoreFriendHistory(Integer friendId) {
        int userId = Integer.parseInt(request.getHeader("userId"));
        String fixId = MongoUtil.concatenateIds(userId, friendId);
        List<Message> byFriendChatId = messageDAO.getByFriendChatId(fixId);
        ArrayList<Integer> ids = new ArrayList<>() {{
            add(userId);
            add(friendId);
        }};
        RpcResult<List<UserInfoDTO>> batchInfo = userDubbo.getBatchInfo(ids);
        Map<Integer, UserInfoDTO> userInfoMap = batchInfo.getData().stream().collect(Collectors.toMap(UserInfoDTO::getUserId, Function.identity()));

        return byFriendChatId.stream().map(message -> new MessageVO()
            .setTime(message.getTimestamp())
            .setText(message.getContent())
            .setUser(userInfoMap.get(message.getSenderId()).getUsername())
            .setProfile(userInfoMap.get(message.getSenderId()).getProfile())
        ).collect(Collectors.toList());
    }

}
