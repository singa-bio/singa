package de.bioforscher.singa.simulation.modules.membranetransport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.updates.CumulativeUpdateBehavior;
import de.bioforscher.singa.simulation.modules.model.updates.PotentialUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.units.ri.quantity.Quantities;

import java.util.*;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

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

    private PotentialUpdate calculateCompartmentSpecificUpdate(BioNode node, CellSection cellSection, ChemicalEntity entity) {
        // this method is only called for membrane nodes
        // determine scaling factor for membrane association
        // final double membraneFactor = 1.0/Math.pow(10, getOctanolWaterCoefficient(entity));

        // three step process
        // membrane entry (outer phase -> outer layer and inner phase -> inner layer) - kIn
        // flip-flip across membrane (outer layer <-> inner layer) - kFlip
        // membrane exit (outer layer -> outer phase and inner layer -> inner phase) - kOut

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

        return new PotentialUpdate(node, cellSection, entity, Quantities.getQuantity(0.0, MOLE_PER_LITRE));
    }

    @Override
    public List<PotentialUpdate> calculateUpdates(BioNode node) {
        return Collections.emptyList();
    }

}
