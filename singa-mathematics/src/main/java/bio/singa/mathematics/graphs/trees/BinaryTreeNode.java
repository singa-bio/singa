package bio.singa.mathematics.graphs.trees;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

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

    public void addLeft(T data) {
        left = new BinaryTreeNode<>(data);
    }

    public BinaryTreeNode<T> getRight() {
        return right;
    }

    public void setRight(BinaryTreeNode<T> right) {
        this.right = right;
    }

    public void addRight(T data) {
        right = new BinaryTreeNode<>(data);
    }

    public Collection<T> getLeafData() {
        ArrayList<T> result = new ArrayList<>();
        return getLeafData(this, result);
    }

    private Collection<T> getLeafData(BinaryTreeNode<T> currentNode, Collection<T> previousData) {
        if (currentNode == null) {
            return previousData;
        }
        if (currentNode.hasLeft()) {
            previousData = getLeafData(currentNode.getLeft(), previousData);
        }
        if (currentNode.hasRight()) {
            previousData = getLeafData(currentNode.getRight(), previousData);
        }
        if (!currentNode.hasLeft() && !currentNode.hasRight()) {
            previousData.add(currentNode.getData());
        }
        return previousData;
    }

    public Collection<T> getAllData() {
        ArrayList<T> result = new ArrayList<>();
        return getAllData(this, result);
    }

    private Collection<T> getAllData(BinaryTreeNode<T> currentNode, Collection<T> previousData) {
        if (currentNode == null) {
            return previousData;
        }
        if (currentNode.hasLeft()) {
            previousData = getAllData(currentNode.getLeft(), previousData);
        }
        if (currentNode.hasRight()) {
            previousData = getAllData(currentNode.getRight(), previousData);
        }
        previousData.add(currentNode.getData());
        return previousData;
    }

    public List<BinaryTreeNode<T>> pathTo(T data) {
        LinkedList<BinaryTreeNode<T>> currentPath = new LinkedList<>();
        currentPath.add(this);
        return pathTo(data, this, currentPath);
    }

    private LinkedList<BinaryTreeNode<T>> pathTo(T data, BinaryTreeNode<T> currentNode, LinkedList<BinaryTreeNode<T>> currentPath) {
        if (currentNode == null) {
            return null;
        }
        if (!currentNode.hasLeft() && !currentNode.hasRight()) {
            // neither left nor right
            currentPath.removeLast();
        }
        if (currentNode.hasLeft()) {
            currentPath.addLast(currentNode.getLeft());
            if (currentNode.getLeft().getData().equals(data)) {
                return currentPath;
            }
            currentPath = pathTo(data, currentNode.getLeft(), currentPath);
        }
        if (currentNode.hasRight()) {
            currentPath.addLast(currentNode.getRight());
            if (currentNode.getRight().getData().equals(data)) {
                return currentPath;
            }
            currentPath = pathTo(data, currentNode.getRight(), currentPath);
        }
        return currentPath;
    }

    public BinaryTreeNode<T> find(T data) {
        return find(data, this);
    }

    private BinaryTreeNode<T> find(T data, BinaryTreeNode<T> node) {
        if (node == null) {
            return null;
        }
        if (node.getData().equals(data)) {
            return node;
        }
        BinaryTreeNode<T> foundNode = find(data, node.getLeft());
        if (foundNode == null) {
            foundNode = find(data, node.getRight());
        }
        return foundNode;

    }

    public void substitute(T target, T replacement) {
        substitute(find(target), new BinaryTreeNode<>(replacement));
    }

    public void substitute(BinaryTreeNode<T> target, BinaryTreeNode<T> replacement) {
        substitute(target, replacement, this);
    }

    private void substitute(BinaryTreeNode<T> target, BinaryTreeNode<T> replacement, BinaryTreeNode<T> reference) {
        if (reference.hasLeft()) {
            if (reference.getLeft().getData().equals(target.getData())) {
                reference.setLeft(replacement);
            }
            substitute(target, replacement, reference.getLeft());
        }
        if (reference.hasRight()) {
            if (reference.getRight().getData().equals(target.getData())) {
                reference.setRight(replacement);
            }
            substitute(target, replacement, reference.getRight());
        }
    }

    public BinaryTreeNode<T> copy() {
        BinaryTreeNode<T> copy = new BinaryTreeNode<>(data);
        if (hasLeft()) {
            copy.setLeft(left.copy());
        }
        if (hasRight()) {
            copy.setRight(right.copy());
        }
        return copy;
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }

    public int size() {
        if (hasLeft() && hasRight()) {
            return 1 + left.size() + right.size();
        }
        if (hasLeft()) {
            return 1 + left.size();
        }
        if (hasRight()) {
            return 1 + right.size();
        }
        return 1;
    }

    public String toStringInOrder() {
        String leftS = "";
        String rightS = "";
        if (hasLeft()) {
            leftS = left.toStringInOrder();
        }
        if (hasRight()) {
            rightS = right.toStringInOrder();
        }
        return leftS + data.toString() + " " + rightS;
    }

    public String toStringPostOrder() {
        String leftString = "";
        String rightString = "";
        if (hasLeft()) {
            leftString = left.toStringPostOrder();
        }
        if (hasRight()) {
            rightString = right.toStringPostOrder();
        }
        return leftString + rightString + data.toString() + " ";
    }

    public String toNewickString(Function<T, String> extractor) {
        String leftString = "";
        String rightString = "";
        if (hasLeft()) {
            leftString = left.toNewickString(extractor);
        }
        if (hasRight()) {
            rightString = right.toNewickString(extractor);
        }
        if (!hasLeft() && !hasRight()) {
            return leftString + extractor.apply(data) + rightString;
        } else {
            return "(" + leftString + "," + rightString + ")";
        }
    }

    @Override
    public String toString() {
        return data + ": " + toNewickString(Objects::toString);
    }

    public void print() {
        print("", true);
    }

    private void print(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + getData());
        String currentPrefix = prefix + (isTail ? "    " : "│   ");
        if (hasLeft()) {
            if (hasRight()) {
                getLeft().print(currentPrefix, false);
            } else {
                getLeft().print(currentPrefix, true);
            }
        }
        if (hasRight()) {
            getRight().print(currentPrefix, true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryTreeNode<?> that = (BinaryTreeNode<?>) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
