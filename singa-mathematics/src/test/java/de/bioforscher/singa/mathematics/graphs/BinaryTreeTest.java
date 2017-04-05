package de.bioforscher.singa.mathematics.graphs;

import de.bioforscher.singa.mathematics.graphs.trees.BinaryTree;
import de.bioforscher.singa.mathematics.graphs.trees.BinaryTreeNode;
import org.junit.Assert;
import org.junit.Test;

public class BinaryTreeTest {

    @Test
    public void shouldAppendNode() {
        BinaryTree<String> tree = new BinaryTree<>(new BinaryTreeNode<>("Root"));
        tree.appendNodeTo(tree.getRoot(), "Left");
        tree.appendNodeTo(tree.getRoot(), "Right");
        tree.appendNodeTo("Left", "ChildOfLeft");
        Assert.assertTrue(tree.containsNode("Root"));
        Assert.assertTrue(tree.containsNode("Right"));
        Assert.assertTrue(tree.containsNode("ChildOfLeft"));
    }

}
