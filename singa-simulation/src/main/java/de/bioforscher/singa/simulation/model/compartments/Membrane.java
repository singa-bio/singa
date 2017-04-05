package de.bioforscher.singa.simulation.model.compartments;

import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.model.graphs.MultiConcentrationContainer;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class Membrane extends CellSection {

    private final EnclosedCompartment enclosingCompartment;

    public Membrane(String identifier, String name, Set<BioNode> content, EnclosedCompartment enclosingCompartment) {
        super(identifier, name, content);
        this.enclosingCompartment = enclosingCompartment;
    }

    public Membrane(String identifier, String name, EnclosedCompartment enclosingCompartment) {
        super(identifier, name);
        this.enclosingCompartment = enclosingCompartment;
    }

    public static Membrane forCompartment(EnclosedCompartment enclosedCompartment) {
        return new Membrane(enclosedCompartment.getIdentifier()+"-M", enclosedCompartment.getName()+" Membrane", enclosedCompartment);
    }

    public EnclosedCompartment getEnclosingCompartment() {
        return this.enclosingCompartment;
    }

    public void initializeNodes(AutomatonGraph automatonGraph) {
        // reinitialize MultiConcentrationContainer
        for (BioNode node: getContent()) {
            // get adjacent compartments
            Set<CellSection> sections = node.getNeighbours().stream()
                    .map(BioNode::getCellSection)
                    .collect(Collectors.toSet());
            sections.add(this);
            node.setConcentrations(new MultiConcentrationContainer(sections));
            node.setCellSection(this);
        }
    }

}
