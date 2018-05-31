package uk.gov.hmcts.reform.feature.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncrypter {

    private PasswordEncrypter() {
    }

    public static String encryptUsingBCryptEncoder(String plaintextPassword) {
        return new BCryptPasswordEncoder().encode(plaintextPassword);
    }
}
