package de.bioforscher.singa.chemistry.parser.pdb.structures.tokens;

import de.bioforscher.singa.chemistry.physical.atoms.Atom;
import de.bioforscher.singa.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.singa.chemistry.physical.families.LigandFamily;
import de.bioforscher.singa.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.singa.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.singa.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.singa.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.singa.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.singa.chemistry.physical.model.BondType;
import de.bioforscher.singa.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.singa.core.utility.Pair;

import java.util.Map;

/**
 * @author cl
 */
public class LeafSkeleton {

    public enum AssignedFamily {
        AMINO_ACID, NUCLEOTIDE, MODIFIED_AMINO_ACID, MODIFIED_NUCLEOTIDE, LIGAND
    }

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

    public LeafSubstructure<?, ?> toRealLeafSubStructure(LeafIdentifier identifer, Map<String, Atom> atoms) {
        LeafSubstructure<?, ?> substructure;
        switch (this.assignedFamily) {
            case MODIFIED_AMINO_ACID: {
                substructure = new AminoAcid(identifer, AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(this.parent)
                        .orElse(AminoAcidFamily.UNKNOWN), this.threeLetterCode);
                break;
            }
            case MODIFIED_NUCLEOTIDE: {
                substructure = new Nucleotide(identifer, NucleotideFamily.getNucleotideByThreeLetterCode(this.parent)
                        .orElse(NucleotideFamily.UNKNOWN), this.threeLetterCode);
                break;
            }
            default: {
                substructure = new AtomContainer<>(identifer, new LigandFamily("?", this.threeLetterCode));
                break;
            }
        }
        atoms.values().forEach(substructure::addNode);
        for (Map.Entry<Pair<String>, BondType> bond : this.bonds.entrySet()) {
            substructure.addEdgeBetween(atoms.get(bond.getKey().getFirst()),
                    atoms.get(bond.getKey().getSecond()), bond.getValue());
        }
        return substructure;
    }

}
