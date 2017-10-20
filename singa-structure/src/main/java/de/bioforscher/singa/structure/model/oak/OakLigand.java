package de.bioforscher.singa.structure.model.oak;

import de.bioforscher.singa.structure.model.graph.families.LigandFamily;
import de.bioforscher.singa.structure.model.graph.model.LeafIdentifier;
import de.bioforscher.singa.structure.model.interfaces.Ligand;

/**
 * @author cl
 */
public class OakLigand extends OakLeafSubstructure<LigandFamily> implements Ligand {

    private String name;

    public OakLigand(LeafIdentifier leafIdentifier, LigandFamily family) {
        super(leafIdentifier, family);
    }

    public OakLigand(OakLigand oakLigand) {
        super(oakLigand);
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public OakLigand getCopy() {
        return new OakLigand(this);
    }
}
