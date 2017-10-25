package de.bioforscher.singa.simulation.model.compartments;

import de.bioforscher.singa.simulation.model.concentrations.MembraneContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public class Membrane extends CellSection {

    private final EnclosedCompartment innerCompartment;

    private CellSection innerLayer;
    private CellSection outerLayer;

    public Membrane(String identifier, String name, EnclosedCompartment innerCompartment) {
        super(identifier, name);
        this.innerCompartment = innerCompartment;
        this.innerLayer = new EnclosedCompartment(identifier+"I", name+ " Inner Layer");
        this.innerLayer.setSubsection(true);
        this.outerLayer = new EnclosedCompartment(identifier+"O", name+ " Outer Layer");
        this.innerLayer.setSubsection(true);
    }

    public static Membrane forCompartment(EnclosedCompartment enclosedCompartment) {
        return new Membrane(enclosedCompartment.getIdentifier()+"-M", enclosedCompartment.getName()+" Membrane", enclosedCompartment);
    }

    public EnclosedCompartment getInnerCompartment() {
        return this.innerCompartment;
    }

    public CellSection getInnerLayer() {
        return innerLayer;
    }

    public CellSection getOuterLayer() {
        return outerLayer;
    }

    public void initializeNodes(AutomatonGraph automatonGraph) {
        // reinitialize MultiConcentrationContainer
        for (AutomatonNode node: getContent()) {
            // get adjacent compartments that are not the inner compartment and not the membrane itself
            Set<CellSection> sections = node.getNeighbours().stream()
                    .map(AutomatonNode::getCellSection)
                    .filter(cellSection -> !cellSection.equals(this.innerCompartment) && !cellSection.equals(this))
                    .collect(Collectors.toSet());
            if (sections.size() != 1) {
                throw new IllegalStateException("The node "+node+" is considered as a membrane node but has neighbours " +
                        "in more than 3 states (inner section, outer section, membrane section)");
            }
            node.setConcentrationContainer(new MembraneContainer(sections.iterator().next(), this.innerCompartment, this));
        }
    }

}
