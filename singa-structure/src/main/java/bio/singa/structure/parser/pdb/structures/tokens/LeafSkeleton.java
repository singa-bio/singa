package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Pair;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.LigandFamily;
import bio.singa.structure.model.families.NucleotideFamily;
import bio.singa.structure.model.identifiers.LeafIdentifier;
import bio.singa.structure.model.oak.*;

import java.util.Map;

/**
 * @author cl
 */
public class LeafSkeleton {

    private String threeLetterCode;
    private String parent;
    private AssignedFamily assignedFamily;
    private Map<Pair<String>, BondType> bonds;

    public LeafSkeleton(String threeLetterCode, String parent, AssignedFamily assignedFamily, Map<Pair<String>, BondType> bonds) {
        this.threeLetterCode = threeLetterCode;
        this.parent = parent;
        this.assignedFamily = assignedFamily;
        this.bonds = bonds;
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

    public AssignedFamily getAssignedFamily() {
        return assignedFamily;
    }

    public void setAssignedFamily(AssignedFamily assignedFamily) {
        this.assignedFamily = assignedFamily;
    }

    public Map<Pair<String>, BondType> getBonds() {
        return bonds;
    }

    public void setBonds(Map<Pair<String>, BondType> bonds) {
        this.bonds = bonds;
    }

    public OakLeafSubstructure<?> toRealLeafSubstructure(LeafIdentifier identifer, Map<String, OakAtom> atoms) {
        OakLeafSubstructure<?> substructure;
        switch (assignedFamily) {
            case MODIFIED_AMINO_ACID: {
                substructure = new OakAminoAcid(identifer, AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(parent)
                        .orElse(AminoAcidFamily.UNKNOWN), threeLetterCode);
                break;
            }
            case MODIFIED_NUCLEOTIDE: {
                substructure = new OakNucleotide(identifer, NucleotideFamily.getNucleotideByThreeLetterCode(parent)
                        .orElse(NucleotideFamily.UNKNOWN), threeLetterCode);
                break;
            }
            default: {
                substructure = new OakLigand(identifer, new LigandFamily("?", threeLetterCode));
                break;
            }
        }
        substructure.setAnnotatedAsHetAtom(true);
        atoms.values().forEach(substructure::addAtom);
        for (Map.Entry<Pair<String>, BondType> bond : bonds.entrySet()) {
            substructure.addBondBetween(atoms.get(bond.getKey().getFirst()),
                    atoms.get(bond.getKey().getSecond()),bond.getValue());
        }
        return substructure;
    }

    public enum AssignedFamily {
        AMINO_ACID, NUCLEOTIDE, MODIFIED_AMINO_ACID, MODIFIED_NUCLEOTIDE, LIGAND
    }

}
