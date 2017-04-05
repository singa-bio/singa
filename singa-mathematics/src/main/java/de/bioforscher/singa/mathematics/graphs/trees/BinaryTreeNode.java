package de.bioforscher.singa.mathematics.graphs.trees;

import java.io.Serializable;

public class BinaryTreeNode<T> implements Serializable{

    private static final long serialVersionUID = -6444789577851989492L;

    private T data;
    private BinaryTreeNode<T> left;
    private BinaryTreeNode<T> right;

    public BinaryTreeNode() {
        this(null, null);
    }

    public BinaryTreeNode(T data) {
        this(data, null, null);
    }

    public BinaryTreeNode(BinaryTreeNode<T> leftNode, BinaryTreeNode<T> rightNode) {
        this(null, leftNode, rightNode);
    }

    public BinaryTreeNode(T data, BinaryTreeNode<T> leftNode, BinaryTreeNode<T> rightNode) {
        this.data = data;
        this.left = leftNode;
        this.right = rightNode;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BinaryTreeNode<T> getLeft() {
        return this.left;
    }

    public void setLeft(BinaryTreeNode<T> left) {
        this.left = left;
    }

    public BinaryTreeNode<T> getRight() {
        return this.right;
    }

    public void setRight(BinaryTreeNode<T> right) {
        this.right = right;
    }

    public BinaryTreeNode<T> findNode(T data) {
        return findNode(data, this);
    }

    private BinaryTreeNode<T> findNode(T data, BinaryTreeNode<T> node) {
        if (node != null) {
            if (node.getData().equals(data)) {
                return node;
            } else {
                BinaryTreeNode<T> foundNode = findNode(data, node.left);
                if (foundNode == null) {
                    foundNode = findNode(data, node.right);
                }
                return foundNode;
            }
        } else {
            return null;
        }

    }

    public int size() {
        if (this.left != null && this.right != null) {
            return 1 + this.left.size() + this.right.size();
        }
        if (this.left != null) {
            return 1 + this.left.size();
        }
        if (this.right != null) {
            return 1 + this.right.size();
        }
        return 1;
    }

    public String toStringInOrder() {
        String leftS = "";
        String rightS = "";
        if (this.left != null) {
            leftS = this.left.toStringInOrder();
        }
        if (this.right != null) {
            rightS = this.right.toStringInOrder();
        }
        return leftS + this.data.toString() + " " + rightS;
    }

    public String toStringPostOrder() {
        String leftString = "";
        String rightString = "";
        if (this.left != null) {
            leftString = this.left.toStringPostOrder();
        }
        if (this.right != null) {
            rightString = this.right.toStringPostOrder();
        }
        return leftString + rightString + this.data.toString() + " ";
    }

    public String toNewickString() {
        String leftString = "";
        String rightString = "";
        if (this.left != null) {
            leftString = this.left.toNewickString();
        }
        if (this.right != null) {
            rightString = this.right.toNewickString();
        }
        if (this.left == null && this.right == null) {
            return leftString + this.data.toString() + rightString;
        } else {
            return "(" + leftString + "," + rightString + ")";
        }
    }

    @Override
    public String toString() {
        return this.data + ": " + toNewickString();
    }

}
