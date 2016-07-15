package de.bioforscher.simulation.modules.reactions.implementations.enzyme.kineticLaws;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;

/**
 * Created by Christoph on 14.07.2016.
 */
public class EntityDependentKineticParameter extends KineticParameter {

    private ChemicalEntity entity;

    public EntityDependentKineticParameter(KineticParameterType parameterType, double value) {
        super(parameterType, value);
    }

    public EntityDependentKineticParameter(KineticParameterType parameterType, double value, ChemicalEntity entity) {
        super(parameterType, value);
        this.entity = entity;
    }

    public ChemicalEntity getEntity() {
        return this.entity;
    }

    public void setEntity(ChemicalEntity entity) {
        this.entity = entity;
    }
}
