package bio.singa.structure.model.cif;

import bio.singa.structure.model.interfaces.Ligand;

public class CifLigand extends CifLeafSubstructure implements Ligand {

    private String name;

    public CifLigand(CifLeafIdentifier leafIdentifier) {
        super(leafIdentifier);
    }

    public CifLigand(CifLigand cifLigand) {
        super(cifLigand);
    }

    @Override
    public CifLigand getCopy() {
        return new CifLigand(this);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
