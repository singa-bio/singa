package bio.singa.mathematics.graphs.trees;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author cl
 */
class NewickParserTest {

    @Test
    @DisplayName("newick parsing - trivial tree")
    void test1() {
        String newick = "(A,B)";
        BinaryTreeNode<String> treeNode = NewickParser.parseNewick(newick);
        assertEquals(newick, treeNode.toNewickString());
    }

    @Test
    @DisplayName("newick parsing - dangling node")
    void test2() {
        String newick = "((A,B),C)";
        BinaryTreeNode<String> treeNode = NewickParser.parseNewick(newick);
        assertEquals(newick, treeNode.toNewickString());
    }

    @Test
    @DisplayName("newick parsing - complex tree")
    void test3() {
        String newick = "((A,B),((C,D),(E,F)))";
        BinaryTreeNode<String> treeNode = NewickParser.parseNewick(newick);
        assertEquals(newick, treeNode.toNewickString());
    }
}