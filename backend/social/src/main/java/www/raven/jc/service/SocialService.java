package www.raven.jc.service;

import java.util.List;
import www.raven.jc.entity.model.CommentModel;
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
     * @param momentId     moment id
     * @param momentUserId moment user id
     */
    void likeMoment(String momentId, Integer momentUserId);

    /**
     * comment moment
     *
     * @param model model
     */
    void commentMoment(CommentModel model);

    /**
     * query moment
     * query moment
     *
     * @param userId user id
     * @return {@link List}<{@link MomentVO}>
     */
    List<MomentVO> queryMoment(int userId);

}
