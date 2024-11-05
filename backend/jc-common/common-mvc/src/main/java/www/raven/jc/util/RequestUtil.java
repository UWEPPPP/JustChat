package www.raven.jc.util;

import jakarta.servlet.http.HttpServletRequest;
import www.raven.jc.constant.JwtConstant;

/**
 * request util
 *
 * @author 刘家辉
 * @date 2024/02/06
 */
public class RequestUtil {

	public static int getUserId(HttpServletRequest request) {
		return Integer.parseInt(request.getHeader(JwtConstant.USER_ID));
	}
}
