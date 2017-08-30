package cn.littlelory;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by littlelory on 29/08/2017.
 */
public class ObjectTypeTest {
    @Test
    public void get_object_type_by_id() {
        Optional<JitBlobType> optional = Arrays.stream(JitBlobType.values()).filter(type -> type.name().equals("OBJECT")).findFirst();
        assertTrue(optional.isPresent());
        JitBlobType expect = optional.get();
        int typeId = expect.getId();

        JitBlobType actual = JitBlobType.getTypeById(typeId);
        assertEquals(expect, actual);
    }

    @Test
    public void get_tree_type_by_id() {
        Optional<JitBlobType> optional = Arrays.stream(JitBlobType.values()).filter(type -> type.name().equals("TREE")).findFirst();
        assertTrue(optional.isPresent());
        JitBlobType expect = optional.get();
        int typeId = expect.getId();

        JitBlobType actual = JitBlobType.getTypeById(typeId);
        assertEquals(expect, actual);
    }

    @Test(expected = RuntimeException.class)
    public void get_other_type_by_id() {
        int typeId = 3;
        JitBlobType.getTypeById(typeId);
    }
}
