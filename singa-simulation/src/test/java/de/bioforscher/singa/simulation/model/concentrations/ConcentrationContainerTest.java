package de.bioforscher.singa.simulation.model.concentrations;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class ConcentrationContainerTest {

    private final ChemicalEntity<?> entity = new Species.Builder("A").build();

    private final EnclosedCompartment innerSection = new EnclosedCompartment("Right", "Right Compartment");
    private final EnclosedCompartment outerSection = new EnclosedCompartment("Left", "Left Compartment");
    private final Membrane membrane = Membrane.forCompartment(innerSection);

    @Test
    public void testReturnOfSimpleConcentration() {
        // by default with simple concentration set
        AutomatonNode n1 = new AutomatonNode(0);
        // initialize value
        n1.setConcentration(entity, 0.3);
        // retrieve value
        assertEquals(0.3, n1.getConcentration(entity).getValue().doubleValue(), 0.0);
    }

    @Test
    public void testReturnOfMultiConcentration() {
        // and with multi concentration set
        AutomatonNode node = new AutomatonNode(1);
        // create set of sections
        Set<CellSection> sections = new HashSet<>();
        sections.add(innerSection);
        sections.add(outerSection);
        // and initialize node as multi section container
        node.setConcentrationContainer(new MultiConcentrationContainer(sections));
        // initialize values
        node.setAvailableConcentration(entity, innerSection, Quantities.getQuantity(0.3, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        node.setAvailableConcentration(entity, outerSection, Quantities.getQuantity(0.5, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        // plain get concentration returns average
        assertEquals(0.4, node.getConcentration(entity).getValue().doubleValue(), 0.0);
        // asking specifically will get available concentration
        assertEquals(0.3, node.getAvailableConcentration(entity, innerSection).getValue().doubleValue(), 0.0);
        assertEquals(0.5, node.getAvailableConcentration(entity, outerSection).getValue().doubleValue(), 0.0);
    }

    @Test
    public void testReturnOfMembraneConcentration() {
        // and with membrane concentration set
        AutomatonNode node = new AutomatonNode(1);
        // and initialize node as multi section container
        node.setConcentrationContainer(new MembraneContainer(outerSection, innerSection, membrane));
        // initialize values
        node.setAvailableConcentration(entity, innerSection, Quantities.getQuantity(0.3, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        node.setAvailableConcentration(entity, outerSection, Quantities.getQuantity(0.5, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        node.setAvailableConcentration(entity, membrane, Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        // plain get concentration returns average
        assertEquals(0.25, node.getConcentration(entity).getValue().doubleValue(), 1e-16);
        // asking specifically will get available concentration
        assertEquals(0.3, node.getAvailableConcentration(entity, innerSection).getValue().doubleValue(), 0.0);
        assertEquals(0.5, node.getAvailableConcentration(entity, outerSection).getValue().doubleValue(), 0.0);
        assertEquals(0.1, node.getAvailableConcentration(entity, membrane).getValue().doubleValue(), 0.0);
        // set inner and outer layer
        node.setAvailableConcentration(entity, membrane.getInnerLayer(), Quantities.getQuantity(0.1, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        node.setAvailableConcentration(entity, membrane.getOuterLayer(), Quantities.getQuantity(0.2, MOLE_PER_LITRE).to(EnvironmentalParameters.getTransformedMolarConcentration()));
        assertEquals(0.1, node.getAvailableConcentration(entity, membrane.getInnerLayer()).getValue().doubleValue(), 0.0);
        assertEquals(0.2, node.getAvailableConcentration(entity, membrane.getOuterLayer()).getValue().doubleValue(), 0.0);
    }

}