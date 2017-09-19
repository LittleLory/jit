package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by littlelory on 18/09/2017.
 */
public class BeanManagerTest {
    private BeanManager beanManager;
    private String basePath;

    @Before
    public void init() throws IOException {
        basePath = TestUtil.resourcesPath() + "/data/bean";
        beanManager = new BeanManager(basePath);
        Files.list(Paths.get(basePath + "/NormalJitBean")).forEach(f -> {
            try {
                Files.delete(f.toAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void save() {
        NormalJitBean bean = new NormalJitBean();
        bean.setKey("1");
        bean.setF1(2);
        bean.setF2(3L);

        beanManager.save(bean);

        assertTrue(Files.exists(Paths.get(basePath + "/NormalJitBean")));
        assertTrue(Files.exists(Paths.get(basePath + "/NormalJitBean/1")));

    }

    @Test
    public void delete() throws BeanNotExistException, IOException {
        byte[] bytes = new byte[]{
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0D, 0x4E, 0x6F, 0x72, 0x6D, 0x61, 0x6C, 0x4A, 0x69, 0x74,
                0x42, 0x65, 0x61, 0x6E, 0x00, 0x00, 0x00, 0x01, 0x32, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03
        };
        Path path = Paths.get(basePath + "/NormalJitBean/2");
        Files.write(path, bytes);

        NormalJitBean bean = beanManager.delete(NormalJitBean.class, "2");
        assertNotNull(bean);
        assertEquals("2", bean.getKey());
        assertEquals(2, bean.getF1());
        assertEquals(3L, bean.getF2());
        assertTrue(!Files.exists(path));
    }

    @Test
    public void get() throws IOException, BeanNotExistException {
        byte[] bytes = new byte[]{
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0D, 0x4E, 0x6F, 0x72, 0x6D, 0x61, 0x6C, 0x4A, 0x69, 0x74,
                0x42, 0x65, 0x61, 0x6E, 0x00, 0x00, 0x00, 0x01, 0x32, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03
        };
        Path path = Paths.get(basePath + "/NormalJitBean/2");
        Files.write(path, bytes);

        NormalJitBean bean = beanManager.get(NormalJitBean.class, "2");
        assertNotNull(bean);
        assertEquals("2", bean.getKey());
    }

    @Test
    public void get_a_not_exist_bean() throws BeanNotExistException {
        assertNull(beanManager.get(NormalJitBean.class, "111"));
    }

    @Test
    public void getList() throws IOException {
        byte[] bytes = new byte[]{
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0D, 0x4E, 0x6F, 0x72, 0x6D, 0x61, 0x6C, 0x4A, 0x69, 0x74,
                0x42, 0x65, 0x61, 0x6E, 0x00, 0x00, 0x00, 0x01, 0x32, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x04,
                0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03
        };
        Path path = Paths.get(basePath + "/NormalJitBean/2");
        Files.write(path, bytes);

        List<NormalJitBean> list = beanManager.getList(NormalJitBean.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("2", list.get(0).getKey());
    }
}
