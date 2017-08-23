package cn.littlelory;

/**
 * Created by littlelory on 2017/8/23.
 */
public class MultiKeyException extends RuntimeException {
    public MultiKeyException() {
    }

    public MultiKeyException(String message) {
        super(message);
    }

    public MultiKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiKeyException(Throwable cause) {
        super(cause);
    }
}
