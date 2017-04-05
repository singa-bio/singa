package de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;

import javax.measure.Quantity;

/**
 * @author leberech 
 */
public class EntityDependentKineticParameter<ParameterType extends Quantity<ParameterType>> extends
        KineticParameter<ParameterType> {

    private ChemicalEntity entity;

    public EntityDependentKineticParameter(KineticParameterType parameterType, Quantity<ParameterType> value) {
        super(parameterType, value);
    }

    public EntityDependentKineticParameter(KineticParameterType parameterType, Quantity<ParameterType> value, ChemicalEntity entity) {
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
