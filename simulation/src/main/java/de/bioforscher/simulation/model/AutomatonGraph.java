package de.bioforscher.simulation.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.units.UnitProvider.MOLE_PER_LITRE;

public class AutomatonGraph extends AbstractGraph<BioNode, BioEdge, Vector2D> {

    public AutomatonGraph() {

    }

    public AutomatonGraph(int nodeCapacity, int edgeCapacity) {
        super(nodeCapacity, edgeCapacity);
    }

    @Override
    public void addEdgeBetween(BioNode source, BioNode target) {

    }

    public void connect(int identifier, BioNode source, BioNode target) {
        super.connect(identifier, source, target, BioEdge.class);
    }

    public void initializeSpeciesWithConcentration(ChemicalEntity entity, double concentration) {
        initializeSpeciesWithConcentration(entity, Quantities.getQuantity(concentration,
                MOLE_PER_LITRE));
    }

    public void initializeSpeciesWithConcentration(ChemicalEntity entity, Quantity<MolarConcentration> concentration) {
        this.getNodes().forEach(node -> {
            node.setConcentration(entity, concentration);
        });
        this.getEdges().forEach(edge -> {
            edge.addPermeability(entity, 1.0);
        });
    }

    public Quantity<MolarConcentration> getSteepestDifference(ChemicalEntity entity) {
        return Quantities.getQuantity(this.getNodes().stream()
                   .mapToDouble(node -> node.getSteepestConcentrationDifference(entity))
                   .max().orElse(0.0), MOLE_PER_LITRE);
    }



}
