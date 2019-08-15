package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.entities.ComplexEntity;
import bio.singa.core.utility.Pair;
import bio.singa.features.parameters.Environment;
import bio.singa.features.quantities.MolarConcentration;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.features.*;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.surfacelike.MembraneSegment;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ConcentrationDelta;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.sections.CellTopology;
import bio.singa.simulation.model.sections.ConcentrationContainer;
import bio.singa.simulation.model.sections.ConcentrationPool;
import bio.singa.simulation.model.simulation.Updatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @author cl
 */
public class VesicleFusion extends QualitativeModule {

    private static final Logger logger = LoggerFactory.getLogger(VesicleFusion.class);

    private Map<Vesicle, Quantity<Time>> tetheredVesicles;
    private Map<Vesicle, AutomatonNode> tetheredNodes;

    private Map<Pair<ChemicalEntity>, ComplexEntity> complexes;

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
        getRequiredFeatures().add(FusionTime.class);
        getRequiredFeatures().add(AttachmentDistance.class);
        getRequiredFeatures().add(MatchingQSnares.class);
        getRequiredFeatures().add(MatchingRSnares.class);
        getRequiredFeatures().add(SNAREFusionPairs.class);
    }

    @Override
    public void initialize() {
        MatchingQSnares qSnares = getFeature(MatchingQSnares.class);
        MatchingRSnares rSnares = getFeature(MatchingRSnares.class);
        for (ChemicalEntity qSnare : qSnares.getContent()) {
            for (ChemicalEntity rSnare : rSnares.getContent()) {
                Pair<ChemicalEntity> pair = new Pair<>(qSnare, rSnare);
                complexes.put(pair, ComplexEntity.from(qSnare, rSnare));
            }
        }
    }

    public Map<Vesicle, Quantity<Time>> getTetheredVesicles() {
        return tetheredVesicles;
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
        logger.debug("Applying pending changes for {}.", this);
        // fuse vesicles
        for (Vesicle fusingVesicle : fusingVesicles) {
            fuse(fusingVesicle);
            tetheredVesicles.remove(fusingVesicle);
            simulation.getVesicleLayer().removeVesicle(fusingVesicle);
        }
        // tether vesicles
        for (Map.Entry<Vesicle, TetheringSnares> entry : tetheringVesicles.entrySet()) {
            tetherVesicle(entry.getKey(), entry.getValue());
        }
    }

    private void fuse(Vesicle tetheredVesicle) {
        // merge concentrations
        ConcentrationContainer vesicleContainer = tetheredVesicle.getConcentrationManager().getOriginalConcentrations();
        AutomatonNode node = tetheredNodes.get(tetheredVesicle);
        // merge membranes
        for (Map.Entry<ChemicalEntity, Double> entry : vesicleContainer.getPool(CellTopology.MEMBRANE).getValue().getConcentrations().entrySet()) {
            node.addPotentialDelta(new ConcentrationDelta(this, node.getCellRegion().getMembraneSubsection(), entry.getKey(), entry.getValue()));
        }
        // merge inner concentrations
        for (Map.Entry<ChemicalEntity, Double> entry : vesicleContainer.getPool(CellTopology.OUTER).getValue().getConcentrations().entrySet()) {
            node.addPotentialDelta(new ConcentrationDelta(this, node.getCellRegion().getInnerSubsection(), entry.getKey(), entry.getValue()));
        }
        // add occupied snares
        ConcentrationPool concentrationPool = occupiedSnares.get(tetheredVesicle);
        for (Map.Entry<ChemicalEntity, Double> entry : concentrationPool.getConcentrations().entrySet()) {
            node.addPotentialDelta(new ConcentrationDelta(this, node.getCellRegion().getMembraneSubsection(), entry.getKey(), entry.getValue()));
        }
    }

    private void tetherVesicle(Vesicle vesicle, TetheringSnares tetheringSnares) {
        // add tethering time to current time
        ComparableQuantity<Time> tetheringTime = simulation.getElapsedTime().add(getFeature(FusionTime.class).getContent());
        vesicle.setState(VesicleStateRegistry.MEMBRANE_TETHERED);
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
            if (vesicle.getState().equals(VesicleStateRegistry.ACTIN_PROPELLED) ||
                    vesicle.getState().equals(VesicleStateRegistry.MEMBRANE_TETHERED)) {
                continue;
            }
            Vector2D currentPosition = vesicle.getPosition();
            // for each associated node
            nodesLoop:
            for (Map.Entry<AutomatonNode, Double> entry : vesicle.getAssociatedNodes().entrySet()) {
                AutomatonNode node = entry.getKey();
                // if it contains a membrane
                if (node.getCellRegion().hasMembrane()) {
                    // get membrane segment
                    for (MembraneSegment membraneSegment : node.getMembraneSegments()) {
                        // check distance cutoff
                        double currentDistance = membraneSegment.distanceTo(currentPosition);
                        ComparableQuantity<Length> threshold = (ComparableQuantity<Length>) getFeature(AttachmentDistance.class).getContent().add(vesicle.getRadius());
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
        return new TetheringSnares(countSnares(vesicle, getFeature(MatchingRSnares.class).getContent()), countSnares(node, getFeature(MatchingQSnares.class).getContent()), node);
    }

    private Map<ChemicalEntity, Integer> countSnares(Updatable updatable, List<ChemicalEntity> entitiesToCount) {
        HashMap<ChemicalEntity, Integer> availableQSnares = new HashMap<>();
        for (ChemicalEntity snare : entitiesToCount) {
            double concentration = updatable.getConcentrationManager().getOriginalConcentrations().get(CellTopology.MEMBRANE, snare);
            int numberOfSnares = MolarConcentration.concentrationToMolecules(concentration).getValue().intValue();
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
        int fusionPairs = getFeature(SNAREFusionPairs.class).getContent().getValue().intValue();
        return rSnareSum >= fusionPairs && qSnareSum >= fusionPairs;
    }

    private void reserveSnares(Vesicle vesicle, TetheringSnares tetheringSnares) {
        int fusionPairs = getFeature(SNAREFusionPairs.class).getContent().getValue().intValue();
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
            ComplexEntity snareComplex = complexes.get(new Pair<>(qSnare, rSnare));
            reserveComplex(vesicle, snareComplex);
            // add deltas
            double concentration = MolarConcentration.moleculesToConcentration(-1.0);
            // rsnare in vesicle
            vesicle.addPotentialDelta(new ConcentrationDelta(this, vesicle.getCellRegion().getMembraneSubsection(), rSnare, concentration));
            // System.out.println("reserved during tethering " + rSnare + " " + MolarConcentration.concentrationToMolecules(concentration) + " snares");
            // qsnare in node
            AutomatonNode target = tetheringSnares.getTetheringTarget();
            target.addPotentialDelta(new ConcentrationDelta(this, target.getCellRegion().getMembraneSubsection(), qSnare, concentration));
            // System.out.println("reserved during tethering " + qSnare + " " + MolarConcentration.concentrationToMolecules(concentration) + " snares");
        }

    }

    private void reserveComplex(Vesicle vesicle, ComplexEntity snareComplex) {
        // reserve one snare
        double concentration = MolarConcentration.moleculesToConcentration(1.0);
        if (!occupiedSnares.containsKey(vesicle)) {
            occupiedSnares.put(vesicle, new ConcentrationPool());
        }
        // add complex to occupied concentration
        double addedQuantity = occupiedSnares.get(vesicle).get(snareComplex) + concentration;
        occupiedSnares.get(vesicle).set(snareComplex, addedQuantity);
        // System.out.println("reserved during tethering " + snareComplex + " " + MolarConcentration.concentrationToMolecules(concentration) + " snares");
    }

    private static class TetheringSnares {

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
