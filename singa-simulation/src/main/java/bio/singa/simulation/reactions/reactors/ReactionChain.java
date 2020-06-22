package bio.singa.simulation.reactions.reactors;

import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.core.utility.ListHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ReactionChain {

    private String identifier;
    private List<ComplexReactor> reactors;
    private List<ReactionTrack> tracks;
    private Set<ReactionElement> reactantElements;
    private boolean considerInversion;

    private List<ComplexReactor> invertedReactors;

    public ReactionChain(List<ComplexReactor> reactors) {
        this.reactors = reactors;
        invertedReactors = new ArrayList<>();
        reactantElements = new HashSet<>();
        tracks = new ArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<ComplexReactor> getReactors() {
        return reactors;
    }

    public List<ReactionTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<ReactionTrack> tracks) {
        this.tracks = tracks;
    }

    public Set<ReactionElement> getReactantElements() {
        return reactantElements;
    }

    public void process(Collection<ComplexEntity> availableEntities) {
        processReactors(availableEntities, reactors);
        sealTracks();
        if (considerInversion) {
            if (invertedReactors.isEmpty()) {
                invertReactors();
            }
            processReactors(availableEntities, invertedReactors);
            invertUnsealedTracks();
            sealTracks();
        }
        collectReactantElements();
        tracks.clear();
    }

    private void processReactors(Collection<ComplexEntity> availableEntities, List<ComplexReactor> reactors) {
        List<ComplexEntity> next = new ArrayList<>(availableEntities);
        for (ComplexReactor reactor : reactors) {
            reactor.collectCandidates(next);
            reactor.apply();
            List<ReactionElement> products = reactor.getProducts();
            expandPath(products);
            next = products.stream()
                    .flatMap(product -> product.getProducts().stream())
                    .collect(Collectors.toList());
            reactor.clear();
        }
    }

    private void expandPath(List<ReactionElement> reactionElements) {
        for (ReactionElement element : reactionElements) {
            List<ComplexEntity> substrates = element.getSubstrates();
            List<ComplexEntity> products = element.getProducts();
            if (tracks.isEmpty()) {
                // add initial track
                initializeTrack(substrates, products);
            } else {
                // look for the right track
                ReactionTrack relevantTrack = null;
                for (ReactionTrack currentTrack : tracks) {
                    if (currentTrack.isSealed()) {
                        continue;
                    }
                    if (ListHelper.haveSameElements(currentTrack.getLast(), substrates)) {
                        relevantTrack = currentTrack;
                        break;
                    }
                }
                if (relevantTrack != null) {
                    relevantTrack.append(products);
                } else {
                    initializeTrack(substrates, products);
                }
            }
        }
    }

    private void initializeTrack(List<ComplexEntity> substrates, List<ComplexEntity> products) {
        ReactionTrack track = new ReactionTrack();
        track.append(substrates);
        track.append(products);
        tracks.add(track);
    }

    private void collectReactantElements() {
        for (ReactionTrack track : tracks) {
            List<ComplexEntity> substrates = track.getFirst();
            List<ComplexEntity> products = track.getLast();
            ReactionElement newElement = null;
            if (track.isInverse()) {
                newElement = new ReactionElement(products, substrates);
            } else {
                newElement = new ReactionElement(substrates, products);
            }
            if (reactantElements.contains(newElement)) {
                continue;
            }
            reactantElements.add(newElement);
        }
    }

    private void invertUnsealedTracks() {
        for (ReactionTrack track : tracks) {
            if (!track.isSealed()) {
                track.setInverse(true);
            }
        }
    }

    private void sealTracks() {
        for (ReactionTrack track : tracks) {
            track.seal();
        }
    }

    private void invertReactors() {
        ListIterator<ComplexReactor> reactorIterator = reactors.listIterator(reactors.size());
        while (reactorIterator.hasPrevious()) {
            ComplexReactor reactor = reactorIterator.previous();
            invertedReactors.add(reactor.invert());
        }
    }

    public boolean isConsiderInversion() {
        return considerInversion;
    }

    public void setConsiderInversion(boolean considerInversion) {
        this.considerInversion = considerInversion;
    }
}
