package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by littlelory on 31/08/2017.
 */
public class IndexTest {
    private String indexPath;
    private Index index;

    @Before
    public void init() throws IOException {
        indexPath = TestUtil.resourcesPath() + "data/manager/index";
        System.out.println("indexPath:" + indexPath);

        byte[] bytes = new byte[]{
                0x0,0x0,0x0,0x2,//length = 2

                0x0,0x0,0x0,0x35,//first entry size
                0x0,0x0,0x0,0x28,//fingerprint length = 40
                0x38, 0x62, 0x61, 0x65, 0x66, 0x31, 0x62, 0x34, 0x61, 0x62, 0x63, 0x34, 0x37, 0x38, 0x31, 0x37, 0x38,
                0x62, 0x30, 0x30, 0x34, 0x64, 0x36, 0x32, 0x30, 0x33, 0x31, 0x63, 0x66, 0x37, 0x66, 0x65, 0x36, 0x64,
                0x62, 0x36, 0x66, 0x39, 0x30, 0x33,//fingerprint = 8baef1b4abc478178b004d62031cf7fe6db6f903
                0x0,0x0,0x0,0x5,//pathname length = 5
                0x61, 0x2E, 0x74, 0x78, 0x74,//pathname =  a.txt

                0x0,0x0,0x0,0x39,//second entry size
                0x0,0x0,0x0,0x28,//fingerprint length = 40
                0x37, 0x32, 0x39, 0x34, 0x33, 0x61, 0x31, 0x36, 0x66, 0x62, 0x32, 0x63, 0x38, 0x66, 0x33, 0x38, 0x66,
                0x39, 0x64, 0x64, 0x65, 0x32, 0x30, 0x32, 0x62, 0x37, 0x61, 0x37, 0x30, 0x63, 0x63, 0x63, 0x31, 0x39,
                0x63, 0x35, 0x32, 0x66, 0x33, 0x34,//fingerprint = 72943a16fb2c8f38f9dde202b7a70ccc19c52f34
                0x0,0x0,0x0,0x9,//pathname length = 9
                0x64,0x69,0x72,0x2F,0x62,0x2E,0x74,0x78,0x74,//pathname =  a.txt
        };

        Files.write(Paths.get(indexPath), bytes);

        index = new Index(indexPath);
    }

    @Test
    public void add_a_new_entry() throws IOException {
        index.add("b.txt", "81c545efebe5f57d4cab2ba9ec294c4b0cadf672");
        byte[] actual = readBytesFromIndex();
        byte[] expect = new byte[] {
                0x0,0x0,0x0,0x3,//length = 3

                0x0,0x0,0x0,0x35,//first entry size
                0x0,0x0,0x0,0x28,//fingerprint length = 40
                0x38, 0x62, 0x61, 0x65, 0x66, 0x31, 0x62, 0x34, 0x61, 0x62, 0x63, 0x34, 0x37, 0x38, 0x31, 0x37, 0x38,
                0x62, 0x30, 0x30, 0x34, 0x64, 0x36, 0x32, 0x30, 0x33, 0x31, 0x63, 0x66, 0x37, 0x66, 0x65, 0x36, 0x64,
                0x62, 0x36, 0x66, 0x39, 0x30, 0x33,//fingerprint = 8baef1b4abc478178b004d62031cf7fe6db6f903
                0x0,0x0,0x0,0x5,//pathname length = 5
                0x61, 0x2E, 0x74, 0x78, 0x74,//pathname =  a.txt

                0x0,0x0,0x0,0x35,//second entry size
                0x0,0x0,0x0,0x28,//fingerprint length = 40
                0x38, 0x31, 0x63, 0x35, 0x34, 0x35, 0x65, 0x66, 0x65, 0x62, 0x65, 0x35, 0x66, 0x35, 0x37, 0x64, 0x34,
                0x63, 0x61, 0x62, 0x32, 0x62, 0x61, 0x39, 0x65, 0x63, 0x32, 0x39, 0x34, 0x63, 0x34, 0x62, 0x30, 0x63,
                0x61, 0x64, 0x66, 0x36, 0x37, 0x32,//fingerprint = 81c545efebe5f57d4cab2ba9ec294c4b0cadf672
                0x0,0x0,0x0,0x5,//pathname length = 5
                0x62, 0x2E, 0x74, 0x78, 0x74,//pathname =  b.txt

                0x0,0x0,0x0,0x39,//third entry size
                0x0,0x0,0x0,0x28,//fingerprint length = 40
                0x37, 0x32, 0x39, 0x34, 0x33, 0x61, 0x31, 0x36, 0x66, 0x62, 0x32, 0x63, 0x38, 0x66, 0x33, 0x38, 0x66,
                0x39, 0x64, 0x64, 0x65, 0x32, 0x30, 0x32, 0x62, 0x37, 0x61, 0x37, 0x30, 0x63, 0x63, 0x63, 0x31, 0x39,
                0x63, 0x35, 0x32, 0x66, 0x33, 0x34,//fingerprint = 72943a16fb2c8f38f9dde202b7a70ccc19c52f34
                0x0,0x0,0x0,0x9,//pathname length = 9
                0x64,0x69,0x72,0x2F,0x62,0x2E,0x74,0x78,0x74,//pathname =  dir/a.txt
        };

        TestUtil.assertBytesEquals(expect, actual);
    }

    @Test(expected = IndexAlreadyExistExpetion.class)
    public void add_an_exist_entry() {
        index.add("a.txt", "72943a16fb2c8f38f9dde202b7a70ccc19c52f34");
    }

    @Test
    public void update_an_entry() throws IOException {
        index.update("a.txt", "81c545efebe5f57d4cab2ba9ec294c4b0cadf672");
        byte[] actual = readBytesFromIndex();
        byte[] expect = new byte[]{
                0x0,0x0,0x0,0x2,//length = 2

                0x0,0x0,0x0,0x35,//first entry size
                0x0,0x0,0x0,0x28,//fingerprint length = 40
                0x38, 0x31, 0x63, 0x35, 0x34, 0x35, 0x65, 0x66, 0x65, 0x62, 0x65, 0x35, 0x66, 0x35, 0x37, 0x64, 0x34,
                0x63, 0x61, 0x62, 0x32, 0x62, 0x61, 0x39, 0x65, 0x63, 0x32, 0x39, 0x34, 0x63, 0x34, 0x62, 0x30, 0x63,
                0x61, 0x64, 0x66, 0x36, 0x37, 0x32,//fingerprint = 81c545efebe5f57d4cab2ba9ec294c4b0cadf672
                0x0,0x0,0x0,0x5,//pathname length = 5
                0x61, 0x2E, 0x74, 0x78, 0x74,//pathname =  a.txt

                0x0,0x0,0x0,0x39,//second entry size
                0x0,0x0,0x0,0x28,//fingerprint length = 40
                0x37, 0x32, 0x39, 0x34, 0x33, 0x61, 0x31, 0x36, 0x66, 0x62, 0x32, 0x63, 0x38, 0x66, 0x33, 0x38, 0x66,
                0x39, 0x64, 0x64, 0x65, 0x32, 0x30, 0x32, 0x62, 0x37, 0x61, 0x37, 0x30, 0x63, 0x63, 0x63, 0x31, 0x39,
                0x63, 0x35, 0x32, 0x66, 0x33, 0x34,//fingerprint = 72943a16fb2c8f38f9dde202b7a70ccc19c52f34
                0x0,0x0,0x0,0x9,//pathname length = 9
                0x64,0x69,0x72,0x2F,0x62,0x2E,0x74,0x78,0x74,//pathname =  a.txt
        };

        TestUtil.assertBytesEquals(expect, actual);
    }

    @Test(expected = NoSuchIndexException.class)
    public void update_a_not_exist_entry() {
        index.update("c.txt", "81c545efebe5f57d4cab2ba9ec294c4b0cadf672");
    }

    @Test
    public void remove_an_entry() throws IOException {
        index.remove("a.txt");
        byte[] actual = readBytesFromIndex();
        byte[] expect = new byte[]{
                0x0,0x0,0x0,0x1,//length = 2

                0x0,0x0,0x0,0x39,//second entry size
                0x0,0x0,0x0,0x28,//fingerprint length = 40
                0x37, 0x32, 0x39, 0x34, 0x33, 0x61, 0x31, 0x36, 0x66, 0x62, 0x32, 0x63, 0x38, 0x66, 0x33, 0x38, 0x66,
                0x39, 0x64, 0x64, 0x65, 0x32, 0x30, 0x32, 0x62, 0x37, 0x61, 0x37, 0x30, 0x63, 0x63, 0x63, 0x31, 0x39,
                0x63, 0x35, 0x32, 0x66, 0x33, 0x34,//fingerprint = 72943a16fb2c8f38f9dde202b7a70ccc19c52f34
                0x0,0x0,0x0,0x9,//pathname length = 9
                0x64,0x69,0x72,0x2F,0x62,0x2E,0x74,0x78,0x74,//pathname =  a.txt
        };

        TestUtil.assertBytesEquals(expect, actual);
    }

    @Test(expected = NoSuchIndexException.class)
    public void remove_a_not_exist_entry() {
        index.remove("c.txt");
    }


    private byte[] readBytesFromIndex() throws IOException {
        return Files.readAllBytes(Paths.get(indexPath));
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();


    public String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append("0x");
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
            r.append(", ");
        }
        return r.toString();
    }

    @Test
    public void test() {
        System.out.println(printHexBinary("81c545efebe5f57d4cab2ba9ec294c4b0cadf672".getBytes()));
    }
}
