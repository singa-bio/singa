package de.bioforscher.singa.mathematics.graphs.model;

import java.util.*;

/**
 * @author cl
 */
public class GraphPath<NodeType> implements List<NodeType>, Deque<NodeType> {

    private LinkedList<NodeType> path;

    public GraphPath() {
        path = new LinkedList<>();
    }

    public GraphPath(LinkedList<NodeType> path) {
        this.path = path;
    }

    @Override
    public void addFirst(NodeType node) {
        path.addFirst(node);
    }

    @Override
    public void addLast(NodeType node) {
        path.addLast(node);
    }

    @Override
    public boolean offerFirst(NodeType node) {
        return path.offerFirst(node);
    }

    @Override
    public boolean offerLast(NodeType node) {
        return path.offerLast(node);
    }

    @Override
    public NodeType removeFirst() {
        return path.removeFirst();
    }

    @Override
    public NodeType removeLast() {
        return path.removeLast();
    }

    @Override
    public NodeType pollFirst() {
        return path.pollFirst();
    }

    @Override
    public NodeType pollLast() {
        return path.pollLast();
    }

    @Override
    public NodeType getFirst() {
        return path.getFirst();
    }

    @Override
    public NodeType getLast() {
        return path.getLast();
    }

    @Override
    public NodeType peekFirst() {
        return path.peekFirst();
    }

    @Override
    public NodeType peekLast() {
        return path.peekLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return path.removeFirstOccurrence(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return path.removeLastOccurrence(o);
    }

    @Override
    public boolean offer(NodeType node) {
        return path.offer(node);
    }

    @Override
    public NodeType remove() {
        return path.remove();
    }

    @Override
    public NodeType poll() {
        return path.poll();
    }

    @Override
    public NodeType element() {
        return path.element();
    }

    @Override
    public NodeType peek() {
        return path.peek();
    }

    @Override
    public void push(NodeType node) {
        path.push(node);
    }

    @Override
    public NodeType pop() {
        return path.pop();
    }

    @Override
    public Iterator<NodeType> descendingIterator() {
        return path.descendingIterator();
    }

    @Override
    public int size() {
        return path.size();
    }

    @Override
    public boolean isEmpty() {
        return path.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return path.contains(o);
    }

    @Override
    public Iterator<NodeType> iterator() {
        return path.iterator();
    }

    @Override
    public Object[] toArray() {
        return path.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return path.toArray(a);
    }

    @Override
    public boolean add(NodeType node) {
        return path.add(node);
    }

    @Override
    public boolean remove(Object o) {
        return path.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return path.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends NodeType> c) {
        return path.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends NodeType> c) {
        return path.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return path.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return path.retainAll(c);
    }

    @Override
    public void clear() {
        path.clear();
    }

    @Override
    public NodeType get(int index) {
        return path.get(index);
    }

    @Override
    public NodeType set(int index, NodeType node) {
        return path.set(index, node);
    }

    @Override
    public void add(int index, NodeType node) {
        path.add(index, node);
    }

    @Override
    public NodeType remove(int index) {
        return path.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return path.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return path.lastIndexOf(o);
    }

    @Override
    public ListIterator<NodeType> listIterator() {
        return path.listIterator();
    }

    @Override
    public ListIterator<NodeType> listIterator(int index) {
        return path.listIterator(index);
    }

    @Override
    public List<NodeType> subList(int fromIndex, int toIndex) {
        return path.subList(fromIndex, toIndex);
    }

}
