package de.bioforscher.simulation.modules.reactions.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.implementations.DynamicKineticLaw;
import de.bioforscher.units.quantities.ReactionRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;

/**
 * @author cl
 */
public class AssignmentRule {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentRule.class);

    private ChemicalEntity<?> targetEntity;
    private DynamicKineticLaw kineticLaw;

    public AssignmentRule(ChemicalEntity<?> targetEntity, DynamicKineticLaw kineticLaw) {
        this.targetEntity = targetEntity;
        this.kineticLaw = kineticLaw;
        this.kineticLaw.setAppliedScale(1);
    }

    public ChemicalEntity<?> getTargetEntity() {
        return this.targetEntity;
    }

    public void setTargetEntity(ChemicalEntity<?> targetEntity) {
        this.targetEntity = targetEntity;
    }

    public DynamicKineticLaw getKineticLaw() {
        return this.kineticLaw;
    }

    public void setKineticLaw(DynamicKineticLaw kineticLaw) {
        this.kineticLaw = kineticLaw;
    }

    public void applyRule(BioNode node) {
        Quantity<ReactionRate> concentration = this.kineticLaw.calculateAcceleration(node);
        logger.debug("Initialized concentration of {} to {}.", this.targetEntity.getIdentifier(), concentration );
        node.setConcentration(this.targetEntity, concentration.getValue().doubleValue());
    }

    @Override
    public String toString() {
        return "AssignmentRule{" +
                "targetEntity=" + this.targetEntity +
                ", kineticLaw=" + this.kineticLaw +
                '}';
    }
}
