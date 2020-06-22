package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.edges.SimpleLineSegment;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonGraph;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.sections.CellTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author cl
 * @deprecated
 */
public class MembraneTracer {

    private static final Logger logger = LoggerFactory.getLogger(MembraneTracer.class);

    // input
    private HashMap<CellRegion, List<AutomatonNode>> regionNodeMapping;
    private AutomatonGraph graph;

    // output
    private List<Membrane> membranes;

    // working
    private LinkedList<AutomatonNode> currentNodes;
    private Deque<AutomatonNode> queue;
    private List<AutomatonNode> unprocessedNodes;

    public static CellRegion getRegion(Map<CellRegion, Set<Vector2D>> regionMap, Vector2D vector) {
        for (Map.Entry<CellRegion, Set<Vector2D>> entry : regionMap.entrySet()) {
            if (entry.getValue().contains(vector)) {
                return entry.getKey();
            }
        }
        throw new IllegalArgumentException("The segment is not contained in this membrane");
    }

    public static List<Membrane> regionsToMembrane(AutomatonGraph graph) {
        MembraneTracer composer = new MembraneTracer(graph);
        return composer.membranes;
    }

    public MembraneTracer(AutomatonGraph graph) {
        logger.info("Initializing membranes from assigned regions.");
        this.graph = graph;
        membranes = new ArrayList<>();
        if (graph.getNodes().size() == 1) {
            traceSingleNode(graph.getNode(0, 0));
        }
        logger.info("No more than one node is supported.");
    }

    private void traceSingleNode(AutomatonNode node) {
        if (!node.getCellRegion().hasMembrane()) {
            logger.warn("The graph contains only one node that has no membrane region assigned. No Membrane will be created.");
            return;
        }
        double simulationExtend = Environment.getSimulationExtend();
        // add horizontal membrane segment
        Vector2D start = new Vector2D(0, simulationExtend / 2.0);
        Vector2D end = new Vector2D(simulationExtend, simulationExtend / 2.0);
        Membrane membrane = new Membrane(node.getCellRegion().getIdentifier());
        CellRegion region = node.getCellRegion();
        membrane.setMembraneRegion(region);
        CellRegion innerRegion = new CellRegion(region.getInnerSubsection().getIdentifier(), region.getInnerSubsection().getGoTerm());
        innerRegion.addSubsection(CellTopology.INNER, region.getInnerSubsection());
        membrane.setInnerRegion(innerRegion);
        SimpleLineSegment segment = new SimpleLineSegment(start, end);
        membrane.addSegment(node, segment);
        membranes.add(membrane);
    }

}
