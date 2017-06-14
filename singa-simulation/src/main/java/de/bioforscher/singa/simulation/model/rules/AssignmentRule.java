package de.bioforscher.singa.simulation.model.rules;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class AssignmentRule {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentRule.class);

    private AppliedExpression expression;
    private ChemicalEntity<?> targetEntity;
    private Map<ChemicalEntity, String> entityReference;

    public AssignmentRule(ChemicalEntity<?> targetEntity, AppliedExpression expression) {
        this.targetEntity = targetEntity;
        this.expression = expression;
        this.entityReference = new HashMap<>();
    }

    public ChemicalEntity<?> getTargetEntity() {
        return this.targetEntity;
    }

    public void setTargetEntity(ChemicalEntity<?> targetEntity) {
        this.targetEntity = targetEntity;
    }

    public void referenceChemicalEntityToParameter(String parameterIdentifier, ChemicalEntity entity) {
        this.entityReference.put(entity, parameterIdentifier);
        // FIXME this is not done correctly
        this.expression.setParameter(new SimulationParameter<>(parameterIdentifier, Quantities.getQuantity(0.0, MOLE_PER_LITRE)));
    }

    public Map<ChemicalEntity, String> getEntityReference() {
        return this.entityReference;
    }

    public void setEntityReference(Map<ChemicalEntity, String> entityReference) {
        this.entityReference = entityReference;
    }

    public void applyRule(BioNode node) {
        // set entity parameters
        for (Map.Entry<ChemicalEntity, String> entry : this.entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = node.getConcentration(entry.getKey());
            final String parameterName = this.entityReference.get(entry.getKey());
            this.expression.acceptValue(parameterName, concentration.getValue().doubleValue());
        }
        Quantity<?> concentration = this.expression.evaluate();
        logger.debug("Initialized concentration of {} to {}.", this.targetEntity.getIdentifier(), concentration );
        node.setConcentration(this.targetEntity, concentration.getValue().doubleValue());
    }

}
