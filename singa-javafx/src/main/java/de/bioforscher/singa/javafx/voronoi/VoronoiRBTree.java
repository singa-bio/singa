package de.bioforscher.singa.javafx.voronoi;

import de.bioforscher.singa.javafx.voronoi.representation.Edge;

/**
 * Some kind of RB Tree
 */
public class VoronoiRBTree {

    private Site site;
    private Edge edge;
    private VoronoiRBTree circleEvent;

    private VoronoiRBTree root;

    private VoronoiRBTree rbNext;
    private VoronoiRBTree rbPrevious;

    private VoronoiRBTree rbRight;
    private VoronoiRBTree rbLeft;

    private VoronoiRBTree rbParent;

    private boolean rbRed;

    public VoronoiRBTree() {

    }

    public VoronoiRBTree(Site site) {
        this.site = site;
    }

    public void insertSuccessor(VoronoiRBTree node, VoronoiRBTree successor) {
        VoronoiRBTree parent;
        if (node != null) {
            // caching previous and next nodes for performance
            successor.rbPrevious = node;
            successor.rbNext = node.rbNext;
            if (node.rbNext != null) {
                node.rbNext.rbPrevious = successor;
            }
            node.rbNext = successor;
            // checking right
            if (node.rbRight != null) {
                // in-place expansion
                node = node.rbRight;
                while (node.rbLeft != null) {
                    node = node.rbLeft;
                }
                node.rbLeft = successor;
            } else {
                node.rbRight = successor;
            }
            parent = node;
        } else if (this.root != null) {
            // if node is undefined, the successor must be added to the left most part
            node = getFirst(this.root);
            // again cache previous and next
            successor.rbPrevious = null;
            successor.rbNext = node;
            node.rbPrevious = successor;
            // add successor
            node.rbLeft = successor;
            parent = node;
        } else {
            successor.rbPrevious = null;
            successor.rbNext = null;
            this.root = successor;
            parent = null;
        }
        successor.rbLeft = null;
        successor.rbRight = null;
        successor.rbParent = parent;
        successor.rbRed = true;

        // Fixup the modified tree by recoloring nodes and performing
        // rotations (2 at most) hence the red-black tree properties are
        // preserved.
        VoronoiRBTree grandpa;
        VoronoiRBTree uncle;
        node = successor;
        while (parent != null && parent.rbRed) {
            grandpa = parent.rbParent;
            // we really want identity
            if (parent == grandpa.rbLeft) {
                uncle = grandpa.rbRight;
                if (uncle != null && uncle.rbRed) {
                    parent.rbRed = false;
                    uncle.rbRed = false;
                    grandpa.rbRed = true;
                    node = grandpa;
                } else {
                    if (node == parent.rbRight) {
                        rotateLeft(parent);
                        node = parent;
                        parent = node.rbParent;
                    }
                    parent.rbRed = false;
                    grandpa.rbRed = true;
                    rotateRight(grandpa);
                }
            } else {
                uncle = grandpa.rbLeft;
                if (uncle != null && uncle.rbRed) {
                    parent.rbRed = false;
                    uncle.rbRed = false;
                    grandpa.rbRed = true;
                    node = grandpa;
                } else {
                    if (node == parent.rbLeft) {
                        rotateRight(parent);
                        node = parent;
                        parent = node.rbParent;
                    }
                    parent.rbRed = false;
                    grandpa.rbRed = true;
                    rotateLeft(grandpa);
                }
            }
            parent = node.rbParent;
        }
        this.root.rbRed = false;
    }

    public void removeNode(VoronoiRBTree node) {
        // caching
        if (node.rbNext != null) {
            node.rbNext.rbPrevious = node.rbPrevious;
        }
        if (node.rbPrevious != null) {
            node.rbPrevious.rbNext = node.rbNext;
        }
        node.rbNext = null;
        node.rbPrevious = null;

        VoronoiRBTree parent = node.rbParent;
        VoronoiRBTree left = node.rbLeft;
        VoronoiRBTree right = node.rbRight;
        VoronoiRBTree next;

        if (left == null) {
            next = right;
        } else if (right == null) {
            next = left;
        } else {
            next = getFirst(right);
        }

        if (parent != null) {
            if (parent.rbLeft == node) {
                parent.rbLeft = next;
            } else {
                parent.rbRight = next;
            }
        } else {
            this.root = next;
        }

        // force red-black rules
        boolean isRed;
        if (left != null && right != null) {
            isRed = next.rbRed;
            next.rbRed = node.rbRed;
            next.rbLeft = left;
            left.rbParent = next;
            if (next != right) {
                parent = next.rbParent;
                next.rbParent = node.rbParent;
                node = next.rbRight;
                parent.rbLeft = node;
                next.rbRight = right;
                right.rbParent = next;
            } else {
                next.rbParent = parent;
                parent = next;
                node = next.rbRight;
            }
        } else {
            isRed = node.rbRed;
            node = next;
        }

        // 'node' is now the sole successor's child and 'parent' its
        // new parent (since the successor can have been moved)
        if (node != null) {
            node.rbParent = parent;
        }
        if (isRed) {
            return;
        }
        if (node != null && node.rbRed) {
            node.rbRed = false;
            return;
        }
        VoronoiRBTree sibling;
        do {
            if (node == this.root) {
                break;
            }
            if (node == parent.rbLeft) {
                sibling = parent.rbRight;
                if (sibling.rbRed) {
                    sibling.rbRed = false;
                    parent.rbRed = true;
                    rotateLeft(parent);
                    sibling = parent.rbRight;
                }
                if ((sibling.rbLeft != null && sibling.rbLeft.rbRed) ||
                        (sibling.rbRight != null && sibling.rbRight.rbRed)) {
                    if (sibling.rbRight == null || !sibling.rbRight.rbRed) {
                        sibling.rbLeft.rbRed = false;
                        sibling.rbRed = true;
                        rotateRight(sibling);
                        sibling = parent.rbRight;
                    }
                    sibling.rbRed = parent.rbRed;
                    parent.rbRed = false;
                    sibling.rbRight.rbRed = false;
                    rotateLeft(parent);
                    node = this.root;
                    break;
                }
            } else {
                sibling = parent.rbLeft;
                if (sibling.rbRed) {
                    sibling.rbRed = false;
                    parent.rbRed = true;
                    rotateRight(parent);
                    sibling = parent.rbLeft;
                }
                if ((sibling.rbLeft != null && sibling.rbLeft.rbRed)
                        || (sibling.rbRight != null && sibling.rbRight.rbRed)) {
                    if (sibling.rbLeft == null || !sibling.rbLeft.rbRed) {
                        sibling.rbRight.rbRed = false;
                        sibling.rbRed = true;
                        rotateLeft(sibling);
                        sibling = parent.rbLeft;
                    }
                    sibling.rbRed = parent.rbRed;
                    parent.rbRed = false;
                    sibling.rbLeft.rbRed = false;
                    rotateRight(parent);
                    node = this.root;
                    break;
                }
            }
            sibling.rbRed = true;
            node = parent;
            parent = parent.rbParent;
        } while (!node.rbRed);
        if (node != null) {
            node.rbRed = false;
        }
    }

    private void rotateRight(VoronoiRBTree node) {
        VoronoiRBTree p = node;
        VoronoiRBTree q = node.rbLeft;
        VoronoiRBTree parent = p.rbParent;

        if (parent != null) {
            if (parent.rbLeft == p) {
                parent.rbLeft = q;
            } else {
                parent.rbRight = q;
            }
        } else {
            this.root = q;
        }

        q.rbParent = parent;
        p.rbParent = q;
        p.rbLeft = q.rbRight;

        if (p.rbLeft != null) {
            p.rbLeft.rbParent = p;
        }
        q.rbRight = p;
    }

    private void rotateLeft(VoronoiRBTree node) {
        VoronoiRBTree p = node;
        VoronoiRBTree q = node.rbRight;
        VoronoiRBTree parent = p.rbParent;

        if (parent != null) {
            if (parent.rbLeft == p) {
                parent.rbLeft = q;
            } else {
                parent.rbRight = q;
            }
        } else {
            this.root = q;
        }

        q.rbParent = parent;
        p.rbParent = q;
        p.rbRight = q.rbLeft;

        if (p.rbRight != null) {
            p.rbRight.rbParent = p;
        }
        q.rbLeft = p;
    }

    private VoronoiRBTree getFirst(VoronoiRBTree node) {
        while (node.rbLeft != null) {
            node = node.rbLeft;
        }
        return node;
    }

    private VoronoiRBTree getLast(VoronoiRBTree node) {
        while (node.rbRight != null) {
            node = node.rbRight;
        }
        return node;
    }

    public VoronoiRBTree getRoot() {
        return this.root;
    }

    public void setRoot(VoronoiRBTree root) {
        this.root = root;
    }

    public Site getSite() {
        return this.site;
    }

    public VoronoiRBTree getRbRight() {
        return this.rbRight;
    }

    public void setRbRight(VoronoiRBTree rbRight) {
        this.rbRight = rbRight;
    }

    public VoronoiRBTree getRbLeft() {
        return this.rbLeft;
    }

    public void setRbLeft(VoronoiRBTree rbLeft) {
        this.rbLeft = rbLeft;
    }

    public VoronoiRBTree getRbParent() {
        return this.rbParent;
    }

    public void setRbParent(VoronoiRBTree rbParent) {
        this.rbParent = rbParent;
    }

    public VoronoiRBTree getRbNext() {
        return this.rbNext;
    }

    public VoronoiRBTree getRbPrevious() {
        return this.rbPrevious;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setRbNext(VoronoiRBTree rbNext) {
        this.rbNext = rbNext;
    }

    public void setRbPrevious(VoronoiRBTree rbPrevious) {
        this.rbPrevious = rbPrevious;
    }

    public boolean isRbRed() {
        return this.rbRed;
    }

    public void setRbRed(boolean rbRed) {
        this.rbRed = rbRed;
    }

    public VoronoiRBTree getCircleEvent() {
        return this.circleEvent;
    }

    public void setCircleEvent(VoronoiRBTree circleEvent) {
        this.circleEvent = circleEvent;
    }

    // circle event Stuff
    // TODO create real class

    private VoronoiRBTree arc;
    private double x = 0;
    private double y = 0;
    private double yCenter = 0;

    public VoronoiRBTree getArc() {
        return this.arc;
    }

    public void setArc(VoronoiRBTree arc) {
        this.arc = arc;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getyCenter() {
        return this.yCenter;
    }

    public void setyCenter(double yCenter) {
        this.yCenter = yCenter;
    }

    public Edge getEdge() {
        return this.edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }
}
