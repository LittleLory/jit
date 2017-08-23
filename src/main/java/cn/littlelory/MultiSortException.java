package cn.littlelory;

/**
 * Created by littlelory on 2017/8/23.
 */
public class MultiSortException extends RuntimeException {
    public MultiSortException() {
    }

    public MultiSortException(String message) {
        super(message);
    }

    public MultiSortException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultiSortException(Throwable cause) {
        super(cause);
    }
}
