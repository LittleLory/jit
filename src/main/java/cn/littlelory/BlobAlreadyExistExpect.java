package cn.littlelory;

public class BlobAlreadyExistExpect extends RuntimeException {
    public BlobAlreadyExistExpect(String message) {
        super(message);
    }
}
