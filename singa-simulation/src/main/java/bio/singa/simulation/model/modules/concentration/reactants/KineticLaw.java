package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.simulation.model.parameters.SimulationParameter;
import bio.singa.simulation.model.rules.AppliedExpression;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.ConcentrationContainer;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

/**
 * Dynamic kinetic laws allow for the definition of reaction kinetics based on equations. Equations are supplied to the
 * Kinetic law as {@link AppliedExpression}s.
 *
 * @author cl
 */
public class KineticLaw {

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
    public KineticLaw(AppliedExpression expression) {
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

    /**
     * Calculates the velocity of the reaction based on the entities in the concentration container.
     * @param concentrationContainer The concentration container.
     * @return The velocity.
     */
    public double calculateVelocity(ConcentrationContainer concentrationContainer) {
        // set entity parameters
        for (Map.Entry<ChemicalEntity, String> entry : entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = concentrationContainer.get(currentCellSection, entry.getKey());
            final String parameterName = entityReference.get(entry.getKey());
            expression.acceptValue(parameterName, concentration.getValue().doubleValue());
        }
        // calculate
        return expression.evaluate().getValue().doubleValue() * appliedScale;
    }

}
