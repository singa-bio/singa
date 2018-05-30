package de.bioforscher.singa.mathematics.graphs.trees;

/**
 * @author cl
 */
public class RedBlackNode<NodeType extends  RedBlackNode<NodeType>>  {

    private NodeType root;

    private NodeType next;
    private NodeType previous;

    private NodeType right;
    private NodeType left;

    private NodeType parent;

    private boolean isRed;

    public RedBlackNode() {

    }

    public void insertSuccessor(NodeType node, NodeType successor) {
        NodeType parent;
        if (node != null) {
            // caching previous and next nodes for performance
            successor.setPrevious(node);
            successor.setNext(node.getNext());
            if (node.getNext() != null) {
                node.getNext().setPrevious(successor);
            }
            node.setNext(successor);
            // checking right
            if (node.getRight() != null) {
                // in-place expansion
                node = node.getRight();
                while (node.getLeft() != null) {
                    node = node.getLeft();
                }
                node.setLeft(successor);
            } else {
                node.setRight(successor);
            }
            parent = node;
        } else if (root != null) {
            // if node is undefined, the successor must be added to the left most part
            node = getFirst(root);
            // again cache previous and next
            successor.setPrevious(null);
            successor.setNext(node);
            node.setPrevious(successor);
            // add successor
            node.setLeft(successor);
            parent = node;
        } else {
            successor.setPrevious(null);
            successor.setNext(null);
            root = successor;
            parent = null;
        }
        successor.setLeft(null);
        successor.setRight(null);
        successor.setParent(parent);
        successor.setRed(true);

        // Fixup the modified tree by recoloring nodes and performing
        // rotations (2 at most) hence the red-black tree properties are
        // preserved.
        NodeType grandpa;
        NodeType uncle;
        node = successor;
        while (parent != null && parent.isRed()) {
            grandpa = parent.getParent();
            // we really want identity
            if (parent == grandpa.getLeft()) {
                uncle = grandpa.getRight();
                if (uncle != null && uncle.isRed()) {
                    parent.setRed(false);
                    uncle.setRed(false);
                    grandpa.setRed(true);
                    node = grandpa;
                } else {
                    if (node == parent.getRight()) {
                        rotateLeft(parent);
                        node = parent;
                        parent = node.getParent();
                    }
                    parent.setRed(false);
                    grandpa.setRed(true);
                    rotateRight(grandpa);
                }
            } else {
                uncle = grandpa.getLeft();
                if (uncle != null && uncle.isRed()) {
                    parent.setRed(false);
                    uncle.setRed(false);
                    grandpa.setRed(true);
                    node = grandpa;
                } else {
                    if (node == parent.getLeft()) {
                        rotateRight(parent);
                        node = parent;
                        parent = node.getParent();
                    }
                    parent.setRed(false);
                    grandpa.setRed(true);
                    rotateLeft(grandpa);
                }
            }
            parent = node.getParent();
        }
        root.setRed(false);
    }

    public void removeNode(NodeType node) {
        // caching
        if (node.getNext() != null) {
            node.getNext().setPrevious(node.getPrevious());
        }
        if (node.getPrevious() != null) {
            node.getPrevious().setNext(node.getNext());
        }
        node.setNext(null);
        node.setPrevious(null);

        NodeType parent = node.getParent();
        NodeType left = node.getLeft();
        NodeType right = node.getRight();
        NodeType next;

        if (left == null) {
            next = right;
        } else if (right == null) {
            next = left;
        } else {
            next = getFirst(right);
        }

        if (parent != null) {
            if (parent.getLeft() == node) {
                parent.setLeft(next);
            } else {
                parent.setRight(next);
            }
        } else {
            root = next;
        }

        // force red-black rules
        boolean isRed;
        if (left != null && right != null) {
            isRed = next.isRed();
            next.setRed(node.isRed());
            next.setLeft(left);
            left.setParent(next);
            if (next != right) {
                parent = next.getParent();
                next.setParent(node.getParent());
                node = next.getRight();
                parent.setLeft(node);
                next.setRight(right);
                right.setParent(next);
            } else {
                next.setParent(parent);
                parent = next;
                node = next.getRight();
            }
        } else {
            isRed = node.isRed();
            node = next;
        }

        // 'node' is now the sole successor's child and 'parent' its
        // new parent (since the successor can have been moved)
        if (node != null) {
            node.setParent(parent);
        }
        if (isRed) {
            return;
        }
        if (node != null && node.isRed()) {
            node.setRed(false);
            return;
        }
        NodeType sibling;
        do {
            if (node == root) {
                break;
            }
            if (node == parent.getLeft()) {
                sibling = parent.getRight();
                if (sibling.isRed()) {
                    sibling.setRed(false);
                    parent.setRed(true);
                    rotateLeft(parent);
                    sibling = parent.getRight();
                }
                if ((sibling.getLeft() != null && sibling.getLeft().isRed()) ||
                        (sibling.getRight() != null && sibling.getRight().isRed())) {
                    if (sibling.getRight() == null || !sibling.getRight().isRed()) {
                        sibling.getLeft().setRed(false);
                        sibling.setRed(true);
                        rotateRight(sibling);
                        sibling = parent.getRight();
                    }
                    sibling.setRed(parent.isRed());
                    parent.setRed(false);
                    sibling.getRight().setRed(false);
                    rotateLeft(parent);
                    node = root;
                    break;
                }
            } else {
                sibling = parent.getLeft();
                if (sibling.isRed()) {
                    sibling.setRed(false);
                    parent.setRed(true);
                    rotateRight(parent);
                    sibling = parent.getLeft();
                }
                if ((sibling.getLeft() != null && sibling.getLeft().isRed())
                        || (sibling.getRight() != null && sibling.getRight().isRed())) {
                    if (sibling.getLeft() == null || !sibling.getLeft().isRed()) {
                        sibling.getRight().setRed(false);
                        sibling.setRed(true);
                        rotateLeft(sibling);
                        sibling = parent.getLeft();
                    }
                    sibling.setRed(parent.isRed());
                    parent.setRed(false);
                    sibling.getLeft().setRed(false);
                    rotateRight(parent);
                    node = root;
                    break;
                }
            }
            sibling.setRed(true);
            node = parent;
            parent = parent.getParent();
        } while (!node.isRed());
        if (node != null) {
            node.setRed(false);
        }
    }

    private void rotateRight(NodeType node) {
        NodeType q = node.getLeft();
        NodeType parent = node.getParent();

        if (parent != null) {
            if (parent.getLeft() == node) {
                parent.setLeft(q);
            } else {
                parent.setRight(q);
            }
        } else {
            root = q;
        }

        q.setParent(parent);
        node.setParent(q);
        node.setLeft(q.getRight());

        if (node.getLeft() != null) {
            node.getLeft().setParent(node);
        }
        q.setRight(node);
    }

    private void rotateLeft(NodeType node) {
        NodeType p = node;
        NodeType q = node.getRight();
        NodeType parent = p.getParent();

        if (parent != null) {
            if (parent.getLeft() == p) {
                parent.setLeft(q);
            } else {
                parent.setRight(q);
            }
        } else {
            root = q;
        }

        q.setParent(parent);
        p.setParent(q);
        p.setRight(q.getLeft());

        if (p.getRight() != null) {
            p.getRight().setParent(p);
        }
        q.setLeft(p);
    }

    private NodeType getFirst(NodeType node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    private NodeType getLast(NodeType node) {
        while (node.getRight() != null) {
            node = node.getRight();
        }
        return node;
    }

    public NodeType getRoot() {
        return root;
    }

    public void setRoot(NodeType root) {
        this.root = root;
    }

    public NodeType getNext() {
        return next;
    }

    public void setNext(NodeType next) {
        this.next = next;
    }

    public NodeType getPrevious() {
        return previous;
    }

    public void setPrevious(NodeType previous) {
        this.previous = previous;
    }

    public NodeType getRight() {
        return right;
    }

    public void setRight(NodeType right) {
        this.right = right;
    }

    public NodeType getLeft() {
        return left;
    }

    public void setLeft(NodeType left) {
        this.left = left;
    }

    public NodeType getParent() {
        return parent;
    }

    public void setParent(NodeType parent) {
        this.parent = parent;
    }

    public boolean isRed() {
        return isRed;
    }

    public void setRed(boolean red) {
        isRed = red;
    }
    
}
