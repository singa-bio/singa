package bio.singa.chemistry.entities.graphcomplex;

import java.util.List;

/**
 * @author cl
 */
public class ReleaseModification {

    private BindingSite bindingSite;
    private List<GraphComplex> results;

    public ReleaseModification(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    public void setResultList(List<GraphComplex> results) {
        this.results = results;
    }

    public void apply(GraphComplex complex) {
        complex.unbind(bindingSite).ifPresent(results::addAll);
    }

}
