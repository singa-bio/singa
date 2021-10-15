package bio.singa.structure.model.general;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.core.utility.Pair;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.LigandType;
import bio.singa.structure.model.interfaces.Nucleotide;
import bio.singa.structure.model.pdb.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class LeafSkeleton {

    private String threeLetterCode;
    private String parent;
    private String inchi;
    private String name;
    private LigandType ligandType;
    private StructuralFamily structuralFamily;
    private Map<Pair<String>, CovalentBondType> bonds;

    public LeafSkeleton() {
        bonds = new HashMap<>();
        parent = "";
    }

    public LeafSkeleton(String threeLetterCode) {
        this();
        this.threeLetterCode = threeLetterCode;
    }

    public String getInchi() {
        return inchi;
    }

    public void setInchi(String inchi) {
        this.inchi = inchi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThreeLetterCode() {
        return threeLetterCode;
    }

    public void setThreeLetterCode(String threeLetterCode) {
        this.threeLetterCode = threeLetterCode;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public LigandType getLigandType() {
        return ligandType;
    }

    public void setLigandType(LigandType ligandType) {
        this.ligandType = ligandType;
    }

    public StructuralFamily getStructuralFamily() {
        return structuralFamily;
    }

    public void setStructuralFamily(StructuralFamily structuralFamily) {
        this.structuralFamily = structuralFamily;
    }

    public Map<Pair<String>, CovalentBondType> getBonds() {
        return bonds;
    }

    public void setBonds(Map<Pair<String>, CovalentBondType> bonds) {
        this.bonds = bonds;
    }

    public void addBond(Pair<String> atomNames, CovalentBondType bondType) {
        bonds.put(atomNames, bondType);
    }

    public boolean hasBonds() {
        return !bonds.isEmpty();
    }

    public void connect(PdbLeafSubstructure substructure, Map<String, PdbAtom> atomMap) {
        for (Map.Entry<Pair<String>, CovalentBondType> bond : bonds.entrySet()) {
            substructure.addBondBetween(atomMap.get(bond.getKey().getFirst()),
                    atomMap.get(bond.getKey().getSecond()), bond.getValue());
        }
    }


}
