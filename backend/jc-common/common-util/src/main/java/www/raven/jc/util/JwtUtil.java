package www.raven.jc.util;

import static www.raven.jc.constant.JwtConstant.ROLE;
import static www.raven.jc.constant.JwtConstant.TIME;
import static www.raven.jc.constant.JwtConstant.USER_ID;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import www.raven.jc.dto.TokenDTO;

/**
 * jwt util
 *
 * @author 刘家辉
 * @date 2023/11/20
 */
@Slf4j
public class JwtUtil {

  public static String createToken(int userId, List<String> roles, String key,
      Long expireTime) {
    HashMap<String, Object> map = new HashMap<>(3);
    map.put(USER_ID, userId);
    map.put(ROLE, roles);
    map.put(TIME, System.currentTimeMillis() + expireTime);
    return JWTUtil.createToken(map, key.getBytes());
  }

  @SuppressWarnings("unchecked")
  public static TokenDTO parseToken(String token, String key) {
    Assert.isTrue(JWTUtil.verify(token, key.getBytes()), "token验证失败");
    JWTPayload payload = JWTUtil.parseToken(token).getPayload();
    NumberWithFormat expireTime = (NumberWithFormat) payload.getClaim(TIME);
    List<String> role = Convert.convert(List.class, payload.getClaim(ROLE));
    NumberWithFormat userId = (NumberWithFormat) payload.getClaim(USER_ID);
    return new TokenDTO().setRole(role).setUserId(userId.intValue())
        .setExpireTime(expireTime.longValue());
  }
}
