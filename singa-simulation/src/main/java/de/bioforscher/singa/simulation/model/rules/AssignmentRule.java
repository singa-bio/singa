package de.bioforscher.singa.simulation.model.rules;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
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

    private final AppliedExpression expression;

    private ChemicalEntity<?> targetEntity;
    private Map<ChemicalEntity, String> entityReference;

    public AssignmentRule(ChemicalEntity<?> targetEntity, AppliedExpression expression) {
        this.targetEntity = targetEntity;
        this.expression = expression;
        entityReference = new HashMap<>();
    }

    public ChemicalEntity<?> getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(ChemicalEntity<?> targetEntity) {
        this.targetEntity = targetEntity;
    }

    public void referenceChemicalEntityToParameter(String parameterIdentifier, ChemicalEntity entity) {
        entityReference.put(entity, parameterIdentifier);
        // FIXME this is not done correctly
        expression.setParameter(new SimulationParameter<>(parameterIdentifier, Quantities.getQuantity(0.0, MOLE_PER_LITRE)));
    }

    public Map<ChemicalEntity, String> getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(Map<ChemicalEntity, String> entityReference) {
        this.entityReference = entityReference;
    }

    public void applyRule(AutomatonNode node) {
        // set entity parameters
        for (Map.Entry<ChemicalEntity, String> entry : entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = node.getConcentration(entry.getKey());
            final String parameterName = entityReference.get(entry.getKey());
            expression.acceptValue(parameterName, concentration.getValue().doubleValue());
        }
        Quantity<?> concentration = expression.evaluate();
        logger.debug("Initialized concentration of {} to {}.", targetEntity.getIdentifier(), concentration);
        node.setConcentration(targetEntity, concentration.getValue().doubleValue());
    }

}
