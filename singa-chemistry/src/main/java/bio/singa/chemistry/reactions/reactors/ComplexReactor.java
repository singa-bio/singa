package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.reactions.modifications.ComplexEntityModification;
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
