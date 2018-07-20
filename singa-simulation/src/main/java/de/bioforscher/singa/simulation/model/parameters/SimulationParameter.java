package de.bioforscher.singa.simulation.model.parameters;

import de.bioforscher.singa.chemistry.annotations.Annotatable;
import de.bioforscher.singa.chemistry.annotations.Annotation;
import de.bioforscher.singa.core.utility.Nameable;
import de.bioforscher.singa.features.identifiers.SimpleStringIdentifier;
import de.bioforscher.singa.features.identifiers.model.Identifiable;
import de.bioforscher.singa.features.identifiers.model.Identifier;
import tec.uom.se.quantity.Quantities;

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
     * All annotations of this Parameter.
     */
    private final List<Annotation> annotations;

    /**
     * A name or description of this Parameter.
     */
    private String name;

    /**
     * The value and unit (quantity) of this parameter.
     */
    private Quantity<QuantityType> quantity;

    public SimulationParameter(String identifier) {
        this.identifier = new SimpleStringIdentifier(identifier);
        annotations = new ArrayList<>();
    }

    public SimulationParameter(String identifier, Quantity<QuantityType> quantity) {
        this(identifier);
        this.quantity = quantity;
    }

    @Override
    public SimpleStringIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public Quantity<QuantityType> getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity<QuantityType> quantity) {
        this.quantity = quantity;
    }

    public double getValue() {
        return quantity.getValue().doubleValue();
    }

    public void setValue(double value) {
        quantity = Quantities.getQuantity(value, quantity.getUnit());
    }

}
