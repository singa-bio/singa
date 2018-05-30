package de.bioforscher.singa.simulation.modules.reactions.implementations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.parameters.SimulationParameter;
import de.bioforscher.singa.simulation.model.rules.AppliedExpression;
import de.bioforscher.singa.simulation.modules.reactions.model.KineticLaw;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic kinetic laws allow for the definition of reaction kinetics based on equations. Equations are supplied to the
 * Kinetic law as {@link AppliedExpression}s.
 *
 * @author cl
 */
public class DynamicKineticLaw implements KineticLaw {

    /**
     * The expression that is calculated when determining the speed of the reaction.
     */
    private final AppliedExpression expression;

    /**
     * The chemical entities relevant for the kinetic law an their corresponding parameters in the expression.
     */
    private final Map<ChemicalEntity, String> entityReference;

    /**
     * The cell section the kinetic law is currently applied in (to determine the relevant concentrations).
     */
    private CellSubsection currentCellSection;

    /**
     * The scale is adjusted to account for changes in time steps.
     */
    private Double appliedScale = 1.0;

    /**
     * Creates a new dynamic kinetic law with the applied expression.
     * @param expression The expression.
     */
    public DynamicKineticLaw(AppliedExpression expression) {
        this.expression = expression;
        entityReference = new HashMap<>();
    }

    /**
     * Returns the actual expression use to calculate this kinetic law.
     * @return The actual expression use to calculate this kinetic law.
     */
    public AppliedExpression getExpression() {
        return expression;
    }

    /**
     * Assigns a parameter of the expression to the concentration of a chemical species.
     * @param parameterIdentifier The identifier of the parameter.
     * @param entity The entity to be referenced.
     */
    public void referenceChemicalEntityToParameter(String parameterIdentifier, ChemicalEntity entity) {
        entityReference.put(entity, parameterIdentifier);
        expression.setParameter(new SimulationParameter<>(parameterIdentifier, Environment.emptyConcentration()));
    }

    /**
     * Sets the scaling factor that is applied to account for changes in the time step size.
     * @param appliedScale The scaling factor.
     */
    public void setAppliedScale(Double appliedScale) {
        this.appliedScale = appliedScale;
    }

    /**
     * Returns the current cell section the kinetic law is applied to.
     * @return The current cell section the kinetic law is applied to.
     */
    public CellSubsection getCurrentCellSection() {
        return currentCellSection;
    }

    /**
     * Sets the current cell section the kinetic law is applied to.
     * @param currentCellSection The current cell section the kinetic law is applied to.
     */
    public void setCurrentCellSection(CellSubsection currentCellSection) {
        this.currentCellSection = currentCellSection;
    }

    @Override
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // set entity parameters
        for (Map.Entry<ChemicalEntity, String> entry : entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = concentrationContainer.get(currentCellSection, entry.getKey());
            final String parameterName = entityReference.get(entry.getKey());
            expression.acceptValue(parameterName, concentration.getValue().doubleValue());
        }
        return expression.evaluate().getValue().doubleValue() * appliedScale;
    }

}
