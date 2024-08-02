package www.raven.jc.controller;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.AddFriendApplyModel;
import www.raven.jc.entity.vo.NoticeVO;
import www.raven.jc.result.HttpResult;
import www.raven.jc.service.NoticeService;

/**
 * notice controller
 *
 * @author 刘家辉
 * @date 2023/12/16
 */
@RestController
@ResponseBody
@RequestMapping("/notice")
@Slf4j
public class NoticeController {

  @Autowired
  private NoticeService noticeService;

  @GetMapping("/getNotice")
  public HttpResult<List<NoticeVO>> getNotice() {
    return HttpResult.operateSuccess("查找成功", noticeService.loadNotice());
  }

  @PostMapping("/addFriendApply")
  public HttpResult<Void> addFriendApply(
      @RequestBody @NotNull AddFriendApplyModel model) {
    noticeService.addFriendApply(model.getFriendName());
    return HttpResult.operateSuccess("添加好友申请成功");
  }
}
