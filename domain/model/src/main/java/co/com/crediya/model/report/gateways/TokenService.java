package co.com.crediya.model.report.gateways;

import co.com.crediya.model.report.JwtUserInfo;
import co.com.crediya.model.report.User;

public interface TokenService {
    String generateToken(User user);
    boolean validateToken(String token);
    JwtUserInfo getUserInfoFromToken(String token);
}
