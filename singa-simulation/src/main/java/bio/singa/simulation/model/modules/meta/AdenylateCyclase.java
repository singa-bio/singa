package bio.singa.simulation.model.modules.meta;

import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.MichaelisConstant;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.chemistry.features.reactions.TurnoverNumber;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.modules.concentration.imlementations.MichaelisMentenReaction;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.ProductUnit;

import static bio.singa.features.model.FeatureOrigin.MANUALLY_ANNOTATED;
import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.AbstractUnit.ONE;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.MINUTE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class AdenylateCyclase {

    public static void addToSimulation(Simulation simulation) {

    }


    public static void main(String[] args) {

        Simulation simulation = new Simulation();

        // adenylate cyclase
        Protein ac6 = new Protein.Builder("AC6")
                .additionalIdentifier(new UniProtIdentifier("O43306"))
                .setMembraneAnchored(true)
                .build();

        SmallMolecule atp = SmallMolecule.create("ATP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15422"))
                .build();

        SmallMolecule camp = SmallMolecule.create("cAMP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17489"))
                .build();

        Protein gProteinAlpha = new Protein.Builder("G(A)")
                .additionalIdentifier(new UniProtIdentifier("P63092"))
                .build();

        SmallMolecule gtp = new SmallMolecule.Builder("GTP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        ComplexedChemicalEntity gtpGProteinAlpha = ComplexedChemicalEntity.create("G(A):GTP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gtp)
                .setMembraneAnchored(true)
                .build();

        ComplexedChemicalEntity acGalpha = ComplexedChemicalEntity.create("AC6:G(A)")
                .addAssociatedPart(ac6)
                .addAssociatedPart(gtpGProteinAlpha)
                .setMembraneAnchored(true)
                .build();


        // estimated from k2 = kd = 200 nM (Chen-Goodspeed 2005)
        RateConstant kFwd = RateConstant.create(0.005)
                .forward().secondOrder()
                .concentrationUnit(NANO(MOLE_PER_LITRE))
                .timeUnit(SECOND)
                .build();

        RateConstant kBwd = RateConstant.create(1.0)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        MichaelisConstant michaelisConstant = new MichaelisConstant(Quantities.getQuantity(23, MICRO(MOLE_PER_LITRE)), MANUALLY_ANNOTATED);
        TurnoverNumber turnoverNumber = new TurnoverNumber(0.32, new ProductUnit<>(ONE.divide(MINUTE)), MANUALLY_ANNOTATED);

        acGalpha.setFeature(michaelisConstant);
        acGalpha.setFeature(turnoverNumber);

        MichaelisMentenReaction.inSimulation(simulation)
                .enzyme(acGalpha)
                .addSubstrate(atp)
                .addProduct(camp)
                .build();

        ComplexBuildingReaction.inSimulation(simulation)
                .of(gtpGProteinAlpha, kFwd)
                .in(CellTopology.INNER)
                .by(ac6, kBwd)
                .to(CellTopology.MEMBRANE)
                .formingComplex(acGalpha)
                .build();


    }

}
