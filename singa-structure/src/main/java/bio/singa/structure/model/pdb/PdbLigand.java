package bio.singa.structure.model.pdb;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.Ligand;

/**
 * @author cl
 */
public class PdbLigand extends PdbLeafSubstructure implements Ligand {

    private String name;

    public PdbLigand(PdbLeafIdentifier leafIdentifier, StructuralFamily family) {
        super(leafIdentifier, family);
    }

    public PdbLigand(PdbLigand oakLigand) {
        super(oakLigand);
    }

    public PdbLigand(PdbLigand oakLigand, PdbLeafIdentifier leafIdentifier) {
        super(oakLigand, leafIdentifier);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public PdbLigand getCopy() {
        return new PdbLigand(this);
    }
}
