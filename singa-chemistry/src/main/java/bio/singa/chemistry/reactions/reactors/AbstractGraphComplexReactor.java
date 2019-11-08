package bio.singa.chemistry.reactions.reactors;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.complex.BindingSite;
import bio.singa.chemistry.entities.complex.ComplexEntity;
import bio.singa.chemistry.reactions.conditions.CandidateCondition;
import bio.singa.chemistry.reactions.modifications.ComplexEntityModification;
import bio.singa.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public abstract class AbstractGraphComplexReactor implements ComplexReactor {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractGraphComplexReactor.class);

    private ComplexEntityModification modification;

    private List<CandidateCondition> primaryCandidateConditions;
    private List<ComplexEntity> primarySubstrates;
    private List<ComplexEntity> primaryProducts;

    public AbstractGraphComplexReactor() {
        primaryCandidateConditions = new ArrayList<>();
        primarySubstrates = new ArrayList<>();
        primaryProducts = new ArrayList<>();
    }

    protected List<ComplexEntity> filterCandidates(List<ComplexEntity> complexes, List<CandidateCondition> conditions) {
        List<ComplexEntity> list = new ArrayList<>();
        outer:
        for (ComplexEntity graphComplex : complexes) {
            for (CandidateCondition condition : conditions) {
                if (!condition.test(graphComplex)) {
                    continue outer;
                }
            }
            list.add(graphComplex);
        }
        return list;
    }

    public Map.Entry<BindingSite, Pair<ChemicalEntity>> getBindingSite() {
        return new AbstractMap.SimpleEntry<>(modification.getBindingSite(), new Pair<>(modification.getPrimaryEntity(), modification.getSecondaryEntity()));
    }

    public ComplexEntityModification getModification() {
        return modification;
    }

    public void setModification(ComplexEntityModification modification) {
        this.modification = modification;
    }

    public List<CandidateCondition> getPrimaryCandidateConditions() {
        return primaryCandidateConditions;
    }

    public void setPrimaryCandidateConditions(List<CandidateCondition> primaryCandidateConditions) {
        this.primaryCandidateConditions = primaryCandidateConditions;
    }

    public List<ComplexEntity> getPrimarySubstrates() {
        return primarySubstrates;
    }

    public void setPrimarySubstrates(List<ComplexEntity> primarySubstrates) {
        this.primarySubstrates = primarySubstrates;
    }

    public List<ComplexEntity> getPrimaryProducts() {
        return primaryProducts;
    }

    public void setPrimaryProducts(List<ComplexEntity> primaryProducts) {
        this.primaryProducts = primaryProducts;
    }

    @Override
    public void clear() {
        primarySubstrates.clear();
        primaryProducts.clear();
    }
}
