package de.bioforscher.chemistry.descriptive;

import de.bioforscher.core.identifier.ChEBIIdentifier;
import de.bioforscher.core.identifier.SimpleStringIdentifier;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.units.UnitProvider.GRAM_PER_MOLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Christoph on 18.04.2016.
 */
public class SpeciesTest {

    @Test
    public void shouldCreateSpeciesWithStringIdentifier() {
        Species methanol = new Species.Builder("CHEBI:123").build();
        assertEquals(methanol.getIdentifier().getIdentifier(), "CHEBI:123");
    }

    @Test
    public void shouldCreateSpeciesWithIdentifier() {
        Species methanol = new Species.Builder(new SimpleStringIdentifier("CHEBI:123")).build();
        assertEquals(methanol.getIdentifier().getIdentifier(), "CHEBI:123");
    }

    @Test
    public void shouldCreateSpeciesWithDoubleMolecularWeight() {
        Species methanol = new Species.Builder("CHEBI:123").molarMass(10.0).build();
        assertEquals(methanol.getMolarMass().getValue().doubleValue(), 10.0, 0.0);
    }

    @Test
    public void shouldCreateSpeciesWithQuantityMolecularWeight() {
        Species methanol = new Species.Builder("CHEBI:123").molarMass(Quantities.getQuantity(10.0, GRAM_PER_MOLE)).build();
        assertEquals(methanol.getMolarMass().getValue().doubleValue(), 10.0, 0.0);
    }

    @Test
    public void shouldCreateSpeciesWithSMILES() {
        Species methanol = new Species.Builder("CHEBI:123").molarMass(10.0).smilesRepresentation("CCSCC").build();
        assertEquals(methanol.getSmilesRepresentation(), "CCSCC");
    }

    @Test
    public void shouldCreateEnzyme() {
        Enzyme gtpAse = new Enzyme.Builder("A2BC19").molarMass(18714.0).build();
        assertEquals(gtpAse.getMolarMass().getValue().doubleValue(), 18714.0, 0.0);
    }

    @Test
    public void shouldBeIdenticalEnzymes() {
        Enzyme gtpAse1 = new Enzyme.Builder("A2BC19").build();
        Enzyme gtpAse2 = new Enzyme.Builder("A2BC19").build();

        assertTrue(gtpAse1.equals(gtpAse2));
    }

    }