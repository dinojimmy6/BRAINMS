package encryption;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import utils.HexTool;

public class LoginCrypto {
    private final static Random rand = new Random();

    private static String toSimpleHexString(final byte[] bytes) {
        return HexTool.toString(bytes).replace(" ", "").toLowerCase();
    }

    private static String hashWithDigest(final String in, final String digest) {
        try {
            MessageDigest Digester = MessageDigest.getInstance(digest);
            Digester.update(in.getBytes("UTF-8"), 0, in.length());
            byte[] sha1Hash = Digester.digest();
            return toSimpleHexString(sha1Hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Hashing the password failed", ex);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding the string failed", e);
        }
    }

    public static String hexSha1(final String in) {
        return hashWithDigest(in, "SHA-1");
    }

    private static String hexSha512(final String in) {
        return hashWithDigest(in, "SHA-512");
    }

    public static String makeSaltedSha1Hash(final String password, final String salt) {
        return hexSha1(password + salt);
    }

    public static boolean checkSha1Hash(final String hash, final String password) {
        return hash.equals(hexSha1(password));
    }

    public static boolean checkSaltedSha1Hash(final String hash, final String password, final String salt) {
        return hash.equals(makeSaltedSha1Hash(password, salt));
    }

    public static boolean checkSaltedSha512Hash(final String hash, final String password, final String salt) {
        return hash.equals(makeSaltedSha512Hash(password, salt));
    }

    public static String makeSaltedSha512Hash(final String password, final String salt) {
        return hexSha512(password + salt);
    }

    public static String makeSalt() {
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        return toSimpleHexString(salt);
    }
}
