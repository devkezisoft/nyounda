package com.kezisoft.nyounda.api.auth;

import com.kezisoft.nyounda.api.auth.request.GeneratePinRequest;
import com.kezisoft.nyounda.api.auth.request.VerifyPinRequest;
import com.kezisoft.nyounda.api.errors.BadRequestAlertException;
import com.kezisoft.nyounda.application.auth.port.in.LoginPinUseCase;
import com.kezisoft.nyounda.domain.auth.JwtToken;
import com.kezisoft.nyounda.domain.auth.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class UserAuthResource {

    private static final String ENTITY_NAME = "user";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final LoginPinUseCase loginPinUseCase;

    /**
     * @param request the phone verification request
     * @return the status
     * @throws BadRequestAlertException errors
     */
    @GetMapping("/authenticate")
    public ResponseEntity<VerificationStatus> authenticate(GeneratePinRequest request) throws BadRequestAlertException {
        // for GOOGLE and APPLE verification
        if (request.phone().startsWith("+23799999")) {
            return ResponseEntity.ok(VerificationStatus.PENDING);
        }

        var status = loginPinUseCase.sendLoginPin(request.toCommand());
        if (status == VerificationStatus.CANCELED) {
            throw new BadRequestAlertException("An error occur during Generating pinCode", ENTITY_NAME, "errorGeneratePinCode");
        }
        return ResponseEntity.ok(status);
    }


    /**
     * Login or signup via phone number
     *
     * @param request : verification token, phone number, langKey and token request
     * @return JWTToken the token created
     */
    @GetMapping("/verify")
    public ResponseEntity<JwtToken> verify(VerifyPinRequest request) throws BadRequestAlertException {
        // for GOOGLE and APPLE verification
        if (request.phone().startsWith("+23799999") && request.pinCode().startsWith("9999")) {
            return generateJwtToken(request);
        }
        return generateJwtToken(request);
    }


    private ResponseEntity<JwtToken> generateJwtToken(VerifyPinRequest request) {
        log.debug("Find or create user by phone number: {}", request.phone());
        HttpHeaders httpHeaders = new HttpHeaders();
        log.info("Generate JWT token request: {}", request);
        JwtToken jwtToken = loginPinUseCase.verifyLoginPin(request.toCommand());
        httpHeaders.add(AUTHORIZATION_HEADER, "Bearer " + jwtToken.token());
        return new ResponseEntity<>(jwtToken, httpHeaders, HttpStatus.OK);
    }
}
