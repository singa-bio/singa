package bio.singa.simulation.model.modules.meta;

import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.entities.SmallMolecule;
import bio.singa.chemistry.features.reactions.RateConstant;
import bio.singa.features.identifiers.ChEBIIdentifier;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.simulation.model.modules.concentration.imlementations.ComplexBuildingReaction;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellSubsection;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.simulation.Simulation;

import static bio.singa.features.units.UnitProvider.MOLE_PER_LITRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class ProteinKinaseA extends MetaModule {


    private Protein adapter;
    private Protein catalytic;
    private Protein regulator;
    private SmallMolecule camp;
    private ComplexedChemicalEntity adapter2Regulator;
    private ComplexedChemicalEntity adapter2Regulator1Catalytic;
    private ComplexedChemicalEntity adapter2Regulator1Catalytic4cAmp;
    private ComplexedChemicalEntity adapter2Regulator2Catalytic;
    private ComplexedChemicalEntity adapter2Regulator2Catalytic1cAmp;

    public ProteinKinaseA(Simulation simulation) {
        super(simulation);
    }

    public static void addTo(Simulation simulation) {
        ProteinKinaseA metaModule = new ProteinKinaseA(simulation);
        metaModule.setupEntities();
    }

    private void setupEntities() {

        UniProtIdentifier q9P0M2 = new UniProtIdentifier("Q9P0M2");
        adapter = new Protein.Builder("AKAP")
                .name("A-kinase anchor protein 7 isoform gamma")
                .additionalIdentifier(q9P0M2)
                .build();
        addEntity(q9P0M2, adapter);

        UniProtIdentifier p22694 = new UniProtIdentifier("P22694");
        catalytic = new Protein.Builder("PKAC")
                .name("cAMP-dependent protein kinase catalytic subunit beta")
                .additionalIdentifier(p22694)
                .build();
        addEntity(p22694, catalytic);

        UniProtIdentifier p31323 = new UniProtIdentifier("P31323");
        regulator = new Protein.Builder("PKAR")
                .name("cAMP-dependent protein kinase type II-beta regulatory subunit")
                .additionalIdentifier(p31323)
                .build();
        addEntity(p31323, regulator);

        ChEBIIdentifier chebi17489 = new ChEBIIdentifier("CHEBI:17489");
        camp = SmallMolecule.create("cAMP")
                .name("Cyclic adenosine monophosphate")
                .additionalIdentifier(chebi17489)
                .build();
        addEntity(chebi17489, camp);

        adapter2Regulator = ComplexedChemicalEntity.create("AKP:2PKAR")
                .addAssociatedPart(adapter)
                .addAssociatedPart(regulator, 2)
                .build();
        addEntity(adapter2Regulator);

        adapter2Regulator1Catalytic = ComplexedChemicalEntity.create("AKP:2PKAR:PKAC")
                .addAssociatedPart(adapter)
                .addAssociatedPart(regulator, 2)
                .addAssociatedPart(catalytic)
                .build();
        addEntity(adapter2Regulator1Catalytic);

        adapter2Regulator1Catalytic4cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:PKAC:4cAMP")
                .addAssociatedPart(adapter)
                .addAssociatedPart(regulator, 2)
                .addAssociatedPart(catalytic)
                .addAssociatedPart(camp, 4)
                .build();
        addEntity(adapter2Regulator1Catalytic4cAmp);

        adapter2Regulator2Catalytic = ComplexedChemicalEntity.create("AKP:2PKAR:2PKAC")
                .addAssociatedPart(adapter)
                .addAssociatedPart(regulator, 2)
                .addAssociatedPart(catalytic, 2)
                .build();
        addEntity(adapter2Regulator2Catalytic);

        adapter2Regulator2Catalytic1cAmp = ComplexedChemicalEntity.create("AKP:PKA:cAMP")
                .addAssociatedPart(adapter2Regulator2Catalytic)
                .addAssociatedPart(camp)
                .build();
        addEntity(adapter2Regulator2Catalytic1cAmp);

        ComplexedChemicalEntity adapter2Regulator2Catalytic2cAmp = ComplexedChemicalEntity.create("AKP:PKA:2cAMP")
                .addAssociatedPart(adapter2Regulator2Catalytic)
                .addAssociatedPart(camp, 2)
                .build();
        addEntity(adapter2Regulator2Catalytic2cAmp);

        ComplexedChemicalEntity adapter2Regulator2Catalytic3cAmp = ComplexedChemicalEntity.create("AKP:PKA:3cAMP")
                .addAssociatedPart(adapter2Regulator2Catalytic)
                .addAssociatedPart(camp, 3)
                .build();
        addEntity(adapter2Regulator2Catalytic3cAmp);

        ComplexedChemicalEntity adapter2Regulator2Catalytic4cAmp = ComplexedChemicalEntity.create("AKP:PKA:4cAMP")
                .addAssociatedPart(adapter2Regulator2Catalytic)
                .addAssociatedPart(camp, 4)
                .build();
        addEntity(adapter2Regulator2Catalytic4cAmp);

        ComplexedChemicalEntity adapter2Regulator4cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:4cAMP")
                .addAssociatedPart(adapter2Regulator)
                .addAssociatedPart(camp, 4)
                .build();
        addEntity(adapter2Regulator4cAmp);

        ComplexedChemicalEntity adapter2Regulator3cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:3cAMP")
                .addAssociatedPart(adapter2Regulator)
                .addAssociatedPart(camp, 3)
                .build();
        addEntity(adapter2Regulator3cAmp);

        ComplexedChemicalEntity adapter2Regulator2cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:2cAMP")
                .addAssociatedPart(adapter2Regulator)
                .addAssociatedPart(camp, 2)
                .build();
        addEntity(adapter2Regulator2cAmp);

        ComplexedChemicalEntity adapter2Regulator1cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:1cAMP")
                .addAssociatedPart(adapter2Regulator)
                .addAssociatedPart(camp)
                .build();
        addEntity(adapter2Regulator1cAmp);

    }

    public static void main(String[] args) {

        Simulation simulation = new Simulation();
        CellSubsection cytoplasm = new CellSubsection("Cytoplasm");
        CellSubsection interstitium = new CellSubsection("Interstitium");
        CellSubsection basolateralMembrane = new CellSubsection("Basolateral membrane");

        CellRegion interstitiumMembrane = new CellRegion("Interstitium membrane");
        interstitiumMembrane.addSubSection(CellTopology.INNER, cytoplasm);
        interstitiumMembrane.addSubSection(CellTopology.MEMBRANE, basolateralMembrane);
        interstitiumMembrane.addSubSection(CellTopology.OUTER, interstitium);

        Protein adapter = new Protein.Builder("AKAP")
                .additionalIdentifier(new UniProtIdentifier("Q9P0M2"))
                .build();

        Protein catalytic = new Protein.Builder("PKAC")
                .additionalIdentifier(new UniProtIdentifier("P22694"))
                .build();

        Protein regulator = new Protein.Builder("PKAR")
                .additionalIdentifier(new UniProtIdentifier("P31323"))
                .build();

        SmallMolecule camp = SmallMolecule.create("cAMP")
                .additionalIdentifier(new ChEBIIdentifier("CHEBI:17489"))
                .build();

        ComplexedChemicalEntity adapter2Regulator = ComplexedChemicalEntity.create("AKP:2PKAR")
                .addAssociatedPart(adapter)
                .addAssociatedPart(regulator, 2)
                .build();

        ComplexedChemicalEntity adapter2Regulator1Catalytic = ComplexedChemicalEntity.create("AKP:2PKAR:PKAC")
                .addAssociatedPart(adapter)
                .addAssociatedPart(regulator, 2)
                .addAssociatedPart(catalytic)
                .build();

        ComplexedChemicalEntity adapter2Regulator1Catalytic4cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:PKAC:4cAMP")
                .addAssociatedPart(adapter)
                .addAssociatedPart(regulator, 2)
                .addAssociatedPart(catalytic)
                .addAssociatedPart(camp, 4)
                .build();

        ComplexedChemicalEntity adapter2Regulator2Catalytic = ComplexedChemicalEntity.create("AKP:2PKAR:2PKAC")
                .addAssociatedPart(adapter)
                .addAssociatedPart(regulator, 2)
                .addAssociatedPart(catalytic, 2)
                .build();

        ComplexedChemicalEntity adapter2Regulator2Catalytic1cAmp = ComplexedChemicalEntity.create("AKP:PKA:cAMP")
                .addAssociatedPart(adapter2Regulator2Catalytic)
                .addAssociatedPart(camp)
                .build();

        ComplexedChemicalEntity adapter2Regulator2Catalytic2cAmp = ComplexedChemicalEntity.create("AKP:PKA:2cAMP")
                .addAssociatedPart(adapter2Regulator2Catalytic)
                .addAssociatedPart(camp, 2)
                .build();

        ComplexedChemicalEntity adapter2Regulator2Catalytic3cAmp = ComplexedChemicalEntity.create("AKP:PKA:3cAMP")
                .addAssociatedPart(adapter2Regulator2Catalytic)
                .addAssociatedPart(camp, 3)
                .build();

        ComplexedChemicalEntity adapter2Regulator2Catalytic4cAmp = ComplexedChemicalEntity.create("AKP:PKA:4cAMP")
                .addAssociatedPart(adapter2Regulator2Catalytic)
                .addAssociatedPart(camp, 4)
                .build();

        ComplexedChemicalEntity adapter2Regulator4cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:4cAMP")
                .addAssociatedPart(adapter2Regulator)
                .addAssociatedPart(camp, 4)
                .build();

        ComplexedChemicalEntity adapter2Regulator3cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:3cAMP")
                .addAssociatedPart(adapter2Regulator)
                .addAssociatedPart(camp, 3)
                .build();

        ComplexedChemicalEntity adapter2Regulator2cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:2cAMP")
                .addAssociatedPart(adapter2Regulator)
                .addAssociatedPart(camp, 2)
                .build();

        ComplexedChemicalEntity adapter2Regulator1cAmp = ComplexedChemicalEntity.create("AKP:2PKAR:1cAMP")
                .addAssociatedPart(adapter2Regulator)
                .addAssociatedPart(camp)
                .build();

        // from literature

        // association from c to r
        RateConstant crAssociation = RateConstant.create(5.11e3)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        // dissociation of c from r
        RateConstant crDissociation = RateConstant.create(1.23e-3)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // camp association when r is bound to c
        RateConstant campAssociation = RateConstant.create(2.0e5)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        // camp dissociation when r is bound to c, for binding site A
        RateConstant campADissociation = RateConstant.create(6.3e-2)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // camp dissociation when r is bound to c, for binding site B
        RateConstant campBDissociation = RateConstant.create(2.6e-6)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // inferred from model BIOMD0000000478

        // camp association when r is free of c
        // unsure
        RateConstant campRAssociation = RateConstant.create(1.0e-6)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        // camp dissociation when r is free of c, for binding site A
        // unsure
        RateConstant campRADissociation = RateConstant.create(0.5)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // camp dissociation when r is free of c, for binding site B
        // unsure
        RateConstant campRBDissociation = RateConstant.create(0.5e-2)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();

        // association from c to r
        RateConstant crCAssociation = RateConstant.create(1.0e-3)
                .forward().secondOrder()
                .concentrationUnit(MOLE_PER_LITRE)
                .timeUnit(SECOND)
                .build();

        // dissociation of c from r
        RateConstant crCDissociation = RateConstant.create(1.0e3)
                .backward().firstOrder()
                .timeUnit(SECOND)
                .build();


        // camp associations

        ComplexBuildingReaction association0to1 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("association0to1")
                .of(camp, campAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator2Catalytic, campADissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator2Catalytic1cAmp)
                .build();

        ComplexBuildingReaction association1to2 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("association1to2")
                .of(camp, campAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator2Catalytic1cAmp, campBDissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator2Catalytic2cAmp)
                .build();

        ComplexBuildingReaction association2to3 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("association2to3")
                .of(camp, campAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator2Catalytic2cAmp, campADissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator2Catalytic3cAmp)
                .build();

        ComplexBuildingReaction association3to4 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("association3to4")
                .of(camp, campAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator2Catalytic3cAmp, campBDissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator2Catalytic4cAmp)
                .build();

        // camp dissociations

        ComplexBuildingReaction dissociation1to0 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("association0to1")
                .of(camp, campRAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator, campRADissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator1cAmp)
                .build();

        ComplexBuildingReaction dissociation2to1 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("association1to2")
                .of(camp, campRAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator1cAmp, campRBDissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator2cAmp)
                .build();

        ComplexBuildingReaction dissociation3to2 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("association2to3")
                .of(camp, campRAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator2cAmp, campRADissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator3cAmp)
                .build();

        ComplexBuildingReaction dissociation4to3 = ComplexBuildingReaction.inSimulation(simulation)
                .identifier("association3to4")
                .of(camp, campRAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator3cAmp, campRBDissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator4cAmp)
                .build();

        // regulator catalytic association and dissociation with full camp

        ComplexBuildingReaction.inSimulation(simulation)
                .of(catalytic, crCAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator1Catalytic4cAmp, crCDissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator2Catalytic4cAmp)
                .build();

        ComplexBuildingReaction.inSimulation(simulation)
                .of(catalytic, crCAssociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator4cAmp, crCDissociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator1Catalytic4cAmp)
                .build();

        // regulator catalytic association and dissociation without camp

        ComplexBuildingReaction.inSimulation(simulation)
                .of(catalytic, crDissociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator, crAssociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator1Catalytic)
                .build();

        ComplexBuildingReaction.inSimulation(simulation)
                .of(catalytic, crDissociation)
                .in(CellTopology.INNER)
                .by(adapter2Regulator1Catalytic, crAssociation)
                .to(CellTopology.MEMBRANE)
                .formingComplex(adapter2Regulator2Catalytic)
                .build();



    }

}
