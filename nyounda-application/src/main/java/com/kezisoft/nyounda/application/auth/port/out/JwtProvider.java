package com.kezisoft.nyounda.application.auth.port.out;

import com.kezisoft.nyounda.domain.auth.Authentication;
import com.kezisoft.nyounda.domain.auth.JwtToken;
import com.kezisoft.nyounda.domain.user.User;

import java.util.Optional;

public interface JwtProvider {

    JwtToken createToken(User user);

    boolean validateToken(String authToken);

    Optional<Authentication> retreiveAuthentication(String authToken);

}
