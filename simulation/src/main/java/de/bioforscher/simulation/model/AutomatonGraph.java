package de.bioforscher.simulation.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.geometry.faces.Rectangle;
import de.bioforscher.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.mathematics.graphs.model.UndirectedEdge;
import de.bioforscher.mathematics.vectors.Vector2D;
import de.bioforscher.simulation.endocytosis.Compartment;
import de.bioforscher.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import java.util.HashSet;
import java.util.Set;

import static de.bioforscher.units.UnitProvider.MOLE_PER_LITRE;

public class AutomatonGraph extends AbstractGraph<BioNode, BioEdge, Vector2D> {

    private Set<Compartment> compartments;

    public AutomatonGraph() {
        this.compartments = new HashSet<>();
    }

    public AutomatonGraph(int nodeCapacity, int edgeCapacity) {
        super(nodeCapacity, edgeCapacity);
        this.compartments = new HashSet<>();
    }

    @Override
    public int addEdgeBetween(int identifier, BioNode source, BioNode target) {
        return addEdgeBetween(new BioEdge(identifier), source, target);
    }

    @Override
    public int addEdgeBetween(BioNode source, BioNode target) {
        return addEdgeBetween(nextEdgeIdentifier(), source, target);
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

    public void addCompartment(Rectangle rectangle) {
        Set<BioNode> compartmentContent = new HashSet<>();
        Rectangle r = new Rectangle(rectangle.getTopRightVertex(), rectangle.getBottomLeftVertex());
        this.getNodes().forEach(node -> {
            if (node.getPosition().canBePlacedIn(r)) {
                compartmentContent.add(node);
                node.setContainingCompartment("not default");
            }
        });
        this.compartments.add(new Compartment("not default", compartmentContent));
    }



}
