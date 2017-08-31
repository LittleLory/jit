package cn.littlelory;

/**
 * Created by littlelory on 31/08/2017.
 */
public class IndexAlreadyExistExpetion extends RuntimeException {
    public IndexAlreadyExistExpetion(String message) {
        super(message);
    }
}
