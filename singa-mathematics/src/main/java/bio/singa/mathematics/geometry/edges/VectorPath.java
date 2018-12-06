package bio.singa.mathematics.geometry.edges;

import bio.singa.mathematics.vectors.Vector2D;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author cl
 */
public class VectorPath {

    protected LinkedList<Vector2D> segments;

    public VectorPath() {
        segments = new LinkedList<>();
    }

    public VectorPath(List<Vector2D> segments) {
        this.segments = new LinkedList<>(segments);
    }

    VectorPath(LinkedList<Vector2D> segments) {
        segments = segments;
    }

    public LinkedList<Vector2D> getSegments() {
        return segments;
    }

    public void setSegments(LinkedList<Vector2D> segments) {
        this.segments = segments;
    }

    public ListIterator<Vector2D> getSegmentIterator(Vector2D segment) {
        int index = segments.indexOf(segment);
        if (index == -1 ) {
            throw new IndexOutOfBoundsException("The requested vector could not be found.");
        }
        return segments.listIterator(index);
    }

    public int getIndexOf(Vector2D vector) {
        return segments.indexOf(vector);
    }

    public Vector2D getVectorAt(int index) {
        return segments.get(index);
    }

    public Vector2D getNextVectorOf(Vector2D vector) {
        int index = getIndexOf(vector);
        if (index == -1 ) {
            throw new IndexOutOfBoundsException("The requested vector could not be found.");
        }
        if (index == segments.size()-1) {
            throw new IndexOutOfBoundsException("The requested vector is already the head vector.");
        }
        return getVectorAt(index + 1);
    }

    public Vector2D getPreviousVectorOf(Vector2D vector) {
        int index = getIndexOf(vector);
        if (index == -1 ) {
            throw new IndexOutOfBoundsException("The requested vector could not be found.");
        }
        if (index == 0) {
            throw new IndexOutOfBoundsException("The requested vector is already the tail vector.");
        }
        return getVectorAt(index - 1);
    }

    public Vector2D getHead() {
        return segments.getFirst();
    }

    public void addToHead(Vector2D vector) {
        segments.addFirst(vector);
    }

    public void removeHead() {
        segments.removeFirst();
    }

    public Vector2D getTail() {
        return segments.getLast();
    }

    public void addToTail(Vector2D vector) {
        segments.addLast(vector);
    }

    public void removeTail() {
        segments.removeLast();
    }


    public int size() {
        return segments.size();
    }

    public VectorPath reduce() {
        return VectorPaths.reduce(this);
    }

    public VectorPath scale(double factor) {
        return VectorPaths.scale(this, factor);
    }


}
