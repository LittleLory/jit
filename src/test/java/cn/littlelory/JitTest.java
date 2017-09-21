package cn.littlelory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by littlelory on 18/09/2017.
 */
public class JitTest {
    private String basePath;
    private Jit jit;

    @Before
    public void init() throws IOException {
        this.basePath = TestUtil.resourcesPath() + "data/jit";

        TestUtil.deleteIfExists(Paths.get(this.basePath + "/.jit"));
        TestUtil.deleteIfExists(Paths.get(this.basePath + "/NormalJitBean"));
        TestUtil.deleteIfExists(Paths.get(this.basePath));

        Files.createDirectory(Paths.get(this.basePath));

        this.jit = new Jit(basePath);
    }

    @Test
    public void test() throws IOException, BeanNotExistException {
        jit.init();

        NormalJitBean bean = new NormalJitBean();
        bean.setKey("1");
        bean.setF1(2);
        bean.setF2(3L);

        jit.save(bean);

        NormalJitBean getResult = jit.get(NormalJitBean.class, "1");
        assertEquals(bean, getResult);

        List<NormalJitBean> getListResult = jit.getList(NormalJitBean.class);
        assertEquals(1, getListResult.size());
        assertEquals(bean, getListResult.get(0));

        List<StatusInfo> statusInfoList = jit.status();
        assertEquals(1, statusInfoList.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.UNTRACKED),statusInfoList.get(0));

        jit.add("NormalJitBean/1");

        statusInfoList = jit.status();
        assertEquals(1, statusInfoList.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.ADDED),statusInfoList.get(0));

        jit.commit();

        statusInfoList = jit.status();
        assertEquals(0, statusInfoList.size());

        bean.setF1(3);
        jit.save(bean);

        statusInfoList = jit.status();
        assertEquals(1, statusInfoList.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.MODIFITED),statusInfoList.get(0));

        jit.delete(NormalJitBean.class, "1");

        statusInfoList = jit.status();
        assertEquals(1, statusInfoList.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.DELETE),statusInfoList.get(0));
    }

    @Test
    public void test2() {
        String path = "abc/def.txt";
        int index = path.lastIndexOf('/');
        System.out.println(path.substring(0, index));
        System.out.println(path.substring(index + 1));
    }
}
