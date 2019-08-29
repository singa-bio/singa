package bio.singa.chemistry.entities.graphcomplex.reactors;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;
import bio.singa.chemistry.entities.graphcomplex.modifications.ComplexEntityModification;
import bio.singa.core.utility.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class AbstractGraphComplexReactor implements ComplexReactor {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractGraphComplexReactor.class);

    private ComplexEntityModification modification;

    private List<Predicate<GraphComplex>> primaryCandidateConditions;
    private List<GraphComplex> primarySubstrates;
    private List<GraphComplex> primaryProducts;

    public AbstractGraphComplexReactor() {
        primaryCandidateConditions = new ArrayList<>();
        primarySubstrates = new ArrayList<>();
        primaryProducts = new ArrayList<>();
    }

    protected List<GraphComplex> filterCandidates(List<GraphComplex> complexes, List<Predicate<GraphComplex>> conditions) {
        List<GraphComplex> list = new ArrayList<>();
        Predicate<GraphComplex> predicate = conditions.stream()
                .reduce(Predicate::and).orElse(complex -> false);
        for (GraphComplex graphComplex : complexes) {
            if (predicate.test(graphComplex)) {
                list.add(graphComplex);
            }
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

    public List<Predicate<GraphComplex>> getPrimaryCandidateConditions() {
        return primaryCandidateConditions;
    }

    public void setPrimaryCandidateConditions(List<Predicate<GraphComplex>> primaryCandidateConditions) {
        this.primaryCandidateConditions = primaryCandidateConditions;
    }

    public List<GraphComplex> getPrimarySubstrates() {
        return primarySubstrates;
    }

    public void setPrimarySubstrates(List<GraphComplex> primarySubstrates) {
        this.primarySubstrates = primarySubstrates;
    }

    public List<GraphComplex> getPrimaryProducts() {
        return primaryProducts;
    }

    public void setPrimaryProducts(List<GraphComplex> primaryProducts) {
        this.primaryProducts = primaryProducts;
    }

    @Override
    public void clear() {
        primarySubstrates.clear();
        primaryProducts.clear();
    }
}
