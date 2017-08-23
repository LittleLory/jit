package cn.littlelory;

/**
 * Created by littlelory on 2017/8/23.
 */
public class NoBeanAnntationFoundException extends RuntimeException {
    public NoBeanAnntationFoundException() {
    }

    public NoBeanAnntationFoundException(String message) {
        super(message);
    }

    public NoBeanAnntationFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoBeanAnntationFoundException(Throwable cause) {
        super(cause);
    }
}
