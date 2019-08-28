package bio.singa.chemistry.entities.graphcomplex.reactors;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;
import bio.singa.core.utility.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public interface ComplexReactor {

    void collectCandidates(List<GraphComplex> substrateCandidates);
    Map.Entry<BindingSite, Pair<ChemicalEntity>> getBindingSite();
    List<ReactionElement> getProducts();
    void apply();

}
