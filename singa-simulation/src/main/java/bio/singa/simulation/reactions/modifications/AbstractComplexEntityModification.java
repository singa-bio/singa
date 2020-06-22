package bio.singa.simulation.reactions.modifications;

import bio.singa.simulation.entities.ChemicalEntity;
import bio.singa.simulation.entities.BindingSite;
import bio.singa.simulation.entities.ComplexEntity;
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

    private List<ComplexEntity> candidates;
    private List<ComplexEntity> results;

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

    public List<ComplexEntity> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<ComplexEntity> candidates) {
        this.candidates = candidates;
    }

    public void addCandidate(ComplexEntity candidate) {
        candidates.add(candidate);
    }

    public List<ComplexEntity> getResults() {
        return results;
    }

    public void setResults(List<ComplexEntity> results) {
        this.results = results;
    }

    void addResult(ComplexEntity result) {
        results.add(result);
    }

    void addAllResults(Collection<ComplexEntity> results) {
        this.results.addAll(results);
    }

    public void clear() {
        candidates.clear();
        results.clear();
    }

}
