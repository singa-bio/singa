package de.bioforscher.simulation.model;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.mathematics.graphs.model.AbstractEdge;

import java.util.HashMap;
import java.util.Map;

public class BioEdge extends AbstractEdge<BioNode> {

    private Map<ChemicalEntity, Double> permeability;

    public BioEdge() {
        this.permeability = new HashMap<>();
    }

    public BioEdge(BioNode source, BioNode target) {
        super(source, target);
        this.permeability = new HashMap<>();
    }

    public BioEdge(int identifier, BioNode source, BioNode target) {
        super(identifier, source, target);
        this.permeability = new HashMap<>();
    }

    public Map<ChemicalEntity, Double> getPermeability() {
        return this.permeability;
    }

    public void setPermeability(Map<ChemicalEntity, Double> speciesPermeability) {
        this.permeability = speciesPermeability;
    }

    public void addPermeability(ChemicalEntity entity, double permeability) {
        this.permeability.put(entity, permeability);
    }

    public double getPermeability(ChemicalEntity entity) {
        if (this.permeability.containsKey(entity)) {
            return this.permeability.get(entity);
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "BioEdge [identifier=" + this.identifier + "]";
    }

}
