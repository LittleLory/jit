package cn.littlelory;

/**
 * Created by littlelory on 2017/8/23.
 */
public class NoKeyFoundException extends RuntimeException {
    public NoKeyFoundException() {
        super();
    }

    public NoKeyFoundException(String message) {
        super(message);
    }

    public NoKeyFoundException(String message, Throwable cause) {
        super(message, cause);
    }


    public NoKeyFoundException(Throwable cause) {
        super(cause);
    }
}
