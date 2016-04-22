package de.bioforscher.mathematics.graphs.trees;

import java.util.ArrayList;
import java.util.List;

public class MultiTreeNode<T> {

    private T data;
    private MultiTreeNode<T> parent;
    private List<MultiTreeNode<T>> children;

    public MultiTreeNode(T data) {
        this.data = data;
        this.children = new ArrayList<MultiTreeNode<T>>();
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public MultiTreeNode<T> getParent() {
        return this.parent;
    }

    public void setParent(MultiTreeNode<T> parent) {
        this.parent = parent;
    }

    public void addChild(MultiTreeNode<T> child) {
        this.children.add(child);
    }

    public MultiTreeNode<T> getChild(T data) {
        for (MultiTreeNode<T> node : this.children) {
            if (node.getData().equals(data)) {
                return node;
            }
        }
        return null;
    }

    public List<MultiTreeNode<T>> getChildren() {
        return this.children;
    }

    public void setChildren(List<MultiTreeNode<T>> children) {
        this.children = children;
    }

}
