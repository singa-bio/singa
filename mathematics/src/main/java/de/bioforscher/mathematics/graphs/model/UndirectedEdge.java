package de.bioforscher.mathematics.graphs.model;

public class UndirectedEdge extends AbstractEdge<RegularNode> {

    public UndirectedEdge() {
        super();
    }

    public UndirectedEdge(int identifier, RegularNode source, RegularNode target) {
        super(identifier, source, target);
    }

    public UndirectedEdge(RegularNode source, RegularNode target) {
        super(source, target);
    }

}
