package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AppliedExpression;
import de.bioforscher.singa.simulation.modules.reactions.model.KineticLaw;
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
public class DynamicKineticLaw implements KineticLaw {

    private static final Logger logger = LoggerFactory.getLogger(DynamicKineticLaw.class);

    private AppliedExpression expression;
    private Map<ChemicalEntity, String> entityReference;

    private CellSection currentCellSection;

    private Double appliedScale = 1.0;

    public DynamicKineticLaw(AppliedExpression expression) {
        this.expression = expression;
        entityReference = new HashMap<>();
    }

    public AppliedExpression getExpression() {
        return expression;
    }

    public void referenceChemicalEntityToParameter(String parameterIdentifier, ChemicalEntity entity) {
        entityReference.put(entity, parameterIdentifier);
        expression.setParameter(new SimulationParameter<>(parameterIdentifier, Quantities.getQuantity(0.0, MOLE_PER_LITRE)));
    }

    public Map<ChemicalEntity, String> getEntityReference() {
        return entityReference;
    }

    public double getAppliedScale() {
        return appliedScale;
    }

    public void setAppliedScale(Double appliedScale) {
        this.appliedScale = appliedScale;
    }

    public CellSection getCurrentCellSection() {
        return currentCellSection;
    }

    public void setCurrentCellSection(CellSection currentCellSection) {
        this.currentCellSection = currentCellSection;
    }

    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // set entity parameters
        for (Map.Entry<ChemicalEntity, String> entry : entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = concentrationContainer.getAvailableConcentration(currentCellSection, entry.getKey());
            final String parameterName = entityReference.get(entry.getKey());
            expression.acceptValue(parameterName, concentration.getValue().doubleValue());
        }
        return expression.evaluate().getValue().doubleValue() * appliedScale;
    }

}
