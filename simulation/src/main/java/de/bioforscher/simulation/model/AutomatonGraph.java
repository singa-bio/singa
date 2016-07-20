package de.bioforscher.simulation.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import static de.bioforscher.units.UnitDictionary.MOLE_PER_LITRE;

public class AutomatonGraph extends AbstractGraph<BioNode, BioEdge, Vector2D> {

    public AutomatonGraph() {

    }

    public AutomatonGraph(int nodeCapacity, int edgeCapacity) {
        super(nodeCapacity, edgeCapacity);
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

}
