package bio.singa.chemistry.entities;

import bio.singa.features.identifiers.SimpleStringIdentifier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author cl
 */
public class SpeciesTest {

    @Test
    public void shouldCreateSpeciesWithStringIdentifier() {
        SmallMolecule methanol = new SmallMolecule.Builder("CHEBI:123").build();
        assertEquals(methanol.getIdentifier().getIdentifier(), "CHEBI:123");
    }

    @Test
    public void shouldCreateSpeciesWithIdentifier() {
        SmallMolecule methanol = new SmallMolecule.Builder(new SimpleStringIdentifier("CHEBI:123")).build();
        assertEquals(methanol.getIdentifier().getIdentifier(), "CHEBI:123");
    }

    @Test
    public void shouldBeIdenticalEnzymes() {
        Enzyme gtpAse1 = new Enzyme.Builder("A2BC19").build();
        Enzyme gtpAse2 = new Enzyme.Builder("A2BC19").build();

        assertTrue(gtpAse1.equals(gtpAse2));
    }

}