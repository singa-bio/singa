package bio.singa.mathematics.graphs.trees;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BinaryTreeTest {

    @Test
    void shouldAppendNode() {
        BinaryTree<String> tree = new BinaryTree<>(new BinaryTreeNode<>("Root"));
        tree.appendNodeTo(tree.getRoot(), "Left");
        tree.appendNodeTo(tree.getRoot(), "Right");
        tree.appendNodeTo("Left", "ChildOfLeft");
        assertTrue(tree.containsNode("Root"));
        assertTrue(tree.containsNode("Right"));
        assertTrue(tree.containsNode("ChildOfLeft"));
    }

}
