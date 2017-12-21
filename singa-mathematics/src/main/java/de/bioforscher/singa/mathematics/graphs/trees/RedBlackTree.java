package de.bioforscher.singa.mathematics.graphs.trees;

/**
 * A implementation where you can manually handle insertions in a red black tree. The tree is kept balanced during its
 * life time. The previous and next node are cached.
 *
 * @author cl
 */
public class RedBlackTree {

    private RedBlackNode root;

    public RedBlackTree() {
    }

    public RedBlackTree(RedBlackNode root) {
        this.root = root;
    }

    public RedBlackNode getRoot() {
        return root;
    }

    public void setRoot(RedBlackNode root) {
        this.root = root;
    }
}
