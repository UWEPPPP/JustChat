package www.raven.jc.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.LikeModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.vo.MomentVO;
import www.raven.jc.result.HttpResult;
import www.raven.jc.service.SocialService;

/**
 * social controller
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
@RestController
@ResponseBody
public class SocialController {

  @Autowired
  private SocialService socialService;
  @Autowired
  private HttpServletRequest request;

  @GetMapping("/queryMoment/{page}/{size}")
  public HttpResult<List<MomentVO>> queryMoment(
      @PathVariable("page") int page, @PathVariable("size") int size) {
    return HttpResult.operateSuccess("查询成功", socialService.queryMoment(page, size));
  }

  @PostMapping("/releaseMoment")
  public HttpResult<Void> releaseMoment(
      @RequestBody @Validated MomentModel model) {
    socialService.releaseMoment(model);
    return HttpResult.operateSuccess("发布成功");
  }

  @PostMapping("/deleteMoment/{momentId}")
  public HttpResult<Void> deleteMoment(
      @PathVariable("momentId") @Validated String momentId) {
    socialService.deleteMoment(momentId);
    return HttpResult.operateSuccess("删除成功");
  }

  @PostMapping("/likeMoment")
  public HttpResult<Void> likeMoment(
      @RequestBody @Validated LikeModel model) {
    socialService.likeMoment(model);
    return HttpResult.operateSuccess("点赞成功");
  }

  @PostMapping("/commentMoment")
  public HttpResult<Void> commentMoment(
      @RequestBody @Validated CommentModel model) {
    socialService.commentMoment(model);
    return HttpResult.operateSuccess("评论成功");
  }
}
