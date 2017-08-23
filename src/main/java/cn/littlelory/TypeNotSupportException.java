package cn.littlelory;

/**
 * Created by littlelory on 2017/8/23.
 */
public class TypeNotSupportException extends RuntimeException {
    public TypeNotSupportException() {
    }

    public TypeNotSupportException(String message) {
        super(message);
    }

    public TypeNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeNotSupportException(Throwable cause) {
        super(cause);
    }
}
