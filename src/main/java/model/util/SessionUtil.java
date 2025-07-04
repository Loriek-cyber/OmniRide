package model.util;
import java.security.SecureRandom;
import java.util.Base64;

public class SessionUtil {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateSecureSessionId() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}