package bio.singa.mathematics.graphs.trees;

import java.util.ArrayList;
import java.util.List;

public class MultiTreeNode<T> {

    private T data;
    private MultiTreeNode<T> parent;
    private List<MultiTreeNode<T>> children;

    public MultiTreeNode(T data) {
        this.data = data;
        children = new ArrayList<>();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public MultiTreeNode<T> getParent() {
        return parent;
    }

    public void setParent(MultiTreeNode<T> parent) {
        this.parent = parent;
    }

    public void addChild(MultiTreeNode<T> child) {
        children.add(child);
    }

    public MultiTreeNode<T> getChild(T data) {
        for (MultiTreeNode<T> node : children) {
            if (node.getData().equals(data)) {
                return node;
            }
        }
        return null;
    }

    public List<MultiTreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<MultiTreeNode<T>> children) {
        this.children = children;
    }

}
