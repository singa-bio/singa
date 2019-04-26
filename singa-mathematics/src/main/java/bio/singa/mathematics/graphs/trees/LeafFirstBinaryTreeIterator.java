package bio.singa.mathematics.graphs.trees;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author cl
 */
public class LeafFirstBinaryTreeIterator<T> implements Iterator<BinaryTreeNode<T>> {

    private final Deque<BinaryTreeNode<T>> deque;

    public LeafFirstBinaryTreeIterator(BinaryTreeNode<T> root) {
        deque = new ArrayDeque<>();
        if (root != null) {
            fillStack(root);
        }
    }

    private void fillStack(BinaryTreeNode<T> binaryTreeNode) {
        binaryTreeNode.inOrderTraversal(deque::push);
    }

    @Override
    public boolean hasNext() {
        return !deque.isEmpty();
    }

    @Override
    public BinaryTreeNode<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return deque.pollLast();
    }

}
