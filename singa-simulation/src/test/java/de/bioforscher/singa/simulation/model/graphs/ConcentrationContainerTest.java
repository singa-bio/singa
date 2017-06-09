package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.entities.Species;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.singa.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class ConcentrationContainerTest {

    private final ChemicalEntity<?> entity = new Species.Builder("A").build();

    private final EnclosedCompartment innerSection = new EnclosedCompartment("Right", "Right Compartment");
    private final EnclosedCompartment outerSection = new EnclosedCompartment("Left", "Left Compartment");
    private final Membrane membrane = Membrane.forCompartment(this.innerSection);

    @Test
    public void testReturnOfSimpleConcentration() {
        // by default with simple concentration set
        BioNode n1 = new BioNode(0);
        // initialize value
        n1.setConcentration(this.entity, 0.3);
        // retrieve value
        assertEquals(0.3, n1.getConcentration(this.entity).getValue().doubleValue(), 0.0);
    }

    @Test
    public void testReturnOfMultiConcentration() {
        // and with multi concentration set
        BioNode node = new BioNode(1);
        // create set of sections
        Set<CellSection> sections = new HashSet<>();
        sections.add(this.innerSection);
        sections.add(this.outerSection);
        // and initialize node as multi section container
        node.setConcentrations(new MultiConcentrationContainer(sections));
        // initialize values
        node.setAvailableConcentration(this.entity, this.innerSection, Quantities.getQuantity(0.3, MOLE_PER_LITRE));
        node.setAvailableConcentration(this.entity, this.outerSection, Quantities.getQuantity(0.5, MOLE_PER_LITRE));
        // plain get concentration returns average
        assertEquals(0.4, node.getConcentration(this.entity).getValue().doubleValue(), 0.0);
        // asking specifically will get available concentration
        assertEquals(0.3, node.getAvailableConcentration(this.entity, this.innerSection).getValue().doubleValue(), 0.0);
        assertEquals(0.5, node.getAvailableConcentration(this.entity, this.outerSection).getValue().doubleValue(), 0.0);
    }

    @Test
    public void testReturnOfMembraneConcentration() {
        // and with membrane concentration set
        BioNode node = new BioNode(1);
        // and initialize node as multi section container
        node.setConcentrations(new MembraneContainer(this.outerSection, this.innerSection, this.membrane));
        // initialize values
        node.setAvailableConcentration(this.entity, this.innerSection, Quantities.getQuantity(0.3, MOLE_PER_LITRE));
        node.setAvailableConcentration(this.entity, this.outerSection, Quantities.getQuantity(0.5, MOLE_PER_LITRE));
        node.setAvailableConcentration(this.entity, this.membrane, Quantities.getQuantity(0.1, MOLE_PER_LITRE));
        // plain get concentration returns average
        assertEquals(0.3, node.getConcentration(this.entity).getValue().doubleValue(),1e-16);
        // asking specifically will get available concentration
        assertEquals(0.3, node.getAvailableConcentration(this.entity, this.innerSection).getValue().doubleValue(), 0.0);
        assertEquals(0.5, node.getAvailableConcentration(this.entity, this.outerSection).getValue().doubleValue(), 0.0);
        assertEquals(0.1, node.getAvailableConcentration(this.entity, this.membrane).getValue().doubleValue(), 0.0);
    }




}