package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.entities.ChemicalEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author cl
 */
public class GraphComplexModification {

    private BindingSite bindingSite;
    private ModificationOperation operation;

    public GraphComplexModification(ModificationOperation operation) {
        this.operation = operation;
    }

    public BindingSite getBindingSite() {
        return bindingSite;
    }

    public void setBindingSite(BindingSite bindingSite) {
        this.bindingSite = bindingSite;
    }

    public ModificationOperation getOperation() {
        return operation;
    }

    public void setOperation(ModificationOperation operation) {
        this.operation = operation;
    }





}
