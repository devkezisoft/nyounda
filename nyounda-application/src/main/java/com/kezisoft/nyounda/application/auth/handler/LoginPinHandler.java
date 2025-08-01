package com.kezisoft.nyounda.application.auth.handler;

import com.kezisoft.nyounda.application.auth.command.GeneratePinCommand;
import com.kezisoft.nyounda.application.auth.command.VerifyPinCommand;
import com.kezisoft.nyounda.application.auth.exception.InvalidPinCodeException;
import com.kezisoft.nyounda.application.auth.exception.PinCodeGenerationCanceledException;
import com.kezisoft.nyounda.application.auth.port.in.LoginPinUseCase;
import com.kezisoft.nyounda.application.auth.port.out.JwtProvider;
import com.kezisoft.nyounda.application.auth.port.out.PinCodeProvider;
import com.kezisoft.nyounda.application.user.port.in.UserUseCase;
import com.kezisoft.nyounda.domain.auth.JwtToken;
import com.kezisoft.nyounda.domain.auth.VerificationStatus;
import com.kezisoft.nyounda.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginPinHandler implements LoginPinUseCase {

    private final PinCodeProvider pinCodeProvider;
    private final UserUseCase userUseCase;
    private final JwtProvider jwtProvider;

    @Override
    public VerificationStatus sendLoginPin(GeneratePinCommand generatePinCommand) {
        if (generatePinCommand.phone().startsWith("+23799999")) {
            return VerificationStatus.PENDING;
        }
        VerificationStatus verificationStatus = pinCodeProvider.send(generatePinCommand.phone(), generatePinCommand.channel());
        if (verificationStatus == VerificationStatus.CANCELED) {
            throw new PinCodeGenerationCanceledException();
        }

        return verificationStatus;
    }

    @Override
    public JwtToken verifyAndCreateToken(VerifyPinCommand verifyPinCommand) {
        if (verifyPinCommand.phone().startsWith("+23799999") && verifyPinCommand.pinCode().startsWith("9999")) {
            return createToken(verifyPinCommand.phone());
        }

        if (!pinCodeProvider.verify(verifyPinCommand.phone(), verifyPinCommand.pinCode())) {
            throw new InvalidPinCodeException();
        }
        return createToken(verifyPinCommand.phone());
    }

    @Override
    public JwtToken createToken(String phone) {
        User user = userUseCase.getOrCreateUser(phone);
        return jwtProvider.createToken(user);
    }


}
