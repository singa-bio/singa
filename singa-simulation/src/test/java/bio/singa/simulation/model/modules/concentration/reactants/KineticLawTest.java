package bio.singa.simulation.model.modules.concentration.reactants;

import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.KineticLaw;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.Reactant;
import bio.singa.simulation.model.modules.concentration.imlementations.reactions.behaviors.reactants.ReactantRole;
import bio.singa.simulation.model.parameters.Parameter;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tec.uom.se.quantity.Quantities;

import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class KineticLawTest {

    @BeforeAll
    static void initialize() {
        UnitRegistry.reinitialize();
    }

    @AfterEach
    void cleanUp() {
        UnitRegistry.reinitialize();
    }

    @Test
    public void shouldCalculateVelocity() {
        // create law
        KineticLaw kl = new KineticLaw("1-D*ATP");
        // reference a feature
        Diffusivity diffusivity = new Diffusivity(Quantities.getQuantity(0.01, SQUARE_CENTIMETRE_PER_SECOND), Evidence.NO_EVIDENCE);
        kl.referenceFeature("D", diffusivity);
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
        assertEquals(0.999999, kl.calculateVelocity(cc, false));
    }

    @Test
    public void shouldReferenceParameters() {
        // create law
        KineticLaw kl = new KineticLaw("a+b+c");
        // reference a parameter
        kl.referenceParameter(new Parameter<>("a", UnitRegistry.concentration(1.0), Evidence.NO_EVIDENCE));
        kl.referenceParameter(new Parameter<>("b", UnitRegistry.concentration(0.1, MOLE_PER_LITRE), Evidence.NO_EVIDENCE));
        kl.referenceParameter(new Parameter<>("c", Quantities.getQuantity(1.0, MOLE_PER_LITRE.divide(MICRO(SECOND))), Evidence.NO_EVIDENCE));
        // exemplary concentration container
        ConcentrationContainer cc = new ConcentrationContainer();
        // assertion
        assertEquals(1.0000011, kl.calculateVelocity(cc, false));
    }


}