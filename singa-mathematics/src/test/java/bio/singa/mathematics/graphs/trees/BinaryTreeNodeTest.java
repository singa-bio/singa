package bio.singa.mathematics.graphs.trees;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author cl
 */
class BinaryTreeNodeTest {

    // first tree
    //   A
    //  / \
    // B   C
    //    / \
    //   D   E
    //  / \
    // F   G
    private static BinaryTreeNode<String> firstTree;

    @BeforeAll
    static void initialize() {
        BinaryTreeNode<String> g = new BinaryTreeNode<>("G");
        BinaryTreeNode<String> f = new BinaryTreeNode<>("F");
        BinaryTreeNode<String> e = new BinaryTreeNode<>("E");
        BinaryTreeNode<String> d = new BinaryTreeNode<>("D", f, g);
        BinaryTreeNode<String> c = new BinaryTreeNode<>("C", d, e);
        BinaryTreeNode<String> b = new BinaryTreeNode<>("B");
        firstTree = new BinaryTreeNode<>("A", b, c);
    }


    @Test
    @DisplayName("binary tree - path to data")
    void pathTo() {
        List<BinaryTreeNode<String>> path = firstTree.pathTo("G");
        Iterator<BinaryTreeNode<String>> iterator = path.iterator();
        assertEquals(iterator.next().getData(), "A");
        assertEquals(iterator.next().getData(), "C");
        assertEquals(iterator.next().getData(), "D");
        assertEquals(iterator.next().getData(), "G");
        assertFalse(iterator.hasNext());
    }

    @Test
    @DisplayName("binary tree - get all leaf data")
    void getAllLeafs() {
        Collection<String> leafData = firstTree.getLeafData();
        assertTrue(leafData.contains("B"));
        assertTrue(leafData.contains("F"));
        assertTrue(leafData.contains("G"));
        assertTrue(leafData.contains("E"));
        assertEquals(4, leafData.size());
    }

    @Test
    @DisplayName("binary tree - get all data")
    void getAllData() {
        Collection<String> leafData = firstTree.getAllData();
        System.out.println(leafData);
        assertTrue(leafData.contains("A"));
        assertTrue(leafData.contains("B"));
        assertTrue(leafData.contains("C"));
        assertTrue(leafData.contains("D"));
        assertTrue(leafData.contains("E"));
        assertTrue(leafData.contains("F"));
        assertTrue(leafData.contains("G"));
        assertEquals(7, leafData.size());
    }

    @Test
    @DisplayName("binary tree - copy")
    void copy() {
        BinaryTreeNode<String> copy = firstTree.copy();
        assertEquals(firstTree, copy);
        assertNotSame(firstTree, copy);
    }
}