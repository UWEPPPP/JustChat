package www.raven.jc.service;

import java.util.List;

import www.raven.jc.entity.vo.NoticeVO;

/**
 * notice service
 *
 * @author 刘家辉
 * @date 2023/12/04
 */
public interface NoticeService {

	/**
	 * load notice
	 *
	 * @return {@link List}<{@link NoticeVO}>
	 */
	List<NoticeVO> loadNotice();

	/**
	 * add friend apply
	 *
	 * @param friendName friend name
	 */
	void addFriendApply(String friendName);

	/**
	 * done notification
	 *
	 * @param id id
	 */
	void deleteNotification(Integer id);

}
