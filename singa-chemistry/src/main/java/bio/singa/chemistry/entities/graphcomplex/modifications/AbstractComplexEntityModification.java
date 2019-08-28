package bio.singa.chemistry.entities.graphcomplex.modifications;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.graphcomplex.BindingSite;
import bio.singa.chemistry.entities.graphcomplex.GraphComplex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractComplexEntityModification implements ComplexEntityModification {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractComplexEntityModification.class);

    private BindingSite bindingSite;
    private ChemicalEntity primaryEntity;
    private ChemicalEntity secondaryEntity;

    private List<GraphComplex> candidates;
    private List<GraphComplex> results;

    public AbstractComplexEntityModification(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
        candidates = new ArrayList<>();
        results = new ArrayList<>();
    }

    @Override
    public BindingSite getBindingSite() {
        return bindingSite;
    }

    public void setBindingSite(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    @Override
    public ChemicalEntity getPrimaryEntity() {
        return primaryEntity;
    }

    public void setPrimaryEntity(ChemicalEntity primaryEntity) {
        this.primaryEntity = primaryEntity;
    }

    @Override
    public ChemicalEntity getSecondaryEntity() {
        return secondaryEntity;
    }

    public void setSecondaryEntity(ChemicalEntity secondaryEntity) {
        this.secondaryEntity = secondaryEntity;
    }

    public List<GraphComplex> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<GraphComplex> candidates) {
        this.candidates = candidates;
    }

    public void addCandidate(GraphComplex candidate) {
        candidates.add(candidate);
    }

    public List<GraphComplex> getResults() {
        return results;
    }

    public void setResults(List<GraphComplex> results) {
        this.results = results;
    }

    void addResult(GraphComplex result) {
        results.add(result);
    }

    void addAllResults(Collection<GraphComplex> results) {
        this.results.addAll(results);
    }

    public void clear() {
        candidates.clear();
        results.clear();
    }

}
