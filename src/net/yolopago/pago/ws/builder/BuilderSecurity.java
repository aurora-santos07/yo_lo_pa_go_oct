package net.yolopago.pago.ws.builder;

import net.yolopago.pago.db.entity.Session;
import net.yolopago.pago.db.entity.User;
import net.yolopago.pago.ws.dto.security.Token;
import net.yolopago.pago.ws.dto.security.UserDto;

public class BuilderSecurity {

	public Session build(Token token, UserDto userDto) {
		Session session = new Session();
		session.setAccessToken(token.getAccess_token());
		session.setRefreshToken(token.getRefresh_token());
		session.setIdUser(userDto.getId());

		return session;
	}

	public User build(UserDto userDto) {
		User user = new User();
		user.set_id(userDto.getId());
		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());

		return user;
	}

}
