package bio.singa.structure.model.oak;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Ligand;

/**
 * @author cl
 */
public class OakLigand extends OakLeafSubstructure implements Ligand {

    private String name;

    public OakLigand(PdbLeafIdentifier leafIdentifier, StructuralFamily family) {
        super(leafIdentifier, family);
    }

    public OakLigand(OakLigand oakLigand) {
        super(oakLigand);
    }

    public OakLigand(OakLigand oakLigand, PdbLeafIdentifier leafIdentifier) {
        super(oakLigand, leafIdentifier);
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
