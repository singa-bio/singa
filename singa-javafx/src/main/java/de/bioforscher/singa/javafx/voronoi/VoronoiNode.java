package de.bioforscher.singa.javafx.voronoi;

import de.bioforscher.singa.javafx.voronoi.representation.Edge;

/**
 * Some kind of RB Tree
 */
public class VoronoiNode {

    private Site site;
    private Edge edge;

    private CircleEvent circleEvent;

    private VoronoiNode root;

    private VoronoiNode rbNext;
    private VoronoiNode rbPrevious;

    private VoronoiNode rbRight;
    private VoronoiNode rbLeft;

    private VoronoiNode rbParent;

    private boolean rbRed;

    public VoronoiNode() {

    }

    public VoronoiNode(Site site) {
        this.site = site;
    }

    public void insertSuccessor(VoronoiNode node, VoronoiNode successor) {
        VoronoiNode parent;
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
        VoronoiNode grandpa;
        VoronoiNode uncle;
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

    public void removeNode(VoronoiNode node) {
        // caching
        if (node.rbNext != null) {
            node.rbNext.rbPrevious = node.rbPrevious;
        }
        if (node.rbPrevious != null) {
            node.rbPrevious.rbNext = node.rbNext;
        }
        node.rbNext = null;
        node.rbPrevious = null;

        VoronoiNode parent = node.rbParent;
        VoronoiNode left = node.rbLeft;
        VoronoiNode right = node.rbRight;
        VoronoiNode next;

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
        VoronoiNode sibling;
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

    private void rotateRight(VoronoiNode node) {
        VoronoiNode p = node;
        VoronoiNode q = node.rbLeft;
        VoronoiNode parent = p.rbParent;

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

    private void rotateLeft(VoronoiNode node) {
        VoronoiNode p = node;
        VoronoiNode q = node.rbRight;
        VoronoiNode parent = p.rbParent;

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

    private VoronoiNode getFirst(VoronoiNode node) {
        while (node.rbLeft != null) {
            node = node.rbLeft;
        }
        return node;
    }

    private VoronoiNode getLast(VoronoiNode node) {
        while (node.rbRight != null) {
            node = node.rbRight;
        }
        return node;
    }

    public VoronoiNode getRoot() {
        return this.root;
    }

    public void setRoot(VoronoiNode root) {
        this.root = root;
    }

    public Site getSite() {
        return this.site;
    }

    public VoronoiNode getRbRight() {
        return this.rbRight;
    }

    public void setRbRight(VoronoiNode rbRight) {
        this.rbRight = rbRight;
    }

    public VoronoiNode getRbLeft() {
        return this.rbLeft;
    }

    public void setRbLeft(VoronoiNode rbLeft) {
        this.rbLeft = rbLeft;
    }

    public VoronoiNode getRbParent() {
        return this.rbParent;
    }

    public void setRbParent(VoronoiNode rbParent) {
        this.rbParent = rbParent;
    }

    public VoronoiNode getRbNext() {
        return this.rbNext;
    }

    public VoronoiNode getRbPrevious() {
        return this.rbPrevious;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public void setRbNext(VoronoiNode rbNext) {
        this.rbNext = rbNext;
    }

    public void setRbPrevious(VoronoiNode rbPrevious) {
        this.rbPrevious = rbPrevious;
    }

    public boolean isRbRed() {
        return this.rbRed;
    }

    public void setRbRed(boolean rbRed) {
        this.rbRed = rbRed;
    }

    public CircleEvent getCircleEvent() {
        return this.circleEvent;
    }

    public void setCircleEvent(CircleEvent circleEvent) {
        this.circleEvent = circleEvent;
    }

    public Edge getEdge() {
        return this.edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    @Override
    public String toString() {
        return "VoronoiNode{" +
                "site=" + site +
                ", leftSite=" + (rbLeft == null ? "none":rbLeft.site) +
                ", rightSite=" + (rbRight == null ? "none":rbRight.site) +
                '}';
    }
}
