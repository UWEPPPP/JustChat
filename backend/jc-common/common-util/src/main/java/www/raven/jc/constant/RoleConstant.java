package www.raven.jc.constant;

import java.util.HashMap;

/**
 * role constant
 *
 * @author 刘家辉
 * @date 2023/11/28
 */
public class RoleConstant {

	public static final Integer ADMIN_ROLE_NUMBER = 2;
	public static final Integer COMMON_ROLE_NUMBER = 1;
	public static final String ADMIN_ROLE = "ROLE_ADMIN";
	public static final String COMMON_ROLE = "ROLE_USER";
	public static final HashMap<Integer, String> MAP = new HashMap<>(2);

	static {
		MAP.put(ADMIN_ROLE_NUMBER, ADMIN_ROLE);
		MAP.put(COMMON_ROLE_NUMBER, COMMON_ROLE);
	}
}

