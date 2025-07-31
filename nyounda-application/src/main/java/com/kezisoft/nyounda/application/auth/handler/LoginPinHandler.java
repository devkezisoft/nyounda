package com.kezisoft.nyounda.application.auth.handler;

import com.kezisoft.nyounda.application.auth.command.GeneratePinCommand;
import com.kezisoft.nyounda.application.auth.command.VerifyPinCommand;
import com.kezisoft.nyounda.application.auth.exception.InvalidPinCodeException;
import com.kezisoft.nyounda.application.auth.port.in.LoginPinUseCase;
import com.kezisoft.nyounda.application.auth.port.out.JwtProvider;
import com.kezisoft.nyounda.application.auth.port.out.PinCodeProvider;
import com.kezisoft.nyounda.application.user.port.out.UserRepository;
import com.kezisoft.nyounda.domain.auth.JwtToken;
import com.kezisoft.nyounda.domain.auth.VerificationStatus;
import com.kezisoft.nyounda.domain.user.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginPinHandler implements LoginPinUseCase {

    private final PinCodeProvider pinCodeProvider;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public VerificationStatus sendLoginPin(GeneratePinCommand generatePinCommand) {
        return pinCodeProvider.send(generatePinCommand.phoneNumber(), generatePinCommand.channel());
    }

    @Override
    public JwtToken verifyLoginPin(VerifyPinCommand verifyPinCommand) {
        if (!pinCodeProvider.verify(verifyPinCommand.phoneNumber(), verifyPinCommand.pinCode())) {
            throw new InvalidPinCodeException();
        }

        User user = userRepository.findByPhoneNumber(verifyPinCommand.phoneNumber())
                .orElseGet(() -> userRepository.save(User.createFromPhoneNumber(verifyPinCommand.phoneNumber())));

        return jwtProvider.createToken(user);
    }
}
