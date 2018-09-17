package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.model.FeatureOrigin;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.Assert.assertEquals;

/**
 * @author cl
 */
public class KineticLawTest {

    @Test
    public void shouldCalcualteVelocity() {
        // create law
        KineticLaw kl = new KineticLaw("1-D*ATP");
        // reference a constant
        kl.referenceConstant("e", Math.E);
        // reference a feature
        Diffusivity diffusivity = new Diffusivity(0.01, FeatureOrigin.MANUALLY_ANNOTATED);
        kl.referenceFeature(diffusivity.getSymbol(), diffusivity);
        diffusivity.scale();
        // reference a reactant to get concentration from
        SmallMolecule atp = SmallMolecule.create("ATP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15422"))
                .build();
        Reactant reactant = new Reactant(atp, ReactantRole.SUBSTRATE, CellTopology.INNER);
        kl.referenceReactant(atp.getIdentifier().toString(), reactant);
        // exemplary concentration container
        ConcentrationContainer cc = new ConcentrationContainer();
        cc.initializeSubsection(CellSubsection.SECTION_A, CellTopology.INNER);
        cc.initialize(CellSubsection.SECTION_A, atp, Quantities.getQuantity(1, MOLE_PER_LITRE));
        // assertion
        assertEquals(0.999999, kl.calculateVelocity(cc, false), 0.0);
    }

}