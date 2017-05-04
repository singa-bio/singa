package de.bioforscher.singa.simulation.modules.membranetransport;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.chemistry.descriptive.estimations.OctanolWaterPartition;
import de.bioforscher.singa.chemistry.descriptive.molecules.MoleculeGraph;
import de.bioforscher.singa.chemistry.parser.smiles.SmilesParser;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.NodeState;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.updates.CumulativeUpdateBehavior;
import de.bioforscher.singa.simulation.modules.model.updates.PotentialUpdate;
import tec.units.ri.quantity.Quantities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.bioforscher.singa.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class PassiveMembraneTransport implements Module, CumulativeUpdateBehavior {

    private Map<ChemicalEntity<?>, Double> octanolWaterCoefficients;

    public PassiveMembraneTransport() {
        this.octanolWaterCoefficients = new HashMap<>();
    }

    private void prepareOctanolWaterCoefficients(Set<ChemicalEntity<?>> entities) {
        for (ChemicalEntity entity : entities) {
            // determine octanol water partition coefficient
            MoleculeGraph moleculeGraph = SmilesParser.parse(((Species) entity).getSmilesRepresentation());
            double octanolWaterCoefficient = OctanolWaterPartition.calculateOctanolWaterPartitionCoefficient(moleculeGraph, OctanolWaterPartition.Method.NC_NHET);
            this.octanolWaterCoefficients.put(entity, octanolWaterCoefficient);
        }
    }

    @Override
    public void applyTo(AutomatonGraph graph) {
        updateGraph(graph);
    }

    @Override
    public void updateGraph(AutomatonGraph graph) {
        // collect updates but this is only relevant in membrane nodes
        List<PotentialUpdate> updates = graph.getNodes().stream()
                .filter(node -> node.getState() == NodeState.MEMBRANE)
                .flatMap(node -> calculateUpdates(node).stream())
                .collect(Collectors.toList());
        // apply updates
        updates.forEach(PotentialUpdate::apply);
    }

    /**
     * Determines the Octanol.Water coefficient if it is not already cached.
     *
     * @param entity The entity.
     * @return The Octanol-Water Coefficient
     */
    private double getOctanolWaterCoefficient(ChemicalEntity entity) {
        if (this.octanolWaterCoefficients.containsKey(entity)) {
            return this.octanolWaterCoefficients.get(entity);
        } else {
            MoleculeGraph moleculeGraph = SmilesParser.parse(((Species) entity).getSmilesRepresentation());
            double octanolWaterCoefficient = OctanolWaterPartition.calculateOctanolWaterPartitionCoefficient(moleculeGraph, OctanolWaterPartition.Method.NC_NHET);
            this.octanolWaterCoefficients.put(entity, octanolWaterCoefficient);
            return octanolWaterCoefficient;
        }
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.octanolWaterCoefficients.keySet();
    }

    private PotentialUpdate calculateCompartmentSpecificUpdate(BioNode node, CellSection cellSection, ChemicalEntity entity) {
        // this is the classical implementation
        // this method is only called for membrane nodes
        // determine scaling factor for membrane association
        final double membraneFactor = 1.0/Math.pow(10, getOctanolWaterCoefficient(entity));
        // TODO split membrane compartment in two compartments (outer and inner)

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
        return null;
    }

}
