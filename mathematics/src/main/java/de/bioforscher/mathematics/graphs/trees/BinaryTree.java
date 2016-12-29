package de.bioforscher.mathematics.graphs.trees;

import java.util.ArrayList;
import java.util.List;

public class BinaryTree<T> {

    private BinaryTreeNode<T> root;

    public BinaryTree(BinaryTreeNode<T> root) {
        this.root = root;
    }

    /**
     * Traverses the tree in pre order and collects leave nodes.
     *
     * @param node       the starting node
     * @param leaveNodes the storage for the leave nodes
     */
    private void collectLeavesPreOrder(BinaryTreeNode<T> node, List<BinaryTreeNode<T>> leaveNodes) {
        if (node != null) {
            BinaryTreeNode<T> left = node.getLeft();
            BinaryTreeNode<T> right = node.getRight();
            if (left == null && right == null) {
                leaveNodes.add(node);
            }
            collectLeavesPreOrder(left, leaveNodes);
            collectLeavesPreOrder(right, leaveNodes);
        }
    }

    /**
     * Returns all the leave nodes in the tree (every call to this method will traverse the entire tree).
     *
     * @return list of leave nodes
     */
    public List<BinaryTreeNode<T>> getLeafNodes() {
        List<BinaryTreeNode<T>> leaveNodes = new ArrayList<>();
        collectLeavesPreOrder(this.root, leaveNodes);
        return leaveNodes;
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
        return findNode(nodeData) != null;
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
            parentNode.setLeft(new BinaryTreeNode<>(childData));
        } else {
            parentNode.setRight(new BinaryTreeNode<>(childData));
        }
    }

    @Override
    public String toString() {
        return this.root.toStringInOrder();
    }

}
