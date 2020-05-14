package bio.singa.structure.model.oak;

import bio.singa.structure.model.families.LigandFamily;
import bio.singa.structure.model.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.Ligand;

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
