package www.raven.jc.service;

import java.util.List;

import www.raven.jc.entity.model.PagesFriendMsgModel;
import www.raven.jc.entity.vo.MessageVO;
import www.raven.jc.entity.vo.UserFriendVO;

/**
 * friend service
 *
 * @author 刘家辉
 * @date 2024/01/21
 */
public interface FriendService {

	/**
	 * init user friend page
	 *
	 * @return {@link List}<{@link UserFriendVO}>
	 */
	List<UserFriendVO> initUserFriendPage();

	/**
	 * restore friend history
	 *
	 * @param model model
	 * @return {@link List}<{@link MessageVO}>
	 */
	List<MessageVO> getFriendMsgPages(PagesFriendMsgModel model);

}
