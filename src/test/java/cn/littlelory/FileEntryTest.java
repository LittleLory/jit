package cn.littlelory;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
/**
 * Created by littlelory on 07/09/2017.
 */
public class FileEntryTest {
    @Test
    public void equals() {
        FileEntry entry1 = new FileEntry("dir/object1", "9dff22578761e09c7fcaad5186f563df1b3365fb");
        FileEntry entry2 = new FileEntry("dir/object1", "9dff22578761e09c7fcaad5186f563df1b3365fb");
        assertTrue(entry1.equals(entry2));
    }
}
