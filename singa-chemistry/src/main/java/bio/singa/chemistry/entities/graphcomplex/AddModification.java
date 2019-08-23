package bio.singa.chemistry.entities.graphcomplex;

import java.util.List;

/**
 * @author cl
 */
public class AddModification {

    private BindingSite bindingSite;
    private GraphComplex complexToAdd;
    private List<GraphComplex> results;

    public AddModification(GraphComplex complexToAdd, BindingSite bindingSite) {
        this.complexToAdd = complexToAdd;
        this.bindingSite = bindingSite;
    }

    public void setResultList(List<GraphComplex> results) {
        this.results = results;
    }

    public void apply(GraphComplex complex) {
        complex.bind(complexToAdd, bindingSite).ifPresent(results::add);
    }

}
