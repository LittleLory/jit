package cn.littlelory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Created by littlelory on 2017/8/25.
 */
class Fingerprint {
    private static MessageDigest messageDigest;

    //todo how to remove this ugly code...
    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generate(byte[] bytes) {
        Objects.requireNonNull(bytes);

        try {
            messageDigest.update(bytes);
            MessageDigest md = (MessageDigest) messageDigest.clone();
            return bytesToHexStr(md.digest());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String bytesToHexStr(byte[] bytes) {
        char[] chars = new char[bytes.length*2];
        for (int i = 0; i < bytes.length; i++) {
            chars[2*i] = (char) (bytes[i] >> 4 & 0xf);
            chars[2*i+1] = (char) (bytes[i] & 0xf);
        }
        return String.valueOf(chars);
    }
}
