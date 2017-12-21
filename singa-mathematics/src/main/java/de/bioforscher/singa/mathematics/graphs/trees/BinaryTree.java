package de.bioforscher.singa.mathematics.graphs.trees;

import de.bioforscher.singa.mathematics.matrices.LabeledSymmetricMatrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BinaryTree<T> implements Serializable {

    private static final long serialVersionUID = 7841821945964241393L;

    private BinaryTreeNode<T> root;
    private LabeledSymmetricMatrix<String> distanceMatrix;

    public BinaryTree(BinaryTreeNode<T> root) {
        this.root = root;
    }

    public LabeledSymmetricMatrix<String> getDistanceMatrix() {
        return distanceMatrix;
    }

    public void setDistanceMatrix(LabeledSymmetricMatrix<String> distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    /**
     * Traverses the tree in pre order and collects leave nodes.
     *
     * @param startingNode the starting node
     * @param leaveNodes the storage for the leave nodes
     */
    public void collectLeavesPreOrder(BinaryTreeNode<T> startingNode, List<BinaryTreeNode<T>> leaveNodes) {
        if (startingNode != null) {
            BinaryTreeNode<T> left = startingNode.getLeft();
            BinaryTreeNode<T> right = startingNode.getRight();
            if (left == null && right == null) {
                leaveNodes.add(startingNode);
            }
            collectLeavesPreOrder(left, leaveNodes);
            collectLeavesPreOrder(right, leaveNodes);
        }
    }

    /**
     * Traverses the tree in pre order and collects nodes.
     *
     * @param startingNode the starting node
     * @param nodes the storage for the nodes
     */
    public void collectNodesPreOrder(BinaryTreeNode<T> startingNode, List<BinaryTreeNode<T>> nodes) {
        if (startingNode != null) {
            BinaryTreeNode<T> left = startingNode.getLeft();
            BinaryTreeNode<T> right = startingNode.getRight();
            nodes.add(startingNode);
            collectNodesPreOrder(left, nodes);
            collectNodesPreOrder(right, nodes);
        }
    }

    /**
     * Returns all the leave nodes in the tree (every call to this method will traverse the entire tree).
     *
     * @return list of leave nodes
     */
    public List<BinaryTreeNode<T>> getLeafNodes() {
        List<BinaryTreeNode<T>> leaveNodes = new ArrayList<>();
        collectLeavesPreOrder(root, leaveNodes);
        return leaveNodes;
    }


    public BinaryTreeNode<T> getRoot() {
        return root;
    }

    public void setRoot(BinaryTreeNode<T> root) {
        this.root = root;
    }

    public BinaryTreeNode<T> findNode(T data) {
        return root.findNode(data);
    }

    public int size() {
        return root.size();
    }

    /**
     * Returns a formatted String formatted in Newick that contains the leaves.
     *
     * @return a formatted String formatted in Newick that contains the leaves.
     */
    public String toNewickString() {
        return root.toNewickString() + ";";
    }

    public boolean containsNode(T nodeData) {
        return findNode(nodeData) != null;
    }

    public void appendNodeTo(BinaryTreeNode<T> parentNode, T childData) {
        appendNodeTo(parentNode.getData(), childData);
    }

    public void appendNodeTo(T parentData, T childData) {
        BinaryTreeNode<T> parentNode = findNode(parentData);
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
        return root.toStringInOrder();
    }

}
