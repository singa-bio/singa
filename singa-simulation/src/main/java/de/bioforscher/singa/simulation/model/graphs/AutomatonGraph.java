package de.bioforscher.singa.simulation.model.graphs;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.mathematics.geometry.faces.Rectangle;
import de.bioforscher.singa.mathematics.graphs.model.AbstractGraph;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.compartments.EnclosedCompartment;
import de.bioforscher.singa.simulation.model.compartments.Membrane;
import de.bioforscher.singa.units.quantities.MolarConcentration;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static de.bioforscher.singa.units.UnitProvider.MOLE_PER_LITRE;

public class AutomatonGraph extends AbstractGraph<BioNode, BioEdge, Vector2D> {

    private Map<String, CellSection> sections;

    public AutomatonGraph() {
        this.sections = new HashMap<>();
    }

    public AutomatonGraph(int nodeCapacity, int edgeCapacity) {
        super(nodeCapacity, edgeCapacity);
        this.sections = new HashMap<>();
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
        this.getNodes().forEach(node -> node.setConcentration(entity, concentration));
        this.getEdges().forEach(edge -> edge.addPermeability(entity, 1.0));
    }

    public Quantity<MolarConcentration> getSteepestDifference(ChemicalEntity entity) {
        return Quantities.getQuantity(this.getNodes().stream()
                .mapToDouble(node -> node.getSteepestConcentrationDifference(entity))
                .max().orElse(0.0), MOLE_PER_LITRE);
    }

    public Set<CellSection> getSections() {
        return new HashSet<>(this.sections.values());
    }

    public void addSection(CellSection cellSection) {
        this.sections.put(cellSection.getIdentifier(), cellSection);
    }

    public void addNodesToCompartment(EnclosedCompartment enclosedCompartment, Rectangle rectangle) {
        Set<BioNode> compartmentContent = new HashSet<>();
        Rectangle r = new Rectangle(rectangle.getTopRightVertex(), rectangle.getBottomLeftVertex());
        this.getNodes().forEach(node -> {
            if (node.getPosition().canBePlacedIn(r)) {
                compartmentContent.add(node);
                node.setCellSection(enclosedCompartment);
            }
        });
        if (!compartmentContent.isEmpty()) {
            this.sections.get(enclosedCompartment.getIdentifier()).getContent().addAll(compartmentContent);
            Membrane membrane = enclosedCompartment.generateMembrane();
            enclosedCompartment.getEnclosingMembrane().initializeNodes(this);
            this.sections.put(membrane.getIdentifier(), membrane);
        }
    }

}
