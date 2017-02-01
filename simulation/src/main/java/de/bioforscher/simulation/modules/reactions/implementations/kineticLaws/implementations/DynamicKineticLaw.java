package de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.implementations;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import de.bioforscher.simulation.modules.reactions.implementations.kineticLaws.model.KineticParameterType;
import de.bioforscher.units.UnitProvider;
import de.bioforscher.units.quantities.MolarConcentration;
import de.bioforscher.units.quantities.ReactionRate;
import tec.units.ri.quantity.Quantities;
import uk.co.cogitolearning.cogpar.ExpressionNode;
import uk.co.cogitolearning.cogpar.Parser;
import uk.co.cogitolearning.cogpar.SetVariable;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cl
 */
public class DynamicKineticLaw implements KineticLaw {

    private ExpressionNode expression;
    private List<SetVariable> localParameters;
    private Map<ChemicalEntity, String> entityReference;

    public DynamicKineticLaw(String expression) {
        Parser parser = new Parser();
        this.expression = parser.parse(expression);
        this.localParameters = new ArrayList<>();
        this.entityReference = new HashMap<>();
    }

    public void setLocalParameter(String parameterName, double value) {
        final SetVariable variable = new SetVariable(parameterName, value);
        this.localParameters.add(variable);
        this.expression.accept(variable);
    }

    public void referenceChemicalEntityToParameter(String parameterName, ChemicalEntity entity) {
        this.entityReference.put(entity, parameterName);
    }

    @Override
    public Quantity<ReactionRate> calculateAcceleration(BioNode node) {
        // set entity parameters
        for (Map.Entry<ChemicalEntity, String> entry : this.entityReference.entrySet()) {
            final Quantity<MolarConcentration> concentration = node.getConcentration(entry.getKey());
            final String parameterName = this.entityReference.get(entry.getKey());
            this.expression.accept(new SetVariable(parameterName, concentration.getValue().doubleValue()));
        }
        return Quantities.getQuantity(this.expression.getValue(), UnitProvider.PER_SECOND);
    }

    @Override
    public void prepareAppliedRateConstants() {

    }

    @Override
    public List<KineticParameterType> getRequiredParameters() {
        return null;
    }
}
