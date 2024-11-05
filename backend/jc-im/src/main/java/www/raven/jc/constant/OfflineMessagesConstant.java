package www.raven.jc.constant;

import java.time.Duration;

/**
 * offline messages constant
 *
 * @author 刘家辉
 * @date 2024/02/28
 */
public class OfflineMessagesConstant {

	public static final String PREFIX = "offline_message_";
	public static final String PREFIX_MATCH = "offline_message_*";
	public static final Duration EXPIRE_DAYS = Duration.ofDays(7);
}
