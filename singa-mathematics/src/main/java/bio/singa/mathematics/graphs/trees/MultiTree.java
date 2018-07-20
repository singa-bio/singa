package bio.singa.mathematics.graphs.trees;

public class MultiTree<T> {

    private MultiTreeNode<T> root;

    public MultiTree(T rootData) {
        root = new MultiTreeNode<>(rootData);
    }

    public MultiTreeNode<T> getRoot() {
        return root;
    }

    public void setRoot(MultiTreeNode<T> root) {
        this.root = root;
    }

    public void addChildToRoot(MultiTreeNode<T> child) {
        root.addChild(child);
    }

    public void traversePreOrder(MultiTreeNode<T> node) {
        if (node == null) {
            return;
        }
        // visit() here
        node.getChildren().forEach(this::traversePreOrder);
    }

    public void traverseInOrder(MultiTreeNode<T> node) {
        if (node == null) {
            return;
        }
        for (MultiTreeNode<T> n : node.getChildren()) {
            traverseInOrder(n);
            // visit() here
        }
    }

}
