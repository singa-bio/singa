package bio.singa.chemistry.entities.graphcomplex;

import java.util.List;

/**
 * @author cl
 */
public class BindModification {

    private BindingSite bindingSite;
    private List<GraphComplex> results;

    public BindModification(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    public void setResultList(List<GraphComplex> results) {
        this.results = results;
    }

    public void apply(GraphComplex first, GraphComplex second) {
        first.bind(second, bindingSite).ifPresent(results::add);
    }

}
