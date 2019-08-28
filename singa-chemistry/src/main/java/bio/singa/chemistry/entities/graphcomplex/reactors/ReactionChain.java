package bio.singa.chemistry.entities.graphcomplex.reactors;

import bio.singa.chemistry.entities.graphcomplex.GraphComplex;
import bio.singa.core.utility.ListHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class ReactionChain {

    private String identifier;
    private List<ComplexReactor> reactors;
    private List<List<List<GraphComplex>>> paths;
    private List<ReactionElement> reactantElements;

    public ReactionChain(List<ComplexReactor> reactors) {
        this.reactors = reactors;
        reactantElements = new ArrayList<>();
        paths = new ArrayList<>();
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

    public List<List<List<GraphComplex>>> getPaths() {
        return paths;
    }

    public List<ReactionElement> getReactantElements() {
        return reactantElements;
    }

    public void process(List<GraphComplex> availableEntities) {
        List<GraphComplex> next = new ArrayList<>(availableEntities);
        for (ComplexReactor reactor : reactors) {
            reactor.collectCandidates(next);
            reactor.apply();
            List<ReactionElement> products = reactor.getProducts();
            expandPath(products);
            next = products.stream()
                    .flatMap(product -> product.getProducts().stream())
                    .collect(Collectors.toList());
        }
        collectReactantElements();
    }

    private void expandPath(List<ReactionElement> reactionElements) {
        for (ReactionElement element : reactionElements) {
            List<GraphComplex> substrates = element.getSubstrates();
            List<GraphComplex> products = element.getProducts();
            if (paths.isEmpty()) {
                // add initial track
                appendFreshPath(substrates, products);
            } else {
                // look for the right track
                List<List<GraphComplex>> relevantTrack = null;
                for (List<List<GraphComplex>> currentTrack : paths) {
                    List<GraphComplex> lastList = currentTrack.get(currentTrack.size() - 1);
                    if (ListHelper.haveSameElements(lastList, substrates)) {
                        relevantTrack = currentTrack;
                        break;
                    }
                }
                if (relevantTrack != null) {
                    relevantTrack.add(products);
                } else {
                    appendFreshPath(substrates, products);
                }
            }
        }
    }

    private void appendFreshPath(List<GraphComplex> substrates, List<GraphComplex> products) {
        List<List<GraphComplex>> track = new ArrayList<>();
        track.add(substrates);
        track.add(products);
        paths.add(track);
    }


    private void collectReactantElements() {
        for (List<List<GraphComplex>> currentTrack : paths) {
            int size = currentTrack.size();
            List<GraphComplex> substrates = currentTrack.get(0);
            List<GraphComplex> products = currentTrack.get(size-1);
            reactantElements.add(new ReactionElement(substrates, products));
        }
    }

}
