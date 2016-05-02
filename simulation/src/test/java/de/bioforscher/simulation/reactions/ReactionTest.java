package de.bioforscher.simulation.reactions;

import de.bioforscher.chemistry.descriptive.Species;
import org.junit.Test;
import tec.units.ri.quantity.Quantities;

import static de.bioforscher.units.UnitDictionary.PER_SECOND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Christoph on 23.04.2016.
 */
public class ReactionTest {

    @Test
    public void shouldCreateNthOrderReaction() {
        Species nitrogenDioxide = new Species.Builder("CHEBI:33101")
                .name("Nitrogen Dioxide").build();
        Species nitricOxide = new Species.Builder("CHEBI:16480")
                .name("Nitric oxide").build();
        Species diOxygen = new Species.Builder("CHEBI:15379")
                .name("Dioxygen").build();

        NthOrderReaction reaction = new NthOrderReaction.Builder()
                .addSubstrate(nitrogenDioxide, 2)
                .addProduct(nitricOxide)
                .addProduct(diOxygen)
                .rateConstant(Quantities.getQuantity(4.2, PER_SECOND))
                .build();

        assertTrue(reaction != null);
        assertEquals(4.2, reaction.getRateConstant().getValue().doubleValue(), 0.0);

    }

    @Test
    public void shouldCreateEnzymeReaction() {

        Species nitrogenDioxide = new Species.Builder("CHEBI:33101")
                .name("Nitrogen Dioxide").build();
        Species nitricOxide = new Species.Builder("CHEBI:16480")
                .name("Nitric oxide").build();
        Species diOxygen = new Species.Builder("CHEBI:15379")
                .name("Dioxygen").build();

        NthOrderReaction reaction = new NthOrderReaction.Builder()
                .addSubstrate(nitrogenDioxide, 2)
                .addProduct(nitricOxide)
                .addProduct(diOxygen)
                .rateConstant(Quantities.getQuantity(4.2, PER_SECOND))
                .build();

        assertTrue(reaction != null);
        assertEquals(4.2, reaction.getRateConstant().getValue().doubleValue(), 0.0);

    }

}