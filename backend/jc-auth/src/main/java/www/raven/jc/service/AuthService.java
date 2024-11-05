package www.raven.jc.service;

import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.entity.vo.TokenVO;

/**
 * account service
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
public interface AuthService {

	/**
	 * login
	 *
	 * @param loginModel login model
	 * @return token
	 */
	TokenVO login(LoginModel loginModel);

	/**
	 * register
	 *
	 * @param registerModel register model
	 * @return {@link String}
	 */
	TokenVO registerCommonRole(RegisterModel registerModel);

	/**
	 * register admin role
	 *
	 * @param registerModel register model
	 * @return {@link String}
	 */
	TokenVO registerAdminRole(RegisterModel registerModel);

	/**
	 * refresh token
	 *
	 * @param token token
	 * @return {@link String}
	 */
	TokenVO refreshToken(String token);

	/**
	 * logout
	 *
	 * @param token token
	 */
	void logout(String token);
}
