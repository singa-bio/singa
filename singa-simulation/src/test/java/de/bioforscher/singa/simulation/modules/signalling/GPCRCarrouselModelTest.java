package de.bioforscher.singa.simulation.modules.signalling;

import de.bioforscher.singa.chemistry.descriptive.entities.*;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.UniProtIdentifier;
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

    @Test
    public void testCarrouselModel() {

        chemicalEntities = new HashSet<>();

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
        ChemicalEntity gdp = new Species.Builder("GDP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();
        chemicalEntities.add(gdp);

        ChemicalEntity gtp = new Species.Builder("GTP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();
        chemicalEntities.add(gtp);

        // vasopressin
        ChemicalEntity vasopressin = new Species.Builder("AVP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:9937"))
                .build();
        chemicalEntities.add(vasopressin);

        // complexed entities
        // g-protein complexes
        // free - alpha beta complex
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
        // receptor + gdp bound - alpha complex
        ComplexedChemicalEntity alphaGDPReceptor = new ComplexedChemicalEntity.Builder("V2R-G(A)GDP")
                .addAssociatedPart(vasopressinReceptor)
                .addAssociatedPart(gdpGProteinAlpha)
                .build();
        chemicalEntities.add(alphaGDPReceptor);

        // receptor + gtp bound - alpha complex
        ComplexedChemicalEntity alphaGTPReceptor = new ComplexedChemicalEntity.Builder("V2R-G(A)GTP")
                .addAssociatedPart(vasopressinReceptor)
                .addAssociatedPart(gtpGProteinAlpha)
                .build();
        chemicalEntities.add(alphaGTPReceptor);

        // receptor + gdp bound - alpha beta gamma complex
        ComplexedChemicalEntity alphaBetaGammaGDPReceptor = new ComplexedChemicalEntity.Builder("V2R-G(ABG)GDP")
                .addAssociatedPart(vasopressinReceptor)
                .addAssociatedPart(gdpGProteinAlphaBetaGamma)
                .build();
        chemicalEntities.add(alphaBetaGammaGDPReceptor);

        // vasopressin receptor complex
        ComplexedChemicalEntity receptorLigand = new ComplexedChemicalEntity.Builder("V2R-AVP")
                .addAssociatedPart(vasopressinReceptor)
                .addAssociatedPart(vasopressin)
                .build();
        chemicalEntities.add(receptorLigand);

        // vasopressin receptor complex + gdp bound - alpha complex
        ComplexedChemicalEntity alphaGDPReceptorLigand = new ComplexedChemicalEntity.Builder("V2R-AVP-G(A)GDP")
                .addAssociatedPart(receptorLigand)
                .addAssociatedPart(gdpGProteinAlpha)
                .build();
        chemicalEntities.add(alphaGDPReceptorLigand);

        // vasopressin receptor complex + gtp bound - alpha complex
        ComplexedChemicalEntity alphaGTPReceptorLigand = new ComplexedChemicalEntity.Builder("V2R-AVP-G(A)GTP")
                .addAssociatedPart(receptorLigand)
                .addAssociatedPart(gtpGProteinAlpha)
                .build();
        chemicalEntities.add(alphaGTPReceptorLigand);

        // vasopressin receptor complex + gdp bound - alpha beta gamma complex
        ComplexedChemicalEntity alphaBetaGammaGDPReceptorLigand = new ComplexedChemicalEntity.Builder("V2R-G(ABG)GDP")
                .addAssociatedPart(receptorLigand)
                .addAssociatedPart(gdpGProteinAlphaBetaGamma)
                .build();
        chemicalEntities.add(alphaBetaGammaGDPReceptorLigand);


        for (ChemicalEntity chemicalEntity : chemicalEntities) {
            System.out.println(chemicalEntity.getStringForProtocol());
        }

    }


}
