package bio.singa.simulation.model.simulation;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.chemistry.features.permeability.MembranePermeability;
import bio.singa.chemistry.features.permeability.OsmoticPermeability;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.structure.features.molarmass.MolarMass;
import tec.uom.se.quantity.Quantities;

import static bio.singa.chemistry.annotations.AnnotationType.NOTE;
import static bio.singa.chemistry.features.diffusivity.Diffusivity.SQUARE_CENTIMETRE_PER_SECOND;
import static bio.singa.chemistry.features.permeability.MembranePermeability.CENTIMETRE_PER_SECOND;
import static bio.singa.features.model.Evidence.NO_EVIDENCE;

/**
 * @author cl
 */
public class EntitySupplier {

    final SmallMolecule water;
    final SmallMolecule solute;

    // vasopressin and receptor
    final Protein v2r;
    final ChemicalEntity avp;
    final ComplexedChemicalEntity v2rAvp;

    // g proteins
    final Protein gA;
    final Protein gB;
    final Protein gG;
    final ChemicalEntity gdp;
    final ChemicalEntity gtp;

    final ComplexedChemicalEntity gBG;
    final ComplexedChemicalEntity gABG;
    final ComplexedChemicalEntity gABGd;
    final ComplexedChemicalEntity gAd;
    final ComplexedChemicalEntity gAt;

    // protein kinase a
    final Protein akap;
    final Protein pkaC;
    final Protein pkaR;
    final SmallMolecule camp;

    final ComplexedChemicalEntity akap2R;

    final ComplexedChemicalEntity akap2R1C;
    final ComplexedChemicalEntity akap2R1C4cAmp;

    final ComplexedChemicalEntity akap2R2C;
    final ComplexedChemicalEntity akap2R2C1cAmp;
    final ComplexedChemicalEntity akap2R2C2cAmp;
    final ComplexedChemicalEntity akap2R2C3cAmp;
    final ComplexedChemicalEntity akap2R2C4cAmp;

    final ComplexedChemicalEntity akap2R4cAmp;
    final ComplexedChemicalEntity akap2R3cAmp;
    final ComplexedChemicalEntity akap2R2cAmp;
    final ComplexedChemicalEntity akap2R1cAmp;

    final SmallMolecule atp;
    final SmallMolecule adp;

    final ComplexedChemicalEntity pkaCatp;
    final ComplexedChemicalEntity pkaCatpAqp2;
    final ComplexedChemicalEntity pkaCadpAqp2p;

    // adenylate cyclase
    final Protein ac6;
    final ComplexedChemicalEntity ac6gA;

    // snares for membrane fusion
    final Protein vamp2;
    final Protein stx3;
    final Protein snap23;
    final ComplexedChemicalEntity snareComplex;

    // aquaporin 2
    final Protein aqp2;
    final SmallMolecule phosphate;
    final ComplexedChemicalEntity aqp2p;
    final ComplexedChemicalEntity aqp2pp;

    final Protein trp;
    final ComplexedChemicalEntity aqp2ptrp;

    final Protein aqp3;
    final Protein aqp4;

    final Protein pp1;
    final ComplexedChemicalEntity aqp2ppPP1;
    final ComplexedChemicalEntity aqp2pPP1;

    final Protein rab11;
    final Protein fib2;
    final Protein myoVb;
    final ComplexedChemicalEntity myoComplex;

    final Protein fib3;
    final Protein dynli;
    final Protein dynhc;
    final ComplexedChemicalEntity dyneinComplex;

    final ChemicalEntity clathrinHeavyChain;
    final ChemicalEntity clathrinLightChain;
    final ComplexedChemicalEntity clathrinTriskelion;

    final Protein pp1R;
    final ComplexedChemicalEntity pp1Rp;
    final ComplexedChemicalEntity pp1RpPP1;
    final ComplexedChemicalEntity pkaCatpPp1R;
    final ComplexedChemicalEntity pkaCadpPp1Rp;

    public EntitySupplier() {

        water = SmallMolecule.create("water")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15377"))
                .assignFeature(new Diffusivity(Quantities.getQuantity(2.6e-6, SQUARE_CENTIMETRE_PER_SECOND), NO_EVIDENCE))
                .assignFeature(new MembranePermeability(Quantities.getQuantity(3.5e-3 * 0.5, CENTIMETRE_PER_SECOND), NO_EVIDENCE))
                .build();

        solute = SmallMolecule.create("solutes")
                .assignFeature(new MolarMass(52.0, NO_EVIDENCE))
                .build();

        aqp3 = new Protein.Builder("AQP3")
                .additionalIdentifier(new UniProtIdentifier("Q92482"))
                .assignFeature(new OsmoticPermeability(2.2e-14, NO_EVIDENCE))
                .build();

        aqp4 = new Protein.Builder("AQP4")
                .additionalIdentifier(new UniProtIdentifier("P55087"))
                .assignFeature(new OsmoticPermeability(7.0e-14, NO_EVIDENCE))
                .build();

        // vasopressin v2 receptor
        v2r = new Protein.Builder("V2R")
                .additionalIdentifier(new UniProtIdentifier("P30518"))
                .build();

        // vasopressin
        avp = new SmallMolecule.Builder("AVP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:34543"))
                .build();

        // g-protein subunits
        gA = new Protein.Builder("G(A)")
                .additionalIdentifier(new UniProtIdentifier("P63092"))
                .build();

        gB = new Protein.Builder("G(B)")
                .additionalIdentifier(new UniProtIdentifier("P62873"))
                .build();

        gG = new Protein.Builder("G(G)")
                .additionalIdentifier(new UniProtIdentifier("P63211"))
                .build();

        // g-protein substrates
        gdp = new SmallMolecule.Builder("GDP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        gtp = new SmallMolecule.Builder("GTP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17552"))
                .build();

        // complexed entities
        // g-protein complexes
        // free - beta gamma complex
        gBG = new ComplexedChemicalEntity.Builder("G(BG)")
                .addAssociatedPart(gB)
                .addAssociatedPart(gG)
                .setMembraneAnchored(true)
                .build();

        // free - alpha beta gamma complex
        gABG = new ComplexedChemicalEntity.Builder("G(ABG)")
                .addAssociatedPart(gA)
                .addAssociatedPart(gBG)
                .setMembraneAnchored(true)
                .build();

        // gdp bound - alpha beta gamma complex
        gABGd = new ComplexedChemicalEntity.Builder("G(ABG):GDP")
                .addAssociatedPart(gABG)
                .addAssociatedPart(gdp)
                .setMembraneAnchored(true)
                .build();

        // gdp bound - alpha complex
        gAd = new ComplexedChemicalEntity.Builder("G(A):GDP")
                .addAssociatedPart(gA)
                .addAssociatedPart(gdp)
                .setMembraneAnchored(true)
                .build();

        // gtp bound - alpha complex
        gAt = new ComplexedChemicalEntity.Builder("G(A):GTP")
                .addAssociatedPart(gA)
                .addAssociatedPart(gtp)
                .setMembraneAnchored(true)
                .build();

        // receptor - ligand
        v2rAvp = new ComplexedChemicalEntity.Builder("V2R:AVP")
                .addAssociatedPart(v2r)
                .addAssociatedPart(avp)
                .build();

        akap = new Protein.Builder("AKAP")
                .additionalIdentifier(new UniProtIdentifier("Q9P0M2"))
                .build();

        pkaC = new Protein.Builder("PKAC")
                .additionalIdentifier(new UniProtIdentifier("P22694"))
                .build();

        pkaR = new Protein.Builder("PKAR")
                .additionalIdentifier(new UniProtIdentifier("P31323"))
                .build();

        camp = SmallMolecule.create("cAMP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17489"))
                .build();

        akap2R = ComplexedChemicalEntity.create("AKP:2PKAR")
                .addAssociatedPart(akap)
                .addAssociatedPart(pkaR, 2)
                .build();

        akap2R1C = ComplexedChemicalEntity.create("AKP:2PKAR:PKAC")
                .addAssociatedPart(akap)
                .addAssociatedPart(pkaR, 2)
                .addAssociatedPart(pkaC)
                .build();

        akap2R1C4cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:PKAC:4cAMP")
                .addAssociatedPart(akap)
                .addAssociatedPart(pkaR, 2)
                .addAssociatedPart(pkaC)
                .addAssociatedPart(camp, 4)
                .build();

        akap2R2C = ComplexedChemicalEntity.create("AKP:2PKAR:2PKAC")
                .addAssociatedPart(akap)
                .addAssociatedPart(pkaR, 2)
                .addAssociatedPart(pkaC, 2)
                .build();

        akap2R2C1cAmp = ComplexedChemicalEntity.create("AKP:PKA:cAMP")
                .addAssociatedPart(akap2R2C)
                .addAssociatedPart(camp)
                .build();

        akap2R2C2cAmp = ComplexedChemicalEntity.create("AKP:PKA:2cAMP")
                .addAssociatedPart(akap2R2C)
                .addAssociatedPart(camp, 2)
                .build();

        akap2R2C3cAmp = ComplexedChemicalEntity.create("AKP:PKA:3cAMP")
                .addAssociatedPart(akap2R2C)
                .addAssociatedPart(camp, 3)
                .build();

        akap2R2C4cAmp = ComplexedChemicalEntity.create("AKP:PKA:4cAMP")
                .addAssociatedPart(akap2R2C)
                .addAssociatedPart(camp, 4)
                .build();

        akap2R4cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:4cAMP")
                .addAssociatedPart(akap2R)
                .addAssociatedPart(camp, 4)
                .build();

        akap2R3cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:3cAMP")
                .addAssociatedPart(akap2R)
                .addAssociatedPart(camp, 3)
                .build();

        akap2R2cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:2cAMP")
                .addAssociatedPart(akap2R)
                .addAssociatedPart(camp, 2)
                .build();

        akap2R1cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:1cAMP")
                .addAssociatedPart(akap2R)
                .addAssociatedPart(camp)
                .build();

        // adenylate cyclase
        ac6 = new Protein.Builder("AC6")
                .additionalIdentifier(new UniProtIdentifier("O43306"))
                .build();

        ac6gA = ComplexedChemicalEntity.create("AC6:G(A)")
                .addAssociatedPart(ac6)
                .addAssociatedPart(gAt)
                .setMembraneAnchored(true)
                .build();

        vamp2 = new Protein.Builder("VAMP2")
                .assignFeature(new UniProtIdentifier("Q15836"))
                .annotation(new Annotation<>(NOTE, "SNARE type", "R-SNARE"))
                .build();

        stx3 = new Protein.Builder("STX3")
                .assignFeature(new UniProtIdentifier("Q13277"))
                .annotation(new Annotation<>(NOTE, "SNARE type", "Qa-SNARE"))
                .build();

        snap23 = new Protein.Builder("SNAP23")
                .assignFeature(new UniProtIdentifier("O00161"))
                .annotation(new Annotation<>(NOTE, "SNARE type", "Qbc-SNARE"))
                .build();

        snareComplex = ComplexedChemicalEntity.create(stx3.getIdentifier().getContent() + ":" + snap23.getIdentifier().getContent())
                .addAssociatedPart(stx3)
                .addAssociatedPart(snap23)
                .annotation(new Annotation<>(NOTE, "SNARE type", "Qabc-SNARE"))
                .build();

        // aquaporin 2
        aqp2 = new Protein.Builder("AQP2")
                .additionalIdentifier(new UniProtIdentifier("P41181"))
                .assignFeature(new OsmoticPermeability(5.31e-14, NO_EVIDENCE))
                .build();

        // inorcanic phosphate
        phosphate = SmallMolecule.create("P")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:24838"))
                .build();

        // aquaporin 2 265-phosphorylated
        aqp2p = new ComplexedChemicalEntity.Builder("AQP2-256P")
                .addAssociatedPart(aqp2)
                .addAssociatedPart(phosphate)
                .build();

        // aquaporin 2 265 and 269 phosphorylated
        aqp2pp = new ComplexedChemicalEntity.Builder("AQP2-256P-269P")
                .addAssociatedPart(aqp2p)
                .addAssociatedPart(phosphate)
                .build();

        trp = new Protein.Builder("TRP")
                .additionalIdentifier(new UniProtIdentifier("P09493"))
                .build();

        aqp2ptrp = new ComplexedChemicalEntity.Builder("AQP2-256P:TRP")
                .addAssociatedPart(aqp2p)
                .addAssociatedPart(trp)
                .build();

        // Ras-related protein Rab-11A
        rab11 = new Protein.Builder("RAB11")
                .additionalIdentifier(new UniProtIdentifier("P62491"))
                .build();

        // Rab11 family-interacting protein 2
        fib2 = new Protein.Builder("FIP2")
                .additionalIdentifier(new UniProtIdentifier("Q7L804"))
                .build();

        // Unconventional myosin-Vb
        myoVb = new Protein.Builder("MYOVB")
                .additionalIdentifier(new UniProtIdentifier("Q9ULV0"))
                .build();

        // Rab11 family-interacting protein 3
        fib3 = new Protein.Builder("FIP3")
                .additionalIdentifier(new UniProtIdentifier("O75154"))
                .build();

        // Cytoplasmic dynein 1 light intermediate chain 1
        dynli = new Protein.Builder("DYNLI")
                .additionalIdentifier(new UniProtIdentifier("Q9Y6G9"))
                .build();

        // Cytoplasmic dynein 1 heavy chain 1
        dynhc = new Protein.Builder("DYNHC")
                .additionalIdentifier(new UniProtIdentifier("Q14204"))
                .build();

        // attached dynein complex
        dyneinComplex = new ComplexedChemicalEntity.Builder("RAB11:FIP2:DYNLI:DYNHC")
                .addAssociatedPart(rab11)
                .addAssociatedPart(fib3)
                .addAssociatedPart(dynli)
                .addAssociatedPart(dynhc)
                .build();

        myoComplex = ComplexedChemicalEntity.create("RAB11:FIB2:MYOVB")
                .addAssociatedPart(rab11)
                .addAssociatedPart(fib2)
                .addAssociatedPart(myoVb)
                .build();

        // serine/threonine-protein phosphatase
        pp1 = new Protein.Builder("PP1")
                .additionalIdentifier(new UniProtIdentifier("P62136"))
                .build();

        aqp2ppPP1 = new ComplexedChemicalEntity.Builder("PP1:AQP2-265P-269P")
                .addAssociatedPart(aqp2pp)
                .addAssociatedPart(pp1)
                .build();

        aqp2pPP1 = new ComplexedChemicalEntity.Builder("PP1:AQP2-265P-269P")
                .addAssociatedPart(aqp2p)
                .addAssociatedPart(pp1)
                .build();

        // setup species for clathrin decay
        clathrinHeavyChain = new Protein.Builder("Clathrin heavy chain")
                .assignFeature(new UniProtIdentifier("Q00610"))
                .build();

        clathrinLightChain = new Protein.Builder("Clathrin light chain")
                .assignFeature(new UniProtIdentifier("P09496"))
                .build();

        clathrinTriskelion = ComplexedChemicalEntity.create("Clathrin Triskelion")
                .addAssociatedPart(clathrinHeavyChain, 3)
                .addAssociatedPart(clathrinLightChain, 3)
                .build();

        // atp
        atp = SmallMolecule.create("ATP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:15422"))
                .build();

        // adp
        adp = SmallMolecule.create("ADP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:16761"))
                .build();

        // intermediate complex 1
        pkaCatp = new ComplexedChemicalEntity.Builder("PKAC:ATP")
                .addAssociatedPart(pkaC)
                .addAssociatedPart(atp)
                .build();

        // intermediate complex 2
        pkaCatpAqp2 = new ComplexedChemicalEntity.Builder("PKAC:AQP2:ATP")
                .addAssociatedPart(pkaC)
                .addAssociatedPart(atp)
                .addAssociatedPart(aqp2)
                .build();

        // intermediate complex 3
        pkaCadpAqp2p = new ComplexedChemicalEntity.Builder("PKAC:AQP2P:ADP")
                .addAssociatedPart(pkaC)
                .addAssociatedPart(adp)
                .addAssociatedPart(aqp2p)
                .build();

        pp1R = new Protein.Builder("PP1R")
                .additionalIdentifier(new UniProtIdentifier("Q13522"))
                .build();

        pp1Rp = ComplexedChemicalEntity.create("PP1R-35P")
                .addAssociatedPart(pp1R)
                .addAssociatedPart(phosphate)
                .build();

        pp1RpPP1 = ComplexedChemicalEntity.create("PP1R-35P:PP1")
                .addAssociatedPart(pp1Rp)
                .addAssociatedPart(pp1)
                .build();

        pkaCatpPp1R = ComplexedChemicalEntity.create("PKAC:PP1R:ATP")
                .addAssociatedPart(pkaC)
                .addAssociatedPart(atp)
                .addAssociatedPart(pp1R)
                .build();

        pkaCadpPp1Rp = ComplexedChemicalEntity.create("PKAC:PP1R:ATP")
                .addAssociatedPart(pkaC)
                .addAssociatedPart(adp)
                .addAssociatedPart(pp1Rp)
                .build();

    }

}
