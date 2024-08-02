package www.raven.jc.service;

import java.util.List;
import www.raven.jc.entity.model.CommentModel;
import www.raven.jc.entity.model.LikeModel;
import www.raven.jc.entity.model.MomentModel;
import www.raven.jc.entity.vo.MomentVO;

/**
 * social service
 *
 * @author 刘家辉
 * @date 2024/01/24
 */
public interface SocialService {

  /**
   * release moment
   *
   * @param model model
   */
  void releaseMoment(MomentModel model);

  /**
   * delete moment
   *
   * @param momentId moment id
   */
  void deleteMoment(String momentId);

  /**
   * like moment
   *
   * @param likeModel like model
   */
  void likeMoment(LikeModel likeModel);

  /**
   * comment moment
   *
   * @param model model
   */
  void commentMoment(CommentModel model);

  /**
   * query moment
   *
   * @param page page
   * @param size size
   * @return {@link List}<{@link MomentVO}>
   */
  List<MomentVO> queryMoment(int page, int size);

}
