package de.bioforscher.mathematics.graphs.trees;

public class MultiTree<T> {

    private MultiTreeNode<T> root;

    public MultiTree(T rootData) {
        this.root = new MultiTreeNode<T>(rootData);
    }

    public MultiTreeNode<T> getRoot() {
        return this.root;
    }

    public void setRoot(MultiTreeNode<T> root) {
        this.root = root;
    }

    public void addChildToRoot(MultiTreeNode<T> child) {
        this.root.addChild(child);
    }

    public void traversePreOrder(MultiTreeNode<T> node) {
        if (node == null) {
            return;
        }
        System.out.println(node.getData().toString()); // visit() here
        for (MultiTreeNode<T> n : node.getChildren()) {
            traversePreOrder(n);
        }
    }

    public void traverseInOrder(MultiTreeNode<T> node) {
        if (node == null) {
            return;
        }
        for (MultiTreeNode<T> n : node.getChildren()) {
            traverseInOrder(n);
            System.out.println(node.getData().toString()); // visit() here
        }
    }

}
