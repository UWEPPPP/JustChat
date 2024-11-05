package www.raven.jc.controller;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import www.raven.jc.config.JwtProperty;
import www.raven.jc.entity.model.LoginModel;
import www.raven.jc.entity.model.RegisterModel;
import www.raven.jc.entity.vo.TokenVO;
import www.raven.jc.result.HttpResult;
import www.raven.jc.service.AuthService;

/**
 * account controller
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@RestController
@ResponseBody
public class AuthController {

	@Autowired
	private AuthService authService;
	@Autowired
	private JwtProperty jwtProperty;

	@PostMapping("/login")
	public HttpResult<TokenVO> login(
			@RequestBody @Validated LoginModel loginModel) {
		return HttpResult.operateSuccess("登录成功", authService.login(loginModel));
	}

	@PostMapping("/register")
	public HttpResult<TokenVO> register(
			@RequestBody @Validated RegisterModel registerModel) {
		return HttpResult.operateSuccess("注册成功", authService.registerCommonRole(registerModel));
	}

	@GetMapping("/logout/{token}")
	public HttpResult<Void> logout(
			@PathVariable("token") @NotBlank String token) {
		authService.logout(token);
		return HttpResult.operateSuccess("登出成功");
	}

	@PostMapping("/refresh")
	public HttpResult<TokenVO> refresh(@RequestBody @NotBlank String token) {
		return HttpResult.operateSuccess("刷新成功", authService.refreshToken(token));
	}

}
