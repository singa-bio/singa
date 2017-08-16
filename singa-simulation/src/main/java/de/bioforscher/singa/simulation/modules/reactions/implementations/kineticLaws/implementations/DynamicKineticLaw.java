package de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AppliedExpression;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
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

    private double appliedScale = 70;

    public DynamicKineticLaw(AppliedExpression expression) {
        this.expression = expression;
        this.entityReference = new HashMap<>();
    }

    public AppliedExpression getExpression() {
        return this.expression;
    }

    public void referenceChemicalEntityToParameter(String parameterIdentifier, ChemicalEntity entity) {
        this.entityReference.put(entity, parameterIdentifier);
        // FIXME this is not done correctly
        // FIXME parameters do not scale with time step
        this.expression.setParameter(new SimulationParameter<>(parameterIdentifier, Quantities.getQuantity(0.0, MOLE_PER_LITRE)));
    }

    public Map<ChemicalEntity, String> getEntityReference() {
        return this.entityReference;
    }

    public double getAppliedScale() {
        return this.appliedScale;
    }

    public void setAppliedScale(double appliedScale) {
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
        for (Map.Entry<ChemicalEntity, String> entry : this.entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = concentrationContainer.getAvailableConcentration(currentCellSection,entry.getKey());
            final String parameterName = this.entityReference.get(entry.getKey());
            this.expression.acceptValue(parameterName, concentration.getValue().doubleValue());
        }
        // FIXME scale depending on time step
        return this.expression.evaluate().getValue().doubleValue() / this.appliedScale;
    }

}
