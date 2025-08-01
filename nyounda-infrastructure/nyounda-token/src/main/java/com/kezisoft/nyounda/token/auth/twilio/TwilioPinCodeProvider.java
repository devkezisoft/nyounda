package com.kezisoft.nyounda.token.auth.twilio;

import com.kezisoft.nyounda.application.auth.port.out.PinCodeProvider;
import com.kezisoft.nyounda.domain.auth.Channel;
import com.kezisoft.nyounda.domain.auth.VerificationStatus;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TwilioPinCodeProvider implements PinCodeProvider {
    private final TwilioProperties twilioProperties;


    public TwilioPinCodeProvider(TwilioProperties twilioProperties) {
        this.twilioProperties = twilioProperties;
        Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
    }


    @Override
    public VerificationStatus send(String phoneNumber, Channel channel) {
        Verification verification = Verification
                .creator(
                        twilioProperties.getVerifySid(),
                        phoneNumber, // concatenated with country code +33 or +237
                        channel.toString()
                )
                .create();
        var status = verification.getStatus();
        log.info("Verification code send, phoneNumber: {} - channel: {} - status: {}", phoneNumber, channel, status);
        if (status == null) {
            return VerificationStatus.CANCELED;
        }
        return VerificationStatus.forValue(status);
    }

    @Override
    public boolean verify(String phoneNumber, String pinCode) {
        VerificationCheck verificationCheck = VerificationCheck
                .creator(twilioProperties.getVerifySid())
                .setCode(pinCode)
                .setTo(phoneNumber)
                .create();

        var status = verificationCheck.getStatus();
        log.info("Verification code verified, phoneNumber: {} - status: {}", phoneNumber, status);

        VerificationStatus verificationStatus = VerificationStatus.forValue(status);
        return verificationStatus == VerificationStatus.APPROVED;
    }
}
