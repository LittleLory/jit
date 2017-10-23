package cn.littlelory;

import java.util.logging.Logger;

/**
 * Created by littlelory on 13/09/2017.
 */
public class BlobUtil {
    private static final Logger logger = Logger.getLogger("BlobUtil");

    public static String writeOBlob(String objectsDirPath, JitBlob object) {
        byte[] bytes = object.encode();
        String fingerprint = Fingerprint.generate(bytes);
        String firstDir = objectsDirPath + "/" + fingerprint.substring(0, 2);
        FileUtil.mkdirIfNotExist(firstDir);
        String objectPath = firstDir + "/" + fingerprint.substring(2);
        if (FileUtil.exist(objectPath)) {
            FileUtil.writeBytes("tmp/wrong", bytes);
            throw new BlobAlreadyExistExpect("blob["+fingerprint+"] exist, write this blob to tmp/wrong");
        }
        logger.info("write object[" + object.getPathname() + "] to path[" + objectPath + "].");
        FileUtil.writeBytes(objectPath, bytes);
        return fingerprint;
    }

    public static JitBlob loadBlob(String objectsDirPath, String fingerprint, JitBlobType type) {
        String blobPath = objectsDirPath + "/" + fingerprint.substring(0, 2) + "/" + fingerprint.substring(2);

        JitBlob blob;
        if (type == JitBlobType.OBJECT)
            blob = new JitObject();
        else if (type == JitBlobType.TREE)
            blob = new JitTree();
        else
            throw new RuntimeException("wrong jit blob type[" + type + "].");

        blob.decode(FileUtil.readBytes(blobPath));
        return blob;
    }

    public static boolean isBlobExist(String objectsDirPath, String fingerprint) {
        return FileUtil.exist(objectsDirPath + "/" + fingerprint.substring(0, 2) + "/" + fingerprint.substring(2));
    }
}
