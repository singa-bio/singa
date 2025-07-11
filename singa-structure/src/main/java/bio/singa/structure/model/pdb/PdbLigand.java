package bio.singa.structure.model.pdb;

import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.general.AuthLeafIdentifier;
import bio.singa.structure.model.interfaces.Ligand;

/**
 * @author cl
 */
public class PdbLigand extends PdbLeafSubstructure implements Ligand {

    private String name;

    private String inchi;

    public PdbLigand(AuthLeafIdentifier leafIdentifier, StructuralFamily family) {
        super(leafIdentifier, family);
    }

    public PdbLigand(PdbLigand pdbLigand) {
        super(pdbLigand);
        name = pdbLigand.name;
        inchi = pdbLigand.inchi;
    }

    public PdbLigand(PdbLigand pdbLigand, AuthLeafIdentifier leafIdentifier) {
        super(pdbLigand, leafIdentifier);
        name = pdbLigand.name;
        inchi = pdbLigand.inchi;;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInchi() {
        return inchi;
    }

    public void setInchi(String inchi) {
        this.inchi = inchi;
    }

    @Override
    public PdbLigand getCopy() {
        return new PdbLigand(this);
    }
}
