package www.raven.jc.service;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import www.raven.jc.entity.po.Message;
import www.raven.jc.entity.po.MessageReadAck;
import www.raven.jc.entity.vo.MessageVO;

/**
 * chat async service
 *
 * @author 刘家辉
 * @date 2024/02/23
 */
public interface MessageService {

	/**
	 * send message
	 *
	 * @param roomId room id
	 * @param userId user id
	 */
	@Async
	void sendNotice(Integer roomId, Integer userId);

	/**
	 * save offline message
	 *
	 * @param message message
	 * @param userIds user ids
	 */
	default void saveOfflineMsgAndReadAck(Message message,
	                                      List<Integer> userIds) {
		saveOfflineMsgAndReadAck(message, userIds, null);
	}

	/**
	 * save offline message
	 *
	 * @param message message
	 * @param userIds user ids
	 * @param metaId  meta id
	 */
	void saveOfflineMsgAndReadAck(Message message, List<Integer> userIds,
	                              Integer metaId);

	/**
	 * get latest offline
	 *
	 * @return list
	 */
	List<MessageVO> getLatestOffline();

	/**
	 * get message ack
	 *
	 * @return {@link List }<{@link MessageReadAck }>
	 */
	List<MessageReadAck> getReadMessageAck();

}
