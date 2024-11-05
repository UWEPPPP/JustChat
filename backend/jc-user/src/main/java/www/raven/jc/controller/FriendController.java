package www.raven.jc.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.ApplyFriendModel;
import www.raven.jc.result.HttpResult;
import www.raven.jc.service.FriendService;

/**
 * friend controller
 *
 * @author 刘家辉
 * @date 2024/01/20
 */
@RestController
@ResponseBody
@RequestMapping("/friend")
@Slf4j
public class FriendController {

	@Autowired
	private FriendService friendService;

	@PostMapping("/agreeToBeFriend")
	public HttpResult<Void> agreeApplyFriend(
			@RequestBody @NotNull ApplyFriendModel model) {
		friendService.agreeApplyFromFriend(model.getFriendId(), model.getNoticeId());
		return HttpResult.operateSuccess("成为好友成功");
	}

	@PostMapping("/refuseToBeFriend")
	public HttpResult<Void> refuseApplyFriend(
			@RequestBody @NotNull ApplyFriendModel model) {
		friendService.refuseApplyFromFriend(model.getNoticeId());
		return HttpResult.operateSuccess("拒绝好友成功");
	}

}
