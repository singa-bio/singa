package bio.singa.mathematics.graphs.trees;

import java.io.Serializable;

public class BinaryTreeNode<T> implements Serializable {

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
        left = leftNode;
        right = rightNode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BinaryTreeNode<T> getLeft() {
        return left;
    }

    public void setLeft(BinaryTreeNode<T> left) {
        this.left = left;
    }

    public BinaryTreeNode<T> getRight() {
        return right;
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
        if (left != null && right != null) {
            return 1 + left.size() + right.size();
        }
        if (left != null) {
            return 1 + left.size();
        }
        if (right != null) {
            return 1 + right.size();
        }
        return 1;
    }

    public String toStringInOrder() {
        String leftS = "";
        String rightS = "";
        if (left != null) {
            leftS = left.toStringInOrder();
        }
        if (right != null) {
            rightS = right.toStringInOrder();
        }
        return leftS + data.toString() + " " + rightS;
    }

    public String toStringPostOrder() {
        String leftString = "";
        String rightString = "";
        if (left != null) {
            leftString = left.toStringPostOrder();
        }
        if (right != null) {
            rightString = right.toStringPostOrder();
        }
        return leftString + rightString + data.toString() + " ";
    }

    public String toNewickString() {
        String leftString = "";
        String rightString = "";
        if (left != null) {
            leftString = left.toNewickString();
        }
        if (right != null) {
            rightString = right.toNewickString();
        }
        if (left == null && right == null) {
            return leftString + data.toString() + rightString;
        } else {
            return "(" + leftString + "," + rightString + ")";
        }
    }

    @Override
    public String toString() {
        return data + ": " + toNewickString();
    }

}
