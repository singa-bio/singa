package de.bioforscher.singa.structure.parser.pdb.structures.tokens;

import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.structure.model.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.families.LigandFamily;
import de.bioforscher.singa.structure.model.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.identifiers.LeafIdentifier;
import de.bioforscher.singa.structure.model.oak.*;

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
        return this.threeLetterCode;
    }

    public void setThreeLetterCode(String threeLetterCode) {
        this.threeLetterCode = threeLetterCode;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public AssignedFamily getAssignedFamily() {
        return this.assignedFamily;
    }

    public void setAssignedFamily(AssignedFamily assignedFamily) {
        this.assignedFamily = assignedFamily;
    }

    public Map<Pair<String>, BondType> getBonds() {
        return this.bonds;
    }

    public void setBonds(Map<Pair<String>, BondType> bonds) {
        this.bonds = bonds;
    }

    public OakLeafSubstructure<?> toRealLeafSubstructure(LeafIdentifier identifer, Map<String, OakAtom> atoms) {
        OakLeafSubstructure<?> substructure;
        switch (this.assignedFamily) {
            case MODIFIED_AMINO_ACID: {
                substructure = new OakAminoAcid(identifer, AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(this.parent)
                        .orElse(AminoAcidFamily.UNKNOWN), this.threeLetterCode);
                break;
            }
            case MODIFIED_NUCLEOTIDE: {
                substructure = new OakNucleotide(identifer, NucleotideFamily.getNucleotideByThreeLetterCode(this.parent)
                        .orElse(NucleotideFamily.UNKNOWN), this.threeLetterCode);
                break;
            }
            default: {
                substructure = new OakLigand(identifer, new LigandFamily("?", this.threeLetterCode));
                break;
            }
        }
        atoms.values().forEach(substructure::addAtom);
        int bondCounter = 0;
        for (Map.Entry<Pair<String>, BondType> bond : this.bonds.entrySet()) {
            substructure.addBondBetween(new OakBond(bondCounter, bond.getValue()), atoms.get(bond.getKey().getFirst()),
                    atoms.get(bond.getKey().getSecond()));
            bondCounter++;
        }
        return substructure;
    }

    public enum AssignedFamily {
        AMINO_ACID, NUCLEOTIDE, MODIFIED_AMINO_ACID, MODIFIED_NUCLEOTIDE, LIGAND
    }

}
