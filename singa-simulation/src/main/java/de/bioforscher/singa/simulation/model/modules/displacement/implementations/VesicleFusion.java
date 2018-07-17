package de.bioforscher.singa.simulation.model.modules.displacement.implementations;

import de.bioforscher.singa.chemistry.entities.ChemicalEntity;
import de.bioforscher.singa.chemistry.entities.ComplexedChemicalEntity;
import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.features.endocytosis.AttachmentDistance;
import de.bioforscher.singa.simulation.features.endocytosis.TetheringTime;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.modules.concentration.ModuleState;
import de.bioforscher.singa.simulation.model.modules.displacement.DisplacementBasedModule;
import de.bioforscher.singa.simulation.model.modules.displacement.Vesicle;
import de.bioforscher.singa.simulation.model.modules.macroscopic.membranes.MacroscopicMembraneSegment;
import de.bioforscher.singa.simulation.model.sections.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.sections.ConcentrationPool;
import de.bioforscher.singa.simulation.model.simulation.Updatable;
import tec.uom.se.ComparableQuantity;

import javax.measure.Quantity;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static de.bioforscher.singa.simulation.model.modules.displacement.Vesicle.AttachmentState.TETHERED;
import static de.bioforscher.singa.simulation.model.sections.CellTopology.INNER;
import static de.bioforscher.singa.simulation.model.sections.CellTopology.MEMBRANE;

/**
 * @author cl
 */
public class VesicleFusion extends DisplacementBasedModule {

    private HashMap<Vesicle, Quantity<Time>> tetheredVesicles;
    private HashMap<Vesicle, AutomatonNode> tetheredNodes;

    private Set<ChemicalEntity> rSnares;
    private Set<ChemicalEntity> qSnares;
    private HashMap<Pair<ChemicalEntity>, ComplexedChemicalEntity> complexes;

    private HashMap<Updatable, ConcentrationPool> occupiedSnares;

    private int minimalPairs;

    public VesicleFusion() {
        // TODO this is currently not compatible with recalculations
        // TODO changes should be moved to deltas
        // initialize
        tetheredVesicles = new HashMap<>();
        tetheredNodes = new HashMap<>();
        rSnares = new HashSet<>();
        qSnares = new HashSet<>();
        complexes = new HashMap<>();
        occupiedSnares = new HashMap<>();
        // feature
        getRequiredFeatures().add(TetheringTime.class);
        getRequiredFeatures().add(AttachmentDistance.class);
    }

    public void addMatchingRSnare(ChemicalEntity entity) {
        rSnares.add(entity);
    }

    public void addMatchingQSnare(ChemicalEntity entity) {
        qSnares.add(entity);
    }

    public int getMinimalPairs() {
        return minimalPairs;
    }

    public void setMinimalPairs(int minimalPairs) {
        this.minimalPairs = minimalPairs;
    }

    public HashMap<Vesicle, Quantity<Time>> getTetheredVesicles() {
        return tetheredVesicles;
    }

    public void initializeComplexes() {
        for (ChemicalEntity qSnare : qSnares) {
            for (ChemicalEntity rSnare : rSnares) {
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
        updateTetheringTimes();
        tetherVesicles();
        state = ModuleState.SUCCEEDED;
    }

    private void updateTetheringTimes() {
        Set<Vesicle> fusedVesicles = new HashSet<>();
        for (Map.Entry<Vesicle, Quantity<Time>> entry : tetheredVesicles.entrySet()) {
            Vesicle tetheredVesicle = entry.getKey();
            Quantity<Time> tetheredTime = entry.getValue();
            ComparableQuantity<Time> totalTime = getFeature(TetheringTime.class).getFeatureContent();
            // if tethered time is reached
            if (totalTime.isLessThan(tetheredTime)) {
                // add vesicle to vesicle layer
                fuse(tetheredVesicle);
                fusedVesicles.add(tetheredVesicle);
            } else {
                // increase Maturation time
                tetheredVesicles.put(tetheredVesicle, tetheredTime.add(Environment.getTimeStep()));
            }
        }

        for (Vesicle fusedVesicle : fusedVesicles) {
            tetheredVesicles.remove(fusedVesicle);
        }

    }

    private void fuse(Vesicle tetheredVesicle) {
        // merge concentrations
        ConcentrationContainer vesicleContainer = tetheredVesicle.getConcentrationContainer();
        ConcentrationContainer nodeContainer = tetheredNodes.get(tetheredVesicle).getConcentrationContainer();
        // merge membranes
        for (Map.Entry<ChemicalEntity, Quantity<MolarConcentration>> entry : vesicleContainer.getPool(MEMBRANE).getValue().getConcentrations()) {
            ChemicalEntity entity = entry.getKey();
            nodeContainer.set(MEMBRANE, entity, entry.getValue().add(nodeContainer.get(MEMBRANE, entity)));
        }
        // merge inner concentrations
        for (Map.Entry<ChemicalEntity, Quantity<MolarConcentration>> entry : vesicleContainer.getPool(INNER).getValue().getConcentrations()) {
            ChemicalEntity entity = entry.getKey();
            nodeContainer.set(INNER, entity, entry.getValue().add(nodeContainer.get(INNER, entity)));
        }
        // add occupied snares
        ConcentrationPool concentrationPool = occupiedSnares.get(tetheredVesicle);
        for (Map.Entry<ChemicalEntity, Quantity<MolarConcentration>> entry : concentrationPool.getConcentrations()) {
            ChemicalEntity entity = entry.getKey();
            nodeContainer.set(MEMBRANE, entity, entry.getValue().add(nodeContainer.get(MEMBRANE, entity)));
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
                    for (MacroscopicMembraneSegment membraneSegment : node.getMembraneSegments()) {
                        // get line representations
                        for (LineSegment lineSegment : membraneSegment.getLineSegments()) {
                            // TODO implement distance checking in general module (similar approach in VesicleAttachment)
                            // check distance cutoff
                            double currentDistance = lineSegment.distanceTo(currentPosition);
                            ComparableQuantity<Length> threshold = getFeature(AttachmentDistance.class).getFeatureContent().add(vesicle.getRadius());
                            Quantity<Length> distance = Environment.convertSimulationToSystemScale(currentDistance);
                            if (threshold.isGreaterThanOrEqualTo(distance)) {
                                if (snaresMatch(node, vesicle)) {
                                    vesicle.setAttachmentState(TETHERED);
                                    tetheredVesicles.put(vesicle, Environment.getTimeStep());
                                    tetheredNodes.put(vesicle, node);
                                    break nodesLoop;
                                }
                            }
                        }
                    }
                }
            }
        }
        // remove now tethered vesicles
        // FIXME this always removes all vesicles that are tethered
        for (Vesicle vesicle : tetheredVesicles.keySet()) {
            simulation.getVesicleLayer().removeVesicle(vesicle);
        }
    }

    private boolean snaresMatch(AutomatonNode node, Vesicle vesicle) {
        HashMap<ChemicalEntity, Integer> rSnares = countRSnares(vesicle);
        int rSnareSum = 0;
        for (Integer rSnareNumber : rSnares.values()) {
            rSnareSum += rSnareNumber;
        }
        HashMap<ChemicalEntity, Integer> qSnares = countQSnares(node);
        int qSnareSum = 0;
        for (Integer qSnareNumber : qSnares.values()) {
            qSnareSum += qSnareNumber;
        }
        if (rSnareSum >= minimalPairs && qSnareSum >= minimalPairs) {
            reserveSnares(vesicle, rSnares, qSnares);
            return true;
        }
        return false;
    }

    private HashMap<ChemicalEntity, Integer> countQSnares(AutomatonNode node) {
        HashMap<ChemicalEntity, Integer> availableQSnares = new HashMap<>();
        for (ChemicalEntity qSnare : qSnares) {
            Quantity<MolarConcentration> quantity = node.getConcentrationContainer().get(MEMBRANE, qSnare);
            int numberOfSnares = MolarConcentration.concentrationToMolecules(quantity, Environment.getSubsectionVolume()).getValue().intValue();
            if (numberOfSnares > 0) {
                availableQSnares.put(qSnare, numberOfSnares);
            }
        }
        return availableQSnares;
    }


    private HashMap<ChemicalEntity, Integer> countRSnares(Vesicle vesicle) {
        HashMap<ChemicalEntity, Integer> availableRSnares = new HashMap<>();
        for (ChemicalEntity rSnare : rSnares) {
            Quantity<MolarConcentration> quantity = vesicle.getConcentrationContainer().get(MEMBRANE, rSnare);
            int numberOfSnares = MolarConcentration.concentrationToMolecules(quantity, Environment.getSubsectionVolume()).getValue().intValue();
            if (numberOfSnares > 0) {
                availableRSnares.put(rSnare, numberOfSnares);
            }
        }
        return availableRSnares;
    }

    private void reserveSnares(Updatable updatable, HashMap<ChemicalEntity, Integer> rSnares, HashMap<ChemicalEntity, Integer> qSnares) {
        List<ChemicalEntity> qSnareEntities = new ArrayList<>(qSnares.keySet());
        List<ChemicalEntity> rSnareEntities = new ArrayList<>(rSnares.keySet());
        for (int occupiedSnareCounter = 0; occupiedSnareCounter < minimalPairs; occupiedSnareCounter++) {
            // choose qSnare at random
            int qSnareIndex = ThreadLocalRandom.current().nextInt(qSnareEntities.size());
            ChemicalEntity qSnare = qSnareEntities.get(qSnareIndex);
            // choose rSnare at random
            int rSnareIndex = ThreadLocalRandom.current().nextInt(rSnareEntities.size());
            ChemicalEntity rSnare = rSnareEntities.get(rSnareIndex);
            // reserve complex
            ComplexedChemicalEntity snareComplex = complexes.get(new Pair<>(qSnare, rSnare));
            reserveSnare(updatable, snareComplex);
        }
    }

    private void reserveSnare(Updatable updatable, ComplexedChemicalEntity snareComplex) {
        // reserve one snare
        Quantity<MolarConcentration> concentrationQuantity = MolarConcentration.moleculesToConcentration(1.0, Environment.getSubsectionVolume());
        if (!occupiedSnares.containsKey(updatable)) {
            occupiedSnares.put(updatable, new ConcentrationPool());
        }
        // add to occupied concentration
        Quantity<MolarConcentration> addedQuantity = occupiedSnares.get(updatable).get(snareComplex).add(concentrationQuantity);
        occupiedSnares.get(updatable).set(snareComplex, addedQuantity);
        // remove from available concentration
        // FIXME this is basically a reaction
        for (ChemicalEntity entity : snareComplex.getAssociatedChemicalEntities()) {
            Quantity<MolarConcentration> vesicleConcentration = updatable.getConcentrationContainer().get(MEMBRANE, entity);
            if (vesicleConcentration.getValue().doubleValue() > 0.0) {
                Quantity<MolarConcentration> reducedQuantity = vesicleConcentration.subtract(concentrationQuantity);
                updatable.getConcentrationContainer().set(MEMBRANE, entity, reducedQuantity);
            }
        }
    }

}
