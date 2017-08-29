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
    public void get_node_type_by_id() {
        Optional<JitObjectType> optional = Arrays.stream(JitObjectType.values()).filter(type -> type.name().equals("NODE")).findFirst();
        assertTrue(optional.isPresent());
        JitObjectType expect = optional.get();
        int typeId = expect.getId();

        JitObjectType actual = JitObjectType.getTypeById(typeId);
        assertEquals(expect, actual);
    }

    @Test
    public void get_tree_type_by_id() {
        Optional<JitObjectType> optional = Arrays.stream(JitObjectType.values()).filter(type -> type.name().equals("TREE")).findFirst();
        assertTrue(optional.isPresent());
        JitObjectType expect = optional.get();
        int typeId = expect.getId();

        JitObjectType actual = JitObjectType.getTypeById(typeId);
        assertEquals(expect, actual);
    }

    @Test(expected = RuntimeException.class)
    public void get_other_type_by_id() {
        int typeId = 3;
        JitObjectType.getTypeById(typeId);
    }
}
