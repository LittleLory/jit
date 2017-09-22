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

        Jit.Status status = jit.status();
        List<StatusInfo> unAddStatus = status.getUnAdd();
        List<StatusInfo> addedStatus = status.getAdded();
        assertEquals(1, unAddStatus.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.UNTRACKED),unAddStatus.get(0));
        assertEquals(0, addedStatus.size());

        jit.add("NormalJitBean/1");

        status = jit.status();
        unAddStatus = status.getUnAdd();
        addedStatus = status.getAdded();
        assertEquals(0, unAddStatus.size());
        assertEquals(1, addedStatus.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.ADDED),addedStatus.get(0));

        jit.commit();

        status = jit.status();
        unAddStatus = status.getUnAdd();
        addedStatus = status.getAdded();
        assertEquals(0, unAddStatus.size());
        assertEquals(0, addedStatus.size());

        bean.setF1(3);
        jit.save(bean);

        status = jit.status();
        unAddStatus = status.getUnAdd();
        addedStatus = status.getAdded();
        assertEquals(1, unAddStatus.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.MODIFITED),unAddStatus.get(0));
        assertEquals(0, addedStatus.size());

        jit.delete(NormalJitBean.class, "1");

        status = jit.status();
        unAddStatus = status.getUnAdd();
        addedStatus = status.getAdded();
        assertEquals(1, unAddStatus.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.DELETE),unAddStatus.get(0));
        assertEquals(0, addedStatus.size());

        jit.add("NormalJitBean/1");

        status = jit.status();
        unAddStatus = status.getUnAdd();
        addedStatus = status.getAdded();
        assertEquals(0, unAddStatus.size());
        assertEquals(1, addedStatus.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.DELETE),addedStatus.get(0));

        String oldHead = jit.head();

        jit.commit();

        status = jit.status();
        unAddStatus = status.getUnAdd();
        addedStatus = status.getAdded();
        assertEquals(0, unAddStatus.size());
        assertEquals(0, addedStatus.size());

        jit.reset(oldHead);

        status = jit.status();
        unAddStatus = status.getUnAdd();
        addedStatus = status.getAdded();
        assertEquals(1, unAddStatus.size());
        assertEquals(new StatusInfo("NormalJitBean/1", StatusInfo.Status.DELETE),unAddStatus.get(0));
        assertEquals(0, addedStatus.size());

        jit.checkout();
        status = jit.status();
        unAddStatus = status.getUnAdd();
        addedStatus = status.getAdded();
        assertEquals(0, unAddStatus.size());
        assertEquals(0, addedStatus.size());
        assertTrue(Files.exists(Paths.get(basePath + "/NormalJitBean/1")));

        NormalJitBean actual = BeanSerializer.getInstance().decode(NormalJitBean.class, Files.readAllBytes(Paths.get(basePath + "/NormalJitBean/1")));

        NormalJitBean expect = new NormalJitBean();
        expect.setKey("1");
        expect.setF1(2);
        expect.setF2(3L);
        assertEquals(expect, actual);

    }

    @Test
    public void test2() {
        String path = "abc/def.txt";
        int index = path.lastIndexOf('/');
        System.out.println(path.substring(0, index));
        System.out.println(path.substring(index + 1));
    }
}
