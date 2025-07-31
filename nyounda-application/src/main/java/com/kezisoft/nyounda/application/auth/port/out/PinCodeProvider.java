package com.kezisoft.nyounda.application.auth.port.out;

import com.kezisoft.nyounda.domain.auth.Channel;
import com.kezisoft.nyounda.domain.auth.VerificationStatus;

public interface PinCodeProvider {
    VerificationStatus send(String phoneNumber, Channel channel);

    boolean verify(String phoneNumber, String pinCode);
}
