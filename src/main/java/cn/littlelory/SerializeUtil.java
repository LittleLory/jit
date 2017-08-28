package cn.littlelory;

import java.nio.charset.Charset;

/**
 * Created by littlelory on 2017/8/28.
 */ 
class SerializeUtil {

    private static final int byteLen = 8;
    
    private static final int INT_LEN = 4;
    static byte[] encodeInt(Integer value) {
        byte[] result = new byte[INT_LEN];
        for (int i = 0; i < INT_LEN; i++)
            result[i] = (byte) ((value >> (INT_LEN - i - 1) * byteLen) & 0xff);
        return result;
    }

    static int decodeInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result <<= byteLen;
            result |= (bytes[i]&0x0ff);
        }
        return result;
    }

    private static final int LONG_LEN = 8;
    static byte[] encodeLong(Long value) {
        byte[] result = new byte[LONG_LEN];
        for (int i = 0; i < LONG_LEN; i++)
            result[i] = (byte) ((value >> (LONG_LEN - i - 1) * byteLen) & 0xff);
        return result;
    }

    static long decodeLong(byte[] bytes) {
        long result = 0L;
        for (int i = 0; i < bytes.length; i++) {
            result <<= byteLen;
            result |= bytes[i]&0x0ff;
        }
        return result;
    }

    static byte[] encodeStr(String value) {
        if (value == null || value.length() == 0)
            return new byte[0];
        return value.getBytes(charset());
    }

    static String decodeStr(byte[] bytes) {
        return new String(bytes, charset());
    }

    private static Charset charset() {
        return Charset.forName("utf8");
    }
}
