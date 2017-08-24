package cn.littlelory;

/**
 * Created by littlelory on 2017/8/24.
 */
public class BeanNotMatchException extends RuntimeException {
    public BeanNotMatchException() {
    }

    public BeanNotMatchException(String message) {
        super(message);
    }

    public BeanNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanNotMatchException(Throwable cause) {
        super(cause);
    }
}
