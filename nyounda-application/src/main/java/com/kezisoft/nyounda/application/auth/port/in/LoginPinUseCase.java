package com.kezisoft.nyounda.application.auth.port.in;

import com.kezisoft.nyounda.application.auth.command.GeneratePinCommand;
import com.kezisoft.nyounda.application.auth.command.VerifyPinCommand;
import com.kezisoft.nyounda.domain.auth.JwtToken;
import com.kezisoft.nyounda.domain.auth.VerificationStatus;

public interface LoginPinUseCase {
    VerificationStatus sendLoginPin(GeneratePinCommand generatePinCommand);

    JwtToken verifyAndCreateToken(VerifyPinCommand verifyPinCommand);

    JwtToken createToken(String phone);
}
