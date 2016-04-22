package de.bioforscher.mathematics.graphs.trees;

public class BinaryTree<T> {

    private BinaryTreeNode<T> root;

    public BinaryTree(BinaryTreeNode<T> root) {
        this.root = root;
    }

    public BinaryTreeNode<T> getRoot() {
        return this.root;
    }

    public void setRoot(BinaryTreeNode<T> root) {
        this.root = root;
    }

    public BinaryTreeNode<T> findNode(T data) {
        return this.root.findNode(data);
    }

    public int size() {
        return this.root.size();
    }

    /**
     * Returns a formatted String that contains only leaves.
     *
     * @return
     */
    public String toNewickString() {
        return this.root.toNewickString() + ";";
    }

    public boolean containsNode(T nodeData) {
        if (findNode(nodeData) != null) {
            return true;
        }
        return false;
    }

    public void appendNodeTo(BinaryTreeNode<T> parentNode, T childData) {
        appendNodeTo(parentNode.getData(), childData);
    }

    public void appendNodeTo(T parentData, T childData) {
        BinaryTreeNode<T> parentNode = this.findNode(parentData);
        if (parentNode.getLeft() != null && parentNode.getRight() != null) {
            throw new IllegalStateException(
                    "Both child nodes of " + parentNode + "are occupied. Unable to append additional child.");
        }
        if (parentNode.getLeft() == null) {
            parentNode.setLeft(new BinaryTreeNode<T>(childData));
        } else {
            parentNode.setRight(new BinaryTreeNode<T>(childData));
        }
    }

    @Override
    public String toString() {
        return this.root.toStringInOrder();
    }

}
