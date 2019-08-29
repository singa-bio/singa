package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.complex.GraphComplex;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ReactionTrack {

    private Deque<List<GraphComplex>> track;
    private boolean sealed;

    public ReactionTrack() {
        track = new LinkedList<>();
    }

    public Deque<List<GraphComplex>> getTrack() {
        return track;
    }

    public void setTrack(Deque<List<GraphComplex>> track) {
        this.track = track;
    }

    public void append(List<GraphComplex> step) {
        track.addLast(step);
    }

    public List<GraphComplex> getFirst() {
        return track.getFirst();
    }

    public List<GraphComplex> getLast() {
        return track.getLast();
    }

    public boolean isSealed() {
        return sealed;
    }

    public void setSealed(boolean sealed) {
        this.sealed = sealed;
    }

    public void seal() {
        setSealed(true);
    }

    @Override
    public String toString() {
        return track.stream()
                .map(complexes -> complexes.stream()
                        .map(GraphComplex::getIdentifier)
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining(" -> "));
    }
}
