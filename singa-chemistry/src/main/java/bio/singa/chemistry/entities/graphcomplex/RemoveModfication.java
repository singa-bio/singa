package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.entities.ChemicalEntity;

import java.util.List;

/**
 * @author cl
 */
public class RemoveModfication {

    private BindingSite bindingSite;
    private ChemicalEntity entityToRemove;
    private List<GraphComplex> results;

    public RemoveModfication(ChemicalEntity entityToRemove, BindingSite bindingSite) {
        this.entityToRemove = entityToRemove;
        this.bindingSite = bindingSite;
    }

    public void setResultList(List<GraphComplex> results) {
        this.results = results;
    }

    public void apply(GraphComplex complex) {
        complex.remove(entityToRemove, bindingSite).ifPresent(results::add);
    }

}
