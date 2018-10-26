package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexedChemicalEntity;
import bio.singa.core.utility.Pair;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.membranes.MembraneSegment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.displacement.Vesicle;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.sections.ConcentrationPool;
import bio.singa.simulation.model.simulation.Updatable;
import tec.uom.se.ComparableQuantity;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static bio.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.TETHERED;

/**
 * @author cl
 */
public class VesicleFusion extends QualitativeModule {

    private Map<Vesicle, Quantity<Time>> tetheredVesicles;
    private Map<Vesicle, AutomatonNode> tetheredNodes;

    private Map<Pair<ChemicalEntity>, ComplexedChemicalEntity> complexes;

    private Map<Updatable, ConcentrationPool> occupiedSnares;

    // temporary variables for #onCompletion
    private List<Vesicle> fusingVesicles;
    private Map<Vesicle, TetheringSnares> tetheringVesicles;

    public VesicleFusion() {
        // initialize
        tetheredVesicles = new HashMap<>();
        tetheredNodes = new HashMap<>();
        complexes = new HashMap<>();
        occupiedSnares = new HashMap<>();
        fusingVesicles = new ArrayList<>();
        tetheringVesicles = new HashMap<>();
        // feature
        getRequiredFeatures().add(TetheringTime.class);
        getRequiredFeatures().add(AttachmentDistance.class);
        getRequiredFeatures().add(MatchingQSnares.class);
        getRequiredFeatures().add(MatchingRSnares.class);
        getRequiredFeatures().add(FusionPairs.class);
    }

    public Map<Vesicle, Quantity<Time>> getTetheredVesicles() {
        return tetheredVesicles;
    }

    public void initializeComplexes() {
        MatchingQSnares qSnares = getFeature(MatchingQSnares.class);
        MatchingRSnares rSnares = getFeature(MatchingRSnares.class);
        for (ChemicalEntity qSnare : qSnares.getFeatureContent()) {
            for (ChemicalEntity rSnare : rSnares.getFeatureContent()) {
                Pair<ChemicalEntity> pair = new Pair<>(qSnare, rSnare);
                ComplexedChemicalEntity complex = ComplexedChemicalEntity.create(qSnare.getIdentifier().getIdentifier() + ":" + rSnare.getIdentifier().getIdentifier())
                        .addAssociatedPart(qSnare)
                        .addAssociatedPart(rSnare)
                        .build();
                complexes.put(pair, complex);
            }
        }
    }

    @Override
    public void calculateUpdates() {
        checkTetheringTime();
        tetherVesicles();
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    @Override
    public void optimizeTimeStep() {
        // nothing to do
    }

    @Override
    public void onReset() {
        fusingVesicles.clear();
        tetheringVesicles.clear();
    }

    @Override
    public void onCompletion() {
        // fuse vesicles
        for (Vesicle fusingVesicle : fusingVesicles) {
            fuse(fusingVesicle);
            tetheredVesicles.remove(fusingVesicle);
        }
        // tether vesicles
        for (Map.Entry<Vesicle, TetheringSnares> entry : tetheringVesicles.entrySet()) {
            tetherVesicle(entry.getKey(), entry.getValue());
            simulation.getVesicleLayer().removeVesicle(entry.getKey());
        }
    }

    private void fuse(Vesicle tetheredVesicle) {
        // merge concentrations
        ConcentrationContainer vesicleContainer = tetheredVesicle.getConcentrationContainer();
        AutomatonNode node = tetheredNodes.get(tetheredVesicle);
        // merge membranes
        for (Map.Entry<ChemicalEntity, Quantity<MolarConcentration>> entry : vesicleContainer.getPool(CellTopology.MEMBRANE).getValue().getConcentrations()) {
            ChemicalEntity entity = entry.getKey();
            Quantity<MolarConcentration> quantity = entry.getValue();
            node.addPotentialDelta(new ConcentrationDelta(this, node.getCellRegion().getMembraneSubsection(), entity, quantity));
        }
        // merge inner concentrations
        for (Map.Entry<ChemicalEntity, Quantity<MolarConcentration>> entry : vesicleContainer.getPool(CellTopology.INNER).getValue().getConcentrations()) {
            ChemicalEntity entity = entry.getKey();
            Quantity<MolarConcentration> quantity = entry.getValue();
            node.addPotentialDelta(new ConcentrationDelta(this, node.getCellRegion().getInnerSubsection(), entity, quantity));
        }
        // add occupied snares
        ConcentrationPool concentrationPool = occupiedSnares.get(tetheredVesicle);
        for (Map.Entry<ChemicalEntity, Quantity<MolarConcentration>> entry : concentrationPool.getConcentrations()) {
            ChemicalEntity entity = entry.getKey();
            Quantity<MolarConcentration> quantity = entry.getValue();
            node.addPotentialDelta(new ConcentrationDelta(this, node.getCellRegion().getMembraneSubsection(), entity, quantity));
        }
    }

    private void tetherVesicle(Vesicle vesicle, TetheringSnares tetheringSnares) {
        // add tethering time to current time
        ComparableQuantity<Time> tetheringTime = simulation.getElapsedTime().add(getFeature(TetheringTime.class).getFeatureContent());
        vesicle.setAttachmentState(TETHERED);
        // set time
        tetheredVesicles.put(vesicle, tetheringTime);
        // set target
        tetheredNodes.put(vesicle, tetheringSnares.getTetheringTarget());
        // set reserved snares
        reserveSnares(vesicle, tetheringSnares);
    }


    private void checkTetheringTime() {
        for (Map.Entry<Vesicle, Quantity<Time>> entry : tetheredVesicles.entrySet()) {
            Vesicle tetheredVesicle = entry.getKey();
            Quantity<Time> fusionTime = entry.getValue();
            // if tethered time is reached
            if (simulation.getElapsedTime().isGreaterThanOrEqualTo(fusionTime)) {
                // add vesicle to vesicle layer
                fusingVesicles.add(tetheredVesicle);
            }
        }
    }

    private void tetherVesicles() {
        List<Vesicle> vesicles = simulation.getVesicleLayer().getVesicles();
        // for each vesicle
        for (Vesicle vesicle : vesicles) {
            if (vesicle.getAttachmentState() == Vesicle.AttachmentState.ACTIN_DEPOLYMERIZATION ||
                    vesicle.getAttachmentState() == Vesicle.AttachmentState.TETHERED) {
                continue;
            }
            Vector2D currentPosition = vesicle.getCurrentPosition();
            // for each associated node
            nodesLoop:
            for (Map.Entry<AutomatonNode, Quantity<Area>> entry : vesicle.getAssociatedNodes().entrySet()) {
                AutomatonNode node = entry.getKey();
                // if it contains a membrane
                if (node.getCellRegion().hasMembrane()) {
                    // get membrane segment
                    for (MembraneSegment membraneSegment : node.getMembraneSegments()) {
                        // check distance cutoff
                        double currentDistance = membraneSegment.distanceTo(currentPosition);
                        ComparableQuantity<Length> threshold = (ComparableQuantity<Length>) getFeature(AttachmentDistance.class).getFeatureContent().add(vesicle.getRadius());
                        Quantity<Length> distance = Environment.convertSimulationToSystemScale(currentDistance);
                        if (threshold.isGreaterThanOrEqualTo(distance)) {
                            TetheringSnares tetheringSnares = prepareTethering(node, vesicle);
                            if (snaresMatch(tetheringSnares)) {
                                tetheringVesicles.put(vesicle, tetheringSnares);
                                break nodesLoop;
                            }
                        }
                    }
                }
            }
        }
    }

    private TetheringSnares prepareTethering(AutomatonNode node, Vesicle vesicle) {
        return new TetheringSnares(countSnares(vesicle, getFeature(MatchingRSnares.class).getFeatureContent()), countSnares(node, getFeature(MatchingQSnares.class).getFeatureContent()), node);
    }

    private Map<ChemicalEntity, Integer> countSnares(Updatable updatable, Set<ChemicalEntity> entitiesToCount) {
        HashMap<ChemicalEntity, Integer> availableQSnares = new HashMap<>();
        for (ChemicalEntity snare : entitiesToCount) {
            Quantity<MolarConcentration> quantity = updatable.getConcentrationContainer().get(CellTopology.MEMBRANE, snare);
            int numberOfSnares = MolarConcentration.concentrationToMolecules(quantity, UnitRegistry.getVolume()).getValue().intValue();
            if (numberOfSnares > 0) {
                availableQSnares.put(snare, numberOfSnares);
            }
        }
        return availableQSnares;
    }

    private boolean snaresMatch(TetheringSnares snarePattern) {
        int rSnareSum = 0;
        for (Integer rSnareNumber : snarePattern.getRSnares().values()) {
            rSnareSum += rSnareNumber;
        }
        int qSnareSum = 0;
        for (Integer qSnareNumber : snarePattern.getQSnares().values()) {
            qSnareSum += qSnareNumber;
        }
        int fusionPairs = getFeature(FusionPairs.class).getFeatureContent().getValue().intValue();
        return rSnareSum >= fusionPairs && qSnareSum >= fusionPairs;
    }

    private void reserveSnares(Vesicle vesicle, TetheringSnares tetheringSnares) {
        int fusionPairs = getFeature(FusionPairs.class).getFeatureContent().getValue().intValue();
        List<ChemicalEntity> qSnareEntities = new ArrayList<>(tetheringSnares.getQSnares().keySet());
        List<ChemicalEntity> rSnareEntities = new ArrayList<>(tetheringSnares.getRSnares().keySet());
        for (int occupiedSnareCounter = 0; occupiedSnareCounter < fusionPairs; occupiedSnareCounter++) {
            // choose qSnare at random
            int qSnareIndex = ThreadLocalRandom.current().nextInt(qSnareEntities.size());
            ChemicalEntity qSnare = qSnareEntities.get(qSnareIndex);
            // choose rSnare at random
            int rSnareIndex = ThreadLocalRandom.current().nextInt(rSnareEntities.size());
            ChemicalEntity rSnare = rSnareEntities.get(rSnareIndex);
            // reserve complex
            ComplexedChemicalEntity snareComplex = complexes.get(new Pair<>(qSnare, rSnare));
            reserveComplex(vesicle, snareComplex);
            // add deltas
            Quantity<MolarConcentration> concentrationQuantity = MolarConcentration.moleculesToConcentration(-1.0, UnitRegistry.getVolume());
            // rsnare in vesicle
            vesicle.addPotentialDelta(new ConcentrationDelta(this, vesicle.getCellRegion().getMembraneSubsection(), rSnare, concentrationQuantity));
            // qsnare in node
            AutomatonNode target = tetheringSnares.getTetheringTarget();
            target.addPotentialDelta(new ConcentrationDelta(this, target.getCellRegion().getMembraneSubsection(), qSnare, concentrationQuantity));
        }

    }

    private void reserveComplex(Vesicle vesicle, ComplexedChemicalEntity snareComplex) {
        // reserve one snare
        Quantity<MolarConcentration> concentrationQuantity = MolarConcentration.moleculesToConcentration(1.0, UnitRegistry.getVolume());
        if (!occupiedSnares.containsKey(vesicle)) {
            occupiedSnares.put(vesicle, new ConcentrationPool());
        }
        // add complex to occupied concentration
        Quantity<MolarConcentration> addedQuantity = occupiedSnares.get(vesicle).get(snareComplex).add(concentrationQuantity);
        occupiedSnares.get(vesicle).set(snareComplex, addedQuantity);
    }

    private class TetheringSnares {

        private Map<ChemicalEntity, Integer> rSnares;
        private Map<ChemicalEntity, Integer> qSnares;
        private AutomatonNode tetheringTarget;

        TetheringSnares(Map<ChemicalEntity, Integer> rSnares, Map<ChemicalEntity, Integer> qSnares, AutomatonNode tetheringTarget) {
            this.rSnares = rSnares;
            this.qSnares = qSnares;
            this.tetheringTarget = tetheringTarget;
        }

        Map<ChemicalEntity, Integer> getRSnares() {
            return rSnares;
        }

        Map<ChemicalEntity, Integer> getQSnares() {
            return qSnares;
        }

        AutomatonNode getTetheringTarget() {
            return tetheringTarget;
        }
    }


}
