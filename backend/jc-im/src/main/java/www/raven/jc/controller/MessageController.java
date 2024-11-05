package www.raven.jc.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.PageGroupMsgModel;
import www.raven.jc.entity.model.PagesFriendMsgModel;
import www.raven.jc.entity.po.MessageReadAck;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.result.HttpResult;
import www.raven.jc.service.FriendService;
import www.raven.jc.service.MessageService;
import www.raven.jc.service.RoomService;

/**
 * query controller
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
@RestController
@ResponseBody
@RequestMapping("/message")
public class MessageController {

	@Autowired
	private MessageService messageService;
	@Autowired
	private RoomService roomService;
	@Autowired
	private FriendService friendService;

	/**
	 * 拉取全量离线消息接口
	 */
	@GetMapping("/getLatestOffline")
	public HttpResult<List<MessageVO>> getLatestOfflineMsg() {
		return HttpResult.operateSuccess("获取最新离线信息成功", messageService.getLatestOffline());
	}

	@GetMapping("/queryRoomMsgPages/{roomId}/{page}/{size}")
	public HttpResult<List<MessageVO>> getGroupMsgHistory(
			@PathVariable("roomId") int roomId, @PathVariable("page") int page,
			@PathVariable("size") int size) {
		PageGroupMsgModel model = new PageGroupMsgModel().setRoomId(roomId).setPage(page).setSize(size);
		return HttpResult.operateSuccess("获取历史群聊信息成功", roomService.getGroupMsgPages(model));
	}

	@GetMapping("/queryFriendMsgPages/{friendId}/{page}/{size}")
	public HttpResult<List<MessageVO>> getFriendMsgHistory(
			@PathVariable("friendId") int friendId, @PathVariable("page") int page,
			@PathVariable("size") int size) {
		PagesFriendMsgModel model = new PagesFriendMsgModel().setFriendId(friendId).setPage(page)
				.setSize(size);
		return HttpResult.operateSuccess("获取历史私聊信息成功",
				friendService.getFriendMsgPages(model));
	}

	/**
	 * 业务设计背景：用户只能看到自己发出的消息是否被已读
	 * <p>
	 * 获取用户自己消息的已读回执 供前端轮询获取
	 */
	@GetMapping("/getReadMessageAck")
	public HttpResult<List<MessageReadAck>> getReadMessageAck() {
		return HttpResult.operateSuccess("获取消息已读状态成功", messageService.getReadMessageAck());
	}
}
