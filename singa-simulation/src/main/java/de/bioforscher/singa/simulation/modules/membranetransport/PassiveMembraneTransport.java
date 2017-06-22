package de.bioforscher.singa.simulation.modules.membranetransport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.features.permeability.MembraneEntry;
import de.bioforscher.singa.simulation.features.permeability.MembraneExit;
import de.bioforscher.singa.simulation.features.permeability.MembraneFlipFlop;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.graphs.MembraneContainer;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.updates.CumulativeUpdateBehavior;
import de.bioforscher.singa.simulation.modules.model.updates.PotentialUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 */
public class PassiveMembraneTransport implements Module, CumulativeUpdateBehavior {

    private static final Logger logger = LoggerFactory.getLogger(PassiveMembraneTransport.class);

    private Set<ChemicalEntity<?>> chemicalEntities;

    public PassiveMembraneTransport() {
        this.chemicalEntities = new HashSet<>();
    }

    public void prepareCoefficients(Set<ChemicalEntity<?>> entities) {
        this.chemicalEntities = entities;
        for (ChemicalEntity entity : entities) {
            // Diffusivity feature = entity.getFeature(Diffusivity.class);
            // feature.scale(EnvironmentalParameters.getInstance().getTimeStep(),
            //         EnvironmentalParameters.getInstance().getNodeDistance());
        }
    }

    @Override
    public void applyTo(AutomatonGraph graph) {
        logger.debug("Applying Passive Membrane Transport module.");
        updateGraph(graph);
    }

    @Override
    public void updateGraph(AutomatonGraph graph) {
        // collect updates but this is only relevant in membrane nodes
        List<PotentialUpdate> updates = new ArrayList<>();
        for (BioNode node : graph.getNodes()) {
            if (node.getState() == NodeState.MEMBRANE) {
                updates.addAll(calculateUpdates(node));
            }
        }
        // apply updates
        updates.forEach(PotentialUpdate::apply);
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.chemicalEntities;
    }

    private List<PotentialUpdate> calculateCompartmentSpecificUpdate(BioNode node, CellSection cellSection, ChemicalEntity<?> entity) {

        setUpParameters(entity);

        MembraneContainer concentrations = (MembraneContainer) node.getConcentrations();
        double outerPhase = concentrations.getOuterPhaseConcentration(entity).getValue().doubleValue();
        double outerLayer = concentrations.getOuterMembraneLayerConcentration(entity).getValue().doubleValue();
        double innerLayer = concentrations.getInnerMembraneLayerConcentration(entity).getValue().doubleValue();
        double innerPhase = concentrations.getInnerPhaseConcentration(entity).getValue().doubleValue();

        // membrane entry (outer phase -> outer layer and inner phase -> inner layer) - kIn
        double kIn = entity.getFeature(MembraneEntry.class).getScaledQuantity().getValue().doubleValue();
        // membrane exit (outer layer -> outer phase and inner layer -> inner phase) - kOut
        double kOut = entity.getFeature(MembraneExit.class).getScaledQuantity().getValue().doubleValue();
        // flip-flip across membrane (outer layer <-> inner layer) - kFlip
        double kFlip = entity.getFeature(MembraneFlipFlop.class).getScaledQuantity().getValue().doubleValue();

        // four concentrations (ao, lo, li, ai) have to be set
        // (outer phase) ao = -kIn * ao + kOut * lo
        // (outer layer) lo = kIn * ao - (kOut + kFlip) * lo + kFlip * li
        // (inner layer) li = kIn * ai - (kOut + kFlip) * li + kFlip * lo
        // (inner phase) ai = -kIn * ai + kOut * li

        double newOuterPhase = -kIn * outerPhase + kOut * outerLayer;
        double newOuterLayer = kIn * outerPhase - (kOut + kFlip) * outerLayer + kFlip * innerLayer;
        double newInnerLayer = kIn * innerPhase - (kOut + kFlip) * innerLayer + kFlip * outerLayer;
        double newInnerPhase = -kIn * innerPhase + kOut * innerLayer;

        List<PotentialUpdate> updates = new ArrayList<>();
        // updates.add(new PotentialUpdate(node, concentrations.get, entity, Quantities.getQuantity(0.0, MOLE_PER_LITRE));


        // for highly polar molecules kIn and kFlip determine overall permeation speed which is slow

        // as lipophilicity increases kIn becomes high and and kFlip and kOut are roughly equivalent
        // leading to a optimal permeation

        // at high lipophilicity kOut becomes low and the compound tends to reside in the membrane

        // 



        // (l)-(c)-(r)
        // D*sum(c(Neighbour)) - D*#neighbours*c(this)
        // M*sum(c(NM-Neighbor))
        for (BioNode neighbour : node.getNeighbours()) {
            //if ()
        }

        return null;
    }

    @Override
    public List<PotentialUpdate> calculateUpdates(BioNode node) {
        return Collections.emptyList();
    }

    /**
     * Determines the diffusivity of the entity and scales it to the dimensions of the system.
     *
     * @param entity The entity.
     * @return The diffusivity of the entity.
     */
    private void setUpParameters(ChemicalEntity<?> entity) {
        if (!this.chemicalEntities.contains(entity)) {
            // entry
            MembraneEntry membraneEntry = entity.getFeature(MembraneEntry.class);
            membraneEntry.scale(EnvironmentalParameters.getInstance().getTimeStep());
            // exit
            MembraneExit membraneExit = entity.getFeature(MembraneExit.class);
            membraneExit.scale(EnvironmentalParameters.getInstance().getTimeStep());
            // flip flop
            MembraneFlipFlop membraneFlipFlop = entity.getFeature(MembraneFlipFlop.class);
            membraneFlipFlop.scale(EnvironmentalParameters.getInstance().getTimeStep());
            // add to set
            this.chemicalEntities.add(entity);
        }

    }
}
