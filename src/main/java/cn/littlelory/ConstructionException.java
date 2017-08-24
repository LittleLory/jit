package cn.littlelory;

/**
 * Created by littlelory on 2017/8/24.
 */
public class ConstructionException extends RuntimeException {

    public ConstructionException() {
    }

    public ConstructionException(String message) {
        super(message);
    }

    public ConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstructionException(Throwable cause) {
        super(cause);
    }
}
