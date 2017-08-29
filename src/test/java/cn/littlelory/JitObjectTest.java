package cn.littlelory;

import org.junit.Test;

import java.util.List;

import static cn.littlelory.TestUtil.assertBytesEquals;
import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.

/**
 * Created by littlelory on 2017/8/28.
 */
public class JitObjectTest {

    @Test
    public void node_encode() {
        String pathname = "/dir/a.file";
        byte[] data = new byte[]{0x74, 0x65, 0x73, 0x74};

        JitNode node = new JitNode(pathname, data);
        byte[] actual = node.encode();

        byte[] expect = new byte[]{
                0x0, 0x0, 0x0, 0x2,//object type id
                0x0, 0x0, 0x0, 0xb,//object pathname length
                0x2F, 0x64, 0x69, 0x72, 0x2F, 0x61, 0x2E, 0x66, 0x69, 0x6C, 0x65,//object pathname
                0x0, 0x0, 0x0, 0x4,//object body length
                0x74, 0x65, 0x73, 0x74//object body
        };

        assertEquals(expect.length, actual.length);
        assertBytesEquals(expect, actual);
    }

    @Test
    public void node_decode() {
        byte[] bytes = new byte[]{
                0x0, 0x0, 0x0, 0x2,//object type id
                0x0, 0x0, 0x0, 0xb,//object pathname length
                0x2F, 0x64, 0x69, 0x72, 0x2F, 0x61, 0x2E, 0x66, 0x69, 0x6C, 0x65,//object pathname
                0x0, 0x0, 0x0, 0x4,//object body length
                0x74, 0x65, 0x73, 0x74//object body
        };

        JitNode node = new JitNode();
        node.decode(bytes);

        assertEquals(2, node.getType().getId());
        assertEquals("/dir/a.file", node.getPathname());
        assertBytesEquals(new byte[]{0x74, 0x65, 0x73, 0x74}, node.getData());
    }

    @Test
    public void type_of_node_bytes() {
        byte[] bytes = new byte[]{
                0x0, 0x0, 0x0, 0x2,//object type id
                0x0, 0x0, 0x0, 0xb,//object pathname length
                0x2F, 0x64, 0x69, 0x72, 0x2F, 0x61, 0x2E, 0x66, 0x69, 0x6C, 0x65,//object pathname
                0x0, 0x0, 0x0, 0x4,//object body length
                0x74, 0x65, 0x73, 0x74//object body
        };

        assertEquals(JitObjectType.NODE, JitObject.typeOf(bytes));
    }

    @Test
    public void tree_encode() {
        String treePathname = "/dir";
        JitTree tree = new JitTree(treePathname);

        JitObjectType nodeType = JitObjectType.NODE;
        String nodePathname = "/dir/a.file";
        String fingerprint = "finger";

        tree.addChild(nodeType, nodePathname, fingerprint);

//        tree.addChild(node);
        byte[] actual = tree.encode();

        byte[] expect = new byte[]{
                0x0, 0x0, 0x0, 0x1,//object type id
                0x0, 0x0, 0x0, 0x4,//object pathname length
                0x2F,0x64,0x69,0x72,//object pathname
                0x0, 0x0, 0x0, 0x25,//object body length
                    //tree
                    0x0, 0x0, 0x0, 0x1,//children count
                        //node
                        0x0, 0x0, 0x0, 0x1d,//node length
                        0x0, 0x0, 0x0, 0x2,//node type id
                        0x0, 0x0, 0x0, 0xb,//pathname length
                        0x2F, 0x64, 0x69, 0x72, 0x2F, 0x61, 0x2E, 0x66, 0x69, 0x6C, 0x65,//pathname
                        0x0, 0x0, 0x0, 0x6,//fingerprint length
                        0x66,0x69,0x6E,0x67,0x65,0x72//fingerprint
        };

        assertBytesEquals(expect, actual);
    }

    @Test
    public void tree_decode() {
        byte[] bytes = new byte[]{
                0x0, 0x0, 0x0, 0x1,//object type id
                0x0, 0x0, 0x0, 0x4,//object pathname length
                0x2F,0x64,0x69,0x72,//object pathname
                0x0, 0x0, 0x0, 0x25,//object body length
                //tree
                0x0, 0x0, 0x0, 0x1,//children count
                //node
                0x0, 0x0, 0x0, 0x1d,//node length
                0x0, 0x0, 0x0, 0x2,//node type id
                0x0, 0x0, 0x0, 0xb,//pathname length
                0x2F, 0x64, 0x69, 0x72, 0x2F, 0x61, 0x2E, 0x66, 0x69, 0x6C, 0x65,//pathname
                0x0, 0x0, 0x0, 0x6,//fingerprint length
                0x66,0x69,0x6E,0x67,0x65,0x72//fingerprint
        };

        JitTree tree = new JitTree();
        tree.decode(bytes);

        assertEquals(1, tree.getType().getId());
        assertEquals("/dir", tree.getPathname());
        List<JitTree.Child> children = tree.getChildren();
        assertEquals(1, children.size());
        JitTree.Child child = children.get(0);
        assertEquals(2, child.getType().getId());
        assertEquals("/dir/a.file", child.getPathname());
        assertEquals("finger", child.getFingerprint());
    }
}
