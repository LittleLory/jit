package cn.littlelory;

/**
 * Created by littlelory on 2017/8/24.
 */
public class MultiFieldException extends RuntimeException {
    public MultiFieldException() {
    }

    public MultiFieldException(String message) {
        super(message);
    }

    public MultiFieldException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiFieldException(Throwable cause) {
        super(cause);
    }
}
