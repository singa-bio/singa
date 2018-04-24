package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.*;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.UniProtIdentifier;
import de.bioforscher.singa.simulation.model.compartments.CellSectionState;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class GPCRCarrouselModelTest {

    private static final Logger logger = LoggerFactory.getLogger(MonovalentReceptorBindingTest.class);

    private Set<ChemicalEntity> chemicalEntities;

    private Set<Module> modules;

    @Test
    public void testCarrouselModel() {

        Simulation simulation = new Simulation();
        chemicalEntities = new HashSet<>();
        modules = new HashSet<>();

        // basal entities
        // vasopressin v2 receptor
        Receptor vasopressinReceptor = new Receptor.Builder("V2R")
                .additionalIdentifier(new UniProtIdentifier("P30518"))
                .build();
        chemicalEntities.add(vasopressinReceptor);

        // g-protein subunits (reactome https://reactome.org/PathwayBrowser/#/R-HSA-432040&SEL=R-HSA-432197&FLG=O14610)
        Protein gProteinAlpha = new Protein.Builder("G(A)")
                .additionalIdentifier(new UniProtIdentifier("P63092"))
                .build();
        chemicalEntities.add(gProteinAlpha);

        Protein gProteinBeta = new Protein.Builder("G(B)")
                .additionalIdentifier(new UniProtIdentifier("P62873"))
                .build();
        chemicalEntities.add(gProteinBeta);

        Protein gProteinGamma = new Protein.Builder("G(G)")
                .additionalIdentifier(new UniProtIdentifier("P63211"))
                .build();
        chemicalEntities.add(gProteinGamma);

        // g-protein substrates
        ChemicalEntity gdp = new SmallMolecule.Builder("GDP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();
        chemicalEntities.add(gdp);

        ChemicalEntity gtp = new SmallMolecule.Builder("GTP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();
        chemicalEntities.add(gtp);

        // vasopressin
        ChemicalEntity vasopressin = new SmallMolecule.Builder("AVP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:34543"))
                .build();
        chemicalEntities.add(vasopressin);

        // complexed entities
        // g-protein complexes
        // free - beta gamma complex
        ComplexedChemicalEntity gProteinBetaGamma = new ComplexedChemicalEntity.Builder("G(BG)")
                .addAssociatedPart(gProteinBeta)
                .addAssociatedPart(gProteinGamma)
                .build();
        chemicalEntities.add(gProteinGamma);

        // free - alpha beta gamma complex
        ComplexedChemicalEntity gProteinAlphaBetaGamma = new ComplexedChemicalEntity.Builder("G(ABG)")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gProteinBetaGamma)
                .build();
        chemicalEntities.add(gProteinAlphaBetaGamma);

        // gdp bound - alpha beta gamma complex
        ComplexedChemicalEntity gdpGProteinAlphaBetaGamma = new ComplexedChemicalEntity.Builder("G(ABG)GDP")
                .addAssociatedPart(gProteinAlphaBetaGamma)
                .addAssociatedPart(gdp)
                .build();
        chemicalEntities.add(gdpGProteinAlphaBetaGamma);

        // gdp bound - alpha complex
        ComplexedChemicalEntity gdpGProteinAlpha = new ComplexedChemicalEntity.Builder("G(A)GDP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gdp)
                .build();
        chemicalEntities.add(gdpGProteinAlpha);

        // gtp bound - alpha complex
        ComplexedChemicalEntity gtpGProteinAlpha = new ComplexedChemicalEntity.Builder("G(A)GTP")
                .addAssociatedPart(gProteinAlpha)
                .addAssociatedPart(gtp)
                .build();
        chemicalEntities.add(gtpGProteinAlpha);

        // receptor complexes

        // modules
        // gdp bound - alpha g-protein + receptor
        ComplexBuildingReaction binding01 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlpha)
                .in(CellSectionState.NON_MEMBRANE)
                .by(vasopressinReceptor)
                .to(CellSectionState.MEMBRANE);
        ComplexedChemicalEntity alphaGDPReceptor = binding01.getComplex();

        // gtp bound - alpha g-protein + receptor
        ComplexBuildingReaction binding02 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gtpGProteinAlpha)
                .in(CellSectionState.NON_MEMBRANE)
                .by(vasopressinReceptor)
                .to(CellSectionState.MEMBRANE);
        ComplexedChemicalEntity alphaGTPReceptor = binding02.getComplex();

        // gdp bound - alpha beta gamma g-protein + receptor
        ComplexBuildingReaction binding03 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlphaBetaGamma)
                .in(CellSectionState.NON_MEMBRANE)
                .by(vasopressinReceptor)
                .to(CellSectionState.MEMBRANE);
        ComplexedChemicalEntity alphaBetaGammaGDPReceptor = binding03.getComplex();

        // vasopressin + receptor
        ComplexBuildingReaction binding04 = ComplexBuildingReaction.inSimulation(simulation)
                .of(vasopressin)
                .in(CellSectionState.NON_MEMBRANE)
                .by(vasopressinReceptor)
                .to(CellSectionState.MEMBRANE);
        ComplexedChemicalEntity receptorLigand = binding04.getComplex();

        //  gdp bound - alpha gprotein + vasopressin receptor complex
        ComplexBuildingReaction binding05 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlpha)
                .in(CellSectionState.NON_MEMBRANE)
                .by(receptorLigand)
                .to(CellSectionState.MEMBRANE);
        ComplexedChemicalEntity alphaGDPReceptorLigand = binding05.getComplex();

        // gtp bound - alpha complex + vasopressin receptor complex
        ComplexBuildingReaction binding06 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gtpGProteinAlpha)
                .in(CellSectionState.NON_MEMBRANE)
                .by(receptorLigand)
                .to(CellSectionState.MEMBRANE);
        ComplexedChemicalEntity alphaGTPReceptorLigand = binding06.getComplex();

        // vasopressin receptor complex + gdp bound - alpha beta gamma complex
        ComplexBuildingReaction binding07 = ComplexBuildingReaction.inSimulation(simulation)
                .of(gdpGProteinAlphaBetaGamma)
                .in(CellSectionState.NON_MEMBRANE)
                .by(receptorLigand)
                .to(CellSectionState.MEMBRANE);
        ComplexedChemicalEntity alphaBetaGammaGDPReceptorLigand = binding07.getComplex();

        for (ChemicalEntity chemicalEntity : chemicalEntities) {
            System.out.println(chemicalEntity.getStringForProtocol());
        }

    }


}
