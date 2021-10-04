package bio.singa.structure.model.general;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.core.utility.Pair;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.Nucleotide;
import bio.singa.structure.model.pdb.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cl
 */
public class LeafSkeleton {

    public static String DEFAULT_PARENT = "?";

    private String threeLetterCode;
    private String parent;
    private String inchi;
    private String name;
    private AssignedFamily assignedFamily;
    private StructuralFamily structuralFamily;
    private Map<Pair<String>, CovalentBondType> bonds;
    private Map<String, PdbAtom> atoms;

    public LeafSkeleton(String threeLetterCode, String parent, AssignedFamily assignedFamily, Map<Pair<String>, CovalentBondType> bonds) {
        this.threeLetterCode = threeLetterCode;
        this.parent = parent;
        this.assignedFamily = assignedFamily;
        this.bonds = bonds;
    }

    public LeafSkeleton(String threeLetterCode) {
        this.threeLetterCode = threeLetterCode;
        parent = DEFAULT_PARENT;
        assignedFamily = AssignedFamily.LIGAND;
        bonds = new HashMap<>();
        atoms = new HashMap<>();
    }

    public Map<String, PdbAtom> getAtoms() {
        return atoms;
    }

    public void setAtoms(Map<String, PdbAtom> atoms) {
        this.atoms = atoms;
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

    public AssignedFamily getAssignedFamily() {
        return assignedFamily;
    }

    public void setAssignedFamily(AssignedFamily assignedFamily) {
        this.assignedFamily = assignedFamily;
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

    public boolean hasBonds() {
        return !bonds.isEmpty();
    }

    public PdbLeafSubstructure toRealLeafSubstructure(PdbLeafIdentifier identifer, Map<String, PdbAtom> atoms) {
        PdbLeafSubstructure substructure;
        switch (assignedFamily) {
            case MODIFIED_AMINO_ACID: {
                substructure = new PdbAminoAcid(identifer, StructuralFamilies.AminoAcids.getOrUnknown(parent), threeLetterCode);
                break;
            }
            case MODIFIED_NUCLEOTIDE: {
                substructure = new PdbNucleotide(identifer, StructuralFamilies.Nucleotides.getOrUnknown(parent), threeLetterCode);
                break;
            }
            default: {
                PdbLigand ligand = new PdbLigand(identifer, new StructuralFamily("?", threeLetterCode));
                ligand.setName(name);
                substructure = ligand;
                break;
            }
        }
        substructure.setAnnotatedAsHetAtom(true);
        atoms.values().forEach(substructure::addAtom);
        for (Map.Entry<Pair<String>, CovalentBondType> bond : bonds.entrySet()) {
            substructure.addBondBetween(atoms.get(bond.getKey().getFirst()),
                    atoms.get(bond.getKey().getSecond()),bond.getValue());
        }
        return substructure;
    }

    public void connect(PdbLeafSubstructure substructure, Map<String, PdbAtom> atomMap) {
        for (Map.Entry<Pair<String>, CovalentBondType> bond : bonds.entrySet()) {
            substructure.addBondBetween(atomMap.get(bond.getKey().getFirst()),
                    atomMap.get(bond.getKey().getSecond()),bond.getValue());
        }
    }

    public enum AssignedFamily {
        AMINO_ACID, NUCLEOTIDE, MODIFIED_AMINO_ACID, MODIFIED_NUCLEOTIDE, LIGAND
    }

    /**
     * Returns whether this molecule can be considered as a {@link Nucleotide}. This checks if the type is either {@code
     * RNA LINKING} or {@code DNA LINKING}.
     *
     * @return True if the entity is a nucleotide.
     */
    public static boolean isNucleotide(String type) {
        return type.equalsIgnoreCase("RNA LINKING") || type.equalsIgnoreCase("DNA LINKING");
    }

    /**
     * Returns whether this molecule can be considered as a {@link AminoAcid}. This checks if the type is {@code
     * L-PEPTIDE LINKING} and a valid parent is specified.
     *
     * @return True if entity is amino acid.
     */
    public static boolean isAminoAcid(String type) {
        return type.equalsIgnoreCase("L-PEPTIDE LINKING") || type.equalsIgnoreCase("PEPTIDE LINKING");
    }

}
