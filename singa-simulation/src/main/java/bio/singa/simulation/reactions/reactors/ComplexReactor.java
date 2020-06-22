package bio.singa.simulation.reactions.reactors;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;
import bio.singa.simulation.reactions.modifications.ComplexEntityModification;
import bio.singa.core.utility.Pair;

import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public interface ComplexReactor {

    void collectCandidates(List<ComplexEntity> substrateCandidates);
    Map.Entry<BindingSite, Pair<ChemicalEntity>> getBindingSite();
    List<ReactionElement> getProducts();
    ComplexEntityModification getModification();

    ComplexReactor invert();
    void apply();
    void clear();

}
