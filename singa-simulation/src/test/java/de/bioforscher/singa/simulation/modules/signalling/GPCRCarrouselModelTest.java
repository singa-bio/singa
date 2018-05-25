package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.*;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.RateConstant;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraphs;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.implementations.EquilibriumReaction;
import de.bioforscher.singa.simulation.modules.reactions.implementations.NthOrderReaction;
import de.bioforscher.singa.simulation.modules.transport.FreeDiffusion;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import java.util.Comparator;

import static de.bioforscher.singa.features.parameters.Environment.getTransformedMolarConcentration;
import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.MEMBRANE;
import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.NON_MEMBRANE;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class GPCRCarrouselModelTest {

    private static final Logger logger = LoggerFactory.getLogger(MonovalentReceptorBindingTest.class);

    private static final FeatureOrigin BUSH2016 = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Bush 2016", "Bush, Alan, et al. \"Yeast GPCR signaling reflects the fraction of occupied receptors, not the number.\" Molecular systems biology 12.12 (2016): 898.");

    @Test
    public void testCarrouselModelSetUp() {
        Environment.setNodeDistance(Quantities.getQuantity(1, MICRO(METRE)));
        Simulation simulation = new Simulation();

        // reactome https://reactome.org/PathwayBrowser/#/R-HSA-432040&SEL=R-HSA-432197&FLG=O14610)
        // biomodels yeast carrousel https://www.ebi.ac.uk/biomodels-main/BIOMD0000000637

        // parameters

        RateConstant kOnR_G = RateConstant.create(4.6111e-3).forward().secondOder().concentrationUnit(NANO(MOLE_PER_LITRE)).timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kOnLR_G, kOnR_Gt, kOnLR_Gt, kOnR_Gd, kOnLR_Gd;
        kOnLR_G = kOnR_Gt = kOnLR_Gt = kOnR_Gd = kOnLR_Gd = kOnR_G;

        RateConstant kOffR_G = RateConstant.create(0.1).backward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kOffLR_G, kOffR_Gt, kOffLR_Gt, kOffR_Gd, kOffLR_Gd;
        kOffLR_G = kOffR_Gt = kOffLR_Gt = kOffR_Gd = kOffLR_Gd = kOffR_G;

        RateConstant kEf_Gd = RateConstant.create(6.2e-4).forward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kEf_RGt, kEf_G;
        kEf_RGt = kEf_G = kEf_Gd;

        RateConstant kHf_Gt = RateConstant.create(2.0e-3).backward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kEf_RG = RateConstant.create(6.2e-4).backward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();

        RateConstant kAf_Gd = RateConstant.create(0.2158).forward().secondOder().concentrationUnit(NANO(MOLE_PER_LITRE)).timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kAf_LRGd, kAf_RGd;
        kAf_LRGd = kAf_RGd = kAf_Gd;

        RateConstant kAr_Gd = RateConstant.create(1.3e-3).backward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kAr_LRGd, kAr_RGd;
        kAr_LRGd = kAr_RGd = kAr_Gd;

        RateConstant kEf_LRGd = RateConstant.create(1.5).forward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kEf_LRG = RateConstant.create(1.5).backward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();

        RateConstant kHf_LRGt = RateConstant.create(0.11).backward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kHf_RGt = kHf_LRGt;

        // backwards rate constant is effectively zero
        RateConstant kEf_zero = RateConstant.create(0.0).forward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();
        // transformed
        RateConstant kOn_LR = RateConstant.create(1.7857e-4).forward().secondOder().concentrationUnit(NANO(MOLE_PER_LITRE)).timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kOn_LRGd, kOn_LRG, kOn_LRGt;
        kOn_LRGd = kOn_LRG = kOn_LRGt = kOn_LR;

        RateConstant kOff_LR = RateConstant.create(0.001).backward().firstOrder().timeUnit(SECOND).origin(BUSH2016).build();
        RateConstant kOff_LRG, kOff_LRGt, kOff_LRGd;
        kOff_LRG = kOff_LRGt = kOff_LRGd = kOff_LR;

        // entities
        // entities
        // vasopressin v2 receptor
        Receptor vasopressinReceptor = new Receptor.Builder("V2R")
                .additionalIdentifier(new UniProtIdentifier("P30518"))
                .build();

        // vasopressin
        ChemicalEntity vasopressin = new SmallMolecule.Builder("AVP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:34543"))
                .build();

        // g-protein subunits
        Protein gProteinAlpha = new Protein.Builder("G(A)")
                .additionalIdentifier(new UniProtIdentifier("P63092"))
                .build();

        Protein gProteinBeta = new Protein.Builder("G(B)")
                .additionalIdentifier(new UniProtIdentifier("P62873"))
                .build();

        Protein gProteinGamma = new Protein.Builder("G(G)")
                .additionalIdentifier(new UniProtIdentifier("P63211"))
                .build();

        // g-protein substrates
        ChemicalEntity gdp = new SmallMolecule.Builder("GDP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        ChemicalEntity gtp = new SmallMolecule.Builder("GTP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        // complexed entities
        // g-protein complexes
        // free - beta gamma complex
        ComplexedChemicalEntity gProteinBetaGamma = new ComplexedChemicalEntity.Builder("G(BG)")
                .addAssociatedPart(gProteinBeta)
                .addAssociatedPart(gProteinGamma)
                .setMembraneAnchored(true)
                .build();

        // free - alpha beta gamma complex
        ComplexedChemicalEntity gProteinAlphaBetaGamma = new ComplexedChemicalEntity.Builder("G(ABG)")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gProteinBetaGamma)
                .setMembraneAnchored(true)
                .build();

        // gdp bound - alpha beta gamma complex
        ComplexedChemicalEntity gdpGProteinAlphaBetaGamma = new ComplexedChemicalEntity.Builder("G(ABG):GDP")
                .addAssociatedPart(gProteinAlphaBetaGamma)
                .addAssociatedPart(gdp)
                .setMembraneAnchored(true)
                .build();

        // gdp bound - alpha complex
        ComplexedChemicalEntity gdpGProteinAlpha = new ComplexedChemicalEntity.Builder("G(A):GDP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gdp)
                .setMembraneAnchored(true)
                .build();

        // gtp bound - alpha complex
        ComplexedChemicalEntity gtpGProteinAlpha = new ComplexedChemicalEntity.Builder("G(A):GTP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gtp)
                .setMembraneAnchored(true)
                .build();

        // receptor - ligand
        ComplexedChemicalEntity receptorLigandComplex = new ComplexedChemicalEntity.Builder("V2R:AVP")
                .addAssociatedPart(vasopressinReceptor)
                .addAssociatedPart(vasopressin)
                .build();

        // modules
        // binding R.Gd
        ComplexBuildingReaction binding01 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 01")
                .of(gdpGProteinAlpha, kOnR_Gd)
                .in(NON_MEMBRANE)
                .by(vasopressinReceptor, kOffR_Gd)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGDPReceptor = binding01.getComplex();

        // binding R.Gt
        ComplexBuildingReaction binding02 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 02")
                .of(gtpGProteinAlpha, kOnR_Gt)
                .in(NON_MEMBRANE)
                .by(vasopressinReceptor, kOffR_Gt)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGTPReceptor = binding02.getComplex();

        // binding R.G
        ComplexBuildingReaction binding03 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 03")
                .of(gdpGProteinAlphaBetaGamma, kOnR_G)
                .in(NON_MEMBRANE)
                .by(vasopressinReceptor, kOffR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaBetaGammaGDPReceptor = binding03.getComplex();

        // binding L.R
        ComplexBuildingReaction binding04 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 04")
                .of(vasopressin, kOn_LR)
                .in(NON_MEMBRANE)
                .by(vasopressinReceptor, kOff_LR)
                .to(MEMBRANE)
                .formingComplex(receptorLigandComplex)
                .build();

        // binding LR.Gd
        ComplexBuildingReaction binding05 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 05")
                .of(gdpGProteinAlpha, kOnLR_Gd)
                .in(NON_MEMBRANE)
                .by(receptorLigandComplex, kOffLR_Gd)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGDPReceptorLigandComplex = binding05.getComplex();

        // binding LR.Gt
        ComplexBuildingReaction binding06 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 06")
                .of(gtpGProteinAlpha, kOnLR_Gt)
                .in(NON_MEMBRANE)
                .by(receptorLigandComplex, kOffLR_Gt)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGTPReceptorLigandComplex = binding06.getComplex();

        // binding LR.G
        ComplexBuildingReaction binding07 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 07")
                .of(gdpGProteinAlphaBetaGamma, kOnLR_G)
                .in(NON_MEMBRANE)
                .by(receptorLigandComplex, kOffLR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaBetaGammaGDPReceptorLigandComplex = binding07.getComplex();

        // exchange G
        NthOrderReaction reaction08 = NthOrderReaction.inSimulation(simulation)
                .identifier("reaction 08")
                .addSubstrate(gdpGProteinAlphaBetaGamma)
                .addProduct(gtpGProteinAlpha)
                .addProduct(gProteinBetaGamma)
                .rateConstant(kEf_G)
                .build();

        // exchange Gd, hydrolysis Gt
        EquilibriumReaction reaction09 = EquilibriumReaction.inSimulation(simulation)
                .identifier("reaction 09")
                .addSubstrate(gtpGProteinAlpha)
                .addProduct(gdpGProteinAlpha)
                .forwardsRateConstant(kEf_Gd)
                .backwardsRateConstant(kHf_Gt)
                .build();

        // association Gd
        ComplexBuildingReaction binding10 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 10")
                .of(gdpGProteinAlpha, kAf_Gd)
                .in(NON_MEMBRANE)
                .by(gProteinBetaGamma, kAr_Gd)
                .to(NON_MEMBRANE)
                .formingComplex(gdpGProteinAlphaBetaGamma)
                .build();

        // hydrolysis LR.Gt, exchange LR.Gd
        EquilibriumReaction reaction11 = EquilibriumReaction.inSimulation(simulation)
                .identifier("reaction 11")
                .addSubstrate(alphaGDPReceptorLigandComplex)
                .addProduct(alphaGTPReceptorLigandComplex)
                .forwardsRateConstant(kEf_LRGd)
                .backwardsRateConstant(kHf_LRGt)
                .build();

        // exchange LRG
        ComplexBuildingReaction binding12 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 12")
                .of(gProteinBetaGamma, kEf_zero)
                .in(NON_MEMBRANE)
                .by(alphaGDPReceptorLigandComplex, kEf_LRG)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptorLigandComplex)
                .build();

        // association LRGd
        ComplexBuildingReaction binding13 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 13")
                .of(gProteinBetaGamma, kAf_LRGd)
                .in(NON_MEMBRANE)
                .by(alphaGDPReceptorLigandComplex, kAr_LRGd)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptorLigandComplex)
                .build();

        // hydrolysis R.Gt, exchange R.Gd
        EquilibriumReaction reaction14 = EquilibriumReaction.inSimulation(simulation)
                .identifier("reaction 14")
                .addSubstrate(alphaGDPReceptor)
                .addProduct(alphaGTPReceptor)
                .forwardsRateConstant(kEf_RGt)
                .backwardsRateConstant(kHf_RGt)
                .build();

        // exchange RG
        ComplexBuildingReaction binding15 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 15")
                .of(gProteinBetaGamma, kEf_zero)
                .in(NON_MEMBRANE)
                .by(alphaGTPReceptor, kEf_RG)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptor)
                .build();

        // association RGd
        ComplexBuildingReaction binding16 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 16")
                .of(gProteinBetaGamma, kAf_RGd)
                .in(NON_MEMBRANE)
                .by(alphaGDPReceptor, kAr_RGd)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptor)
                .build();

        // binding L.RGd
        ComplexBuildingReaction binding17 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 17")
                .of(vasopressin, kOn_LRGd)
                .in(NON_MEMBRANE)
                .by(alphaGDPReceptor, kOff_LRGd)
                .to(MEMBRANE)
                .formingComplex(alphaGDPReceptorLigandComplex)
                .build();

        // binding L.RG
        ComplexBuildingReaction binding18 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 18")
                .of(vasopressin, kOn_LRG)
                .in(NON_MEMBRANE)
                .by(alphaBetaGammaGDPReceptor, kOff_LRG)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptorLigandComplex)
                .build();

        // binding L.RGt
        ComplexBuildingReaction binding19 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("reaction 19")
                .of(vasopressin, kOn_LRGt)
                .in(NON_MEMBRANE)
                .by(alphaGTPReceptor, kOff_LRGt)
                .to(MEMBRANE)
                .formingComplex(alphaGTPReceptorLigandComplex)
                .build();

        // add diffusion
        FreeDiffusion.inSimulation(simulation)
                .identifier("free diffusion")
                .forAll(simulation.getChemicalEntities())
                .build();

        System.out.println();
        System.out.println("--- Entities ---");
        System.out.println();
        simulation.getChemicalEntities().stream()
                .sorted(Comparator.comparing(entity -> entity.getIdentifier().getIdentifier()))
                .forEach(entity -> System.out.println(entity.getStringForProtocol() + System.lineSeparator()));

        System.out.println("--- Modules ---");
        System.out.println();
        simulation.getModules().stream()
                .sorted(Comparator.comparing(module -> module.getIdentifier().getIdentifier()))
                .forEach(module -> System.out.println(module.getStringForProtocol() + System.lineSeparator()));


        // create graph
        AutomatonGraph graph = AutomatonGraphs.createRectangularAutomatonGraph(5, 3);
        simulation.setGraph(graph);
        // sections
        EnclosedCompartment innerSection = new EnclosedCompartment("Cyt", "Cytoplasm");
        EnclosedCompartment outerSection = new EnclosedCompartment("Ext", "Extracellular region");
        // add membrane
        Membrane membrane = AutomatonGraphs.splitRectangularGraphWithMembrane(graph, innerSection, outerSection, true);
        // set concentration of vasopressin in the intersitium
        for (AutomatonNode automatonNode : graph.getNodesOfColumn(4)) {
            automatonNode.setConcentration(vasopressin, Quantities.getQuantity(10, NANO(MOLE_PER_LITRE)).to(getTransformedMolarConcentration()));
        }
        // set concentration of vasopressin in the intersitium
        for (AutomatonNode automatonNode : graph.getNodesOfColumn(3)) {
            automatonNode.setConcentration(vasopressin, Quantities.getQuantity(10, NANO(MOLE_PER_LITRE)).to(getTransformedMolarConcentration()));
        }
        // set concentration of receptors in membrane
        for (AutomatonNode automatonNode : graph.getNodesOfColumn(2)) {
            // uncoupled receptor, inside of cell, 843 nm
            automatonNode.setAvailableConcentration(vasopressin, outerSection, Quantities.getQuantity(10, NANO(MOLE_PER_LITRE)).to(getTransformedMolarConcentration()));
            automatonNode.setAvailableConcentration(vasopressinReceptor, membrane.getOuterLayer(), Quantities.getQuantity(843, NANO(MOLE_PER_LITRE)).to(getTransformedMolarConcentration()));
        }
        // set concentration of g proteins
//        for (AutomatonNode automatonNode : graph.getNodesOfColumn(2)) {
//            // heterotrimeric g protein, inside of cell, 520 nm
//            automatonNode.setConcentration(gProteinAlphaBetaGamma, Quantities.getQuantity(520, NANO(MOLE_PER_LITRE)).to(getTransformedMolarConcentration()));
//        }




    }


}
