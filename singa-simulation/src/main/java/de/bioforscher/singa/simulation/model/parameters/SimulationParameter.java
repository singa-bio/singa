package de.bioforscher.singa.simulation.model.parameters;

import de.bioforscher.singa.chemistry.descriptive.annotations.Annotatable;
import de.bioforscher.singa.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.singa.core.identifier.SimpleStringIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifiable;
import de.bioforscher.singa.core.identifier.model.Identifier;
import de.bioforscher.singa.core.utility.Nameable;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class SimulationParameter<QuantityType extends Quantity<QuantityType>> implements Identifiable<SimpleStringIdentifier>, Nameable, Annotatable {

    /**
     * The distinct {@link Identifier} by which this parameter is identified.
     */
    private final SimpleStringIdentifier identifier;

    /**
     * A name or description of this Parameter.
     */
    private String name;

    /**
     * All annotations of this Parameter.
     */
    private List<Annotation> annotations;

    /**
     * The value and unit (quantity) of this parameter.
     */
    private Quantity<QuantityType> quantity;

    public SimulationParameter(String identifier) {
        this.identifier = new SimpleStringIdentifier(identifier);
        this.annotations = new ArrayList<>();
    }

    public SimulationParameter(String identifier, Quantity<QuantityType> quantity) {
        this(identifier);
        this.quantity = quantity;
    }

    @Override
    public SimpleStringIdentifier getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return this.annotations;
    }

    public Quantity<QuantityType> getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Quantity<QuantityType> quantity) {
        this.quantity = quantity;
    }

    public double getValue() {
        return this.quantity.getValue().doubleValue();
    }

    public void setValue(double value) {
        this.quantity = Quantities.getQuantity(value, this.quantity.getUnit());
    }

}
