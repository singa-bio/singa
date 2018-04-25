package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.*;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.BackwardsRateConstant;
import de.bioforscher.singa.chemistry.descriptive.features.reactions.ForwardsRateConstant;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.implementations.EquilibriumReaction;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import static de.bioforscher.singa.chemistry.descriptive.features.reactions.TurnoverNumber.PER_SECOND;
import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.MEMBRANE;
import static de.bioforscher.singa.simulation.model.compartments.CellSectionState.NON_MEMBRANE;

/**
 * @author cl
 */
public class GPCRCarrouselModelTest {

    private static final Logger logger = LoggerFactory.getLogger(MonovalentReceptorBindingTest.class);

    private static final FeatureOrigin BUSH2016 = new FeatureOrigin(FeatureOrigin.OriginType.MANUAL_ANNOTATION, "Bush 2016", "Bush, Alan, et al. \"Yeast GPCR signaling reflects the fraction of occupied receptors, not the number.\" Molecular systems biology 12.12 (2016): 898.")

    @Test
    public void testCarrouselModel() {

        Simulation simulation = new Simulation();

        // reactome https://reactome.org/PathwayBrowser/#/R-HSA-432040&SEL=R-HSA-432197&FLG=O14610)
        // biomodels yeast carrousel https://www.ebi.ac.uk/biomodels-main/BIOMD0000000637

        // parameters
        BackwardsRateConstant kOffR_G = new BackwardsRateConstant(Quantities.getQuantity(0.1, PER_SECOND),  BUSH2016);
        BackwardsRateConstant kOffLR_G, kOffR_Gt, kOffLR_Gt, kOffR_Gd, kOffLR_Gd;
        kOffLR_G = kOffR_Gt = kOffLR_Gt = kOffR_Gd = kOffLR_Gd = kOffR_G;

        BackwardsRateConstant kOffL_R = new BackwardsRateConstant(Quantities.getQuantity(0.001, PER_SECOND), BUSH2016);
        BackwardsRateConstant kOffL_RG, kOffL_RGt, kOffL_RGd;
        kOffL_RG = kOffL_RGt = kOffL_RGd = kOffL_R;

        ForwardsRateConstant kOnR_G = new ForwardsRateConstant(Quantities.getQuantity(0.00461111111111111, PER_SECOND),  BUSH2016);
        ForwardsRateConstant kOnLR_G, kOnR_Gt, kOnLR_Gt, kOnR_Gd, kOnLR_Gd;
        kOnLR_G = kOnR_Gt = kOnLR_Gt = kOnR_Gd = kOnLR_Gd = kOnR_G;

        ForwardsRateConstant kEf_Gd = new ForwardsRateConstant(Quantities.getQuantity(6.2e-4, PER_SECOND), BUSH2016);
        ForwardsRateConstant kEf_RGd, kEf_G, kEf_RG;
        kEf_RGd = kEf_G = kEf_RG = kEf_Gd;


        // entities
        // vasopressin v2 receptor
        Receptor vasopressinReceptor = new Receptor.Builder("V2R")
                .additionalIdentifier(new UniProtIdentifier("P30518"))
                .build();

        // vasopressin
        ChemicalEntity vasopressin = new SmallMolecule.Builder("AVP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:34543"))
                .build();

        // g-protein subunits (
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
                .build();

        // free - alpha beta gamma complex
        ComplexedChemicalEntity gProteinAlphaBetaGamma = new ComplexedChemicalEntity.Builder("G(ABG)")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gProteinBetaGamma)
                .build();

        // gdp bound - alpha beta gamma complex
        ComplexedChemicalEntity gdpGProteinAlphaBetaGamma = new ComplexedChemicalEntity.Builder("G(ABG):GDP")
                .addAssociatedPart(gProteinAlphaBetaGamma)
                .addAssociatedPart(gdp)
                .build();

        // gdp bound - alpha complex
        ComplexedChemicalEntity gdpGProteinAlpha = new ComplexedChemicalEntity.Builder("G(A):GDP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gdp)
                .build();

        // gtp bound - alpha complex
        ComplexedChemicalEntity gtpGProteinAlpha = new ComplexedChemicalEntity.Builder("G(A):GTP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gtp)
                .build();

        ComplexedChemicalEntity receptorLigand = new ComplexedChemicalEntity.Builder("G(BG)")
                .addAssociatedPart(vasopressinReceptor)
                .addAssociatedPart(vasopressin)
                .build();

        // modules
        // binding R.Gd
        ComplexBuildingReaction binding01 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlpha, kOnR_Gd)
                .in(NON_MEMBRANE)
                .by(vasopressinReceptor, kOffR_Gd)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGDPReceptor = binding01.getComplex();

        // binding R.Gt
        ComplexBuildingReaction binding02 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gtpGProteinAlpha, kOnR_Gt)
                .in(NON_MEMBRANE)
                .by(vasopressinReceptor, kOffR_Gt)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGTPReceptor = binding02.getComplex();

        // binding R.G
        ComplexBuildingReaction binding03 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlphaBetaGamma, kOnR_G)
                .in(NON_MEMBRANE)
                .by(vasopressinReceptor, kOffR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaBetaGammaGDPReceptor = binding03.getComplex();

        // vasopressin + receptor
        EquilibriumReaction reaction04 = EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(receptorLigand)
                .addProduct(vasopressinReceptor)
                .build();

        // binding LR.Gd
        ComplexBuildingReaction binding05 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlpha, kOnLR_Gd)
                .in(NON_MEMBRANE)
                .by(receptorLigand, kOffLR_Gd)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGDPReceptorLigand = binding05.getComplex();

        // binding LR.Gt
        ComplexBuildingReaction binding06 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gtpGProteinAlpha, kOnLR_Gt)
                .in(NON_MEMBRANE)
                .by(receptorLigand, kOffLR_Gt)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaGTPReceptorLigand = binding06.getComplex();

        // binding LR.G
        ComplexBuildingReaction binding07 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlphaBetaGamma, kOnLR_G)
                .in(NON_MEMBRANE)
                .by(receptorLigand, kOffLR_G)
                .to(MEMBRANE)
                .build();
        ComplexedChemicalEntity alphaBetaGammaGDPReceptorLigand = binding07.getComplex();

        // TODO next exchange G
        EquilibriumReaction reaction08 = EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(gdpGProteinAlphaBetaGamma)
                .addProduct(gtpGProteinAlpha)
                .addProduct(gProteinBetaGamma)
                .build();

        EquilibriumReaction reaction09 = EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(gtpGProteinAlpha)
                .addProduct(gdpGProteinAlpha)
                .build();

        ComplexBuildingReaction binding10 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlpha)
                .in(NON_MEMBRANE)
                .by(gProteinBetaGamma)
                .to(NON_MEMBRANE)
                .formingComplex(gdpGProteinAlphaBetaGamma)
                .build();

        EquilibriumReaction reaction11 = EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(alphaGDPReceptorLigand)
                .addProduct(alphaGTPReceptorLigand)
                .build();

        ComplexBuildingReaction binding12 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gProteinBetaGamma)
                .in(NON_MEMBRANE)
                .by(alphaGTPReceptorLigand)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptorLigand)
                .build();

        ComplexBuildingReaction binding13 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gProteinBetaGamma)
                .in(NON_MEMBRANE)
                .by(alphaGDPReceptorLigand)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptorLigand)
                .build();

        EquilibriumReaction reaction14 = EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(alphaGDPReceptor)
                .addProduct(alphaGTPReceptor)
                .build();

        ComplexBuildingReaction binding15 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gProteinBetaGamma)
                .in(NON_MEMBRANE)
                .by(alphaGTPReceptor)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptor)
                .build();

        ComplexBuildingReaction binding16 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gProteinBetaGamma)
                .in(NON_MEMBRANE)
                .by(alphaGDPReceptor)
                .to(MEMBRANE)
                .formingComplex(alphaBetaGammaGDPReceptor)
                .build();

        EquilibriumReaction reaction17 = EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(alphaGDPReceptorLigand)
                .addProduct(alphaGDPReceptor)
                .build();

        EquilibriumReaction reaction18 = EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(alphaBetaGammaGDPReceptorLigand)
                .addProduct(alphaBetaGammaGDPReceptor)
                .build();

        EquilibriumReaction reaction19 = EquilibriumReaction.inSimulation(simulation)
                .addSubstrate(alphaGTPReceptorLigand)
                .addProduct(alphaGTPReceptor)
                .build();

        System.out.println();
        for (ChemicalEntity chemicalEntity : simulation.getChemicalEntities()) {
            System.out.println(chemicalEntity.getStringForProtocol());
            System.out.println();
        }

        for (Module module : simulation.getModules()) {
            System.out.println(module);
        }

    }


}
