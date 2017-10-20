package de.bioforscher.singa.structure.parser.pdb.ligands;

import de.bioforscher.singa.chemistry.descriptive.elements.Element;
import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.core.utility.Pair;
import de.bioforscher.singa.mathematics.vectors.Vector3D;
import de.bioforscher.singa.structure.model.graph.families.AminoAcidFamily;
import de.bioforscher.singa.structure.model.graph.families.LigandFamily;
import de.bioforscher.singa.structure.model.graph.families.NucleotideFamily;
import de.bioforscher.singa.structure.model.graph.interactions.BondType;
import de.bioforscher.singa.structure.model.graph.model.LeafIdentifier;
import de.bioforscher.singa.structure.model.graph.model.StructuralFamily;
import de.bioforscher.singa.structure.model.interfaces.AminoAcid;
import de.bioforscher.singa.structure.model.interfaces.LeafSubstructure;
import de.bioforscher.singa.structure.model.interfaces.Nucleotide;
import de.bioforscher.singa.structure.model.oak.*;
import de.bioforscher.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;

import java.util.*;

/**
 * @author cl
 */
public class CifFileParser {

    private static final int DEFAULT_VALUE_SPACING = 49;

    private List<String> lines;
    private String name;
    private String type;
    private String oneLetterCode;
    private String threeLetterCode;
    private String parent;

    private List<String> atomLines;
    private List<String> bondLines;

    private Map<String, OakAtom> atoms;
    private Map<Pair<String>, BondType> bonds;

    private CifFileParser(List<String> lines) {
        this.lines = lines;
        this.atoms = new HashMap<>();
        this.bonds = new HashMap<>();
        this.bondLines = new ArrayList<>();
        this.atomLines = new ArrayList<>();
    }


    public static LeafSubstructure<?> parseLeafSubstructure(List<String> lines) {
        CifFileParser parser = new CifFileParser(lines);
        return parser.parseCompleteLeafSubstructure();
    }

    public static LeafSkeleton parseLeafSkeleton(List<String> lines) {
        CifFileParser parser = new CifFileParser(lines);
        return parser.parseLeafSkeleton();
    }

    /**
     * Extracts a value from a one line entry from a cif file. Trimming white spaces and removing double quotes.
     *
     * @param line the line to extract
     * @return The extracted value.
     */
    private static String extractValue(String line) {
        return line.substring(DEFAULT_VALUE_SPACING).replace("\"", "").trim();
    }

    /**
     * Collect all relevant lines that are later required for extracting information.
     * @param skipAtoms True, if no atom lines should be parsed.
     */
    private void collectLines(boolean skipAtoms) {
        boolean bondSection = false;
        boolean atomSection = false;

        // extract information
        for (String line : this.lines) {
            // extract information
            extractInformation(line);
            // signifies start of bond section
            if (line.startsWith("_chem_comp_bond.pdbx_ordinal")) {
                bondSection = true;
                continue;
            }
            if (bondSection) {
                // extract bonds connecting the given atoms
                if (line.startsWith("#")) {
                    bondSection = false;
                    continue;
                }
                this.bondLines.add(line);
            }
            if (!skipAtoms) {
                if (line.startsWith("_chem_comp_atom.pdbx_ordinal")) {
                    atomSection = true;
                    continue;
                }
                if (atomSection) {
                    // extract bonds connecting the given atoms
                    if (line.startsWith("#")) {
                        atomSection = false;
                        continue;
                    }
                    this.atomLines.add(line);
                }
            }
        }
    }

    /**
     * Extracts and creates atoms from the extracted lines.
     */
    private void extractAtoms() {
        for (String line : this.atomLines) {
            String[] splitLine = line.split("\\s+");
            /// 1 = atom name, 3 = element, 9 = x coordinate, 10 = y coordinate, 11 = z coordinates, 17 = identifer
            int identifier = Integer.valueOf(splitLine[17]);
            Element element = ElementProvider.getElementBySymbol(splitLine[3]).orElse(ElementProvider.UNKOWN);
            String atomName = splitLine[1];
            Vector3D coordinates = new Vector3D(Double.valueOf(splitLine[9]), Double.valueOf(splitLine[10]), Double.valueOf(splitLine[11]));
            OakAtom atom = new  OakAtom(identifier, element, atomName, coordinates);
            this.atoms.put(atomName, atom);
        }
    }

    /**
     * Extracts the information required to create bonds.
     */
    private void extractBonds() {
        // for each of the collected bond lines
        for (String line : this.bondLines) {
            String[] splitLine = line.split("\\s+");
            // 1 = first atom, 2 = second atom, 3 = bond type
            this.bonds.put(new Pair<>(splitLine[1].replace("\"", ""), splitLine[2].replace("\"", "")),
                    BondType.getBondTypeByCifName(splitLine[3]).orElse(BondType.SINGLE_BOND));
        }
    }

    /**
     * Extracts information about the ligand.
     * @param line The line.
     */
    private void extractInformation(String line) {
        // extract compound name
        if (line.startsWith("_chem_comp.name")) {
            this.name = extractValue(line);
        }
        // extract compound type
        if (line.startsWith("_chem_comp.type")) {
            this.type = extractValue(line);
        }
        // extract one letter code
        if (line.startsWith("_chem_comp.one_letter_code")) {
            this.oneLetterCode = extractValue(line);
        }
        // extract three letter code
        if (line.startsWith("_chem_comp.three_letter_code")) {
            this.threeLetterCode = extractValue(line);
        }
        // extract parent
        if (line.startsWith("_chem_comp.mon_nstd_parent_comp_id")) {
            this.parent = extractValue(line);
        }
    }

    /**
     * Parses a leaf from scratch using only information provided in the cif file.
     * @return A leaf.
     */
    private OakLeafSubstructure<?> parseCompleteLeafSubstructure() {
        collectLines(false);
        extractAtoms();
        extractBonds();
        return createLeafSubstructure(LeafIdentifier.fromString("A-1"));
    }

    /**
     * Creates a complete {@link LeafSubstructure} using the information collected until the call of this method.
     * @param leafIdentifier The identifier this leaf should have.
     * @return A complete {@link LeafSubstructure} using the information collected until the call of this method.
     */
    private OakLeafSubstructure<?> createLeafSubstructure(LeafIdentifier leafIdentifier) {
        OakLeafSubstructure<?> leafSubstructure;
        if (isNucleotide()) {
            // check for nucleotides
                Optional<NucleotideFamily> nucleotideFamily = NucleotideFamily.getNucleotideByThreeLetterCode(this.parent);
            leafSubstructure = nucleotideFamily.map(nucleotideFamily1 -> new OakNucleotide(leafIdentifier, nucleotideFamily1, this.threeLetterCode))
                    .orElseGet(() -> new OakNucleotide(leafIdentifier, NucleotideFamily.getNucleotideByThreeLetterCode(this.threeLetterCode).orElseThrow(() ->
                            new IllegalArgumentException("Could not create Nucleotide with three letter code" + this.threeLetterCode))));

        } else if (isAminoAcid()) {
            // check for amino acids
            Optional<AminoAcidFamily> aminoAcidFamily = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(this.parent);
            leafSubstructure = aminoAcidFamily.map(aminoAcidFamily1 -> new OakAminoAcid(leafIdentifier, aminoAcidFamily1, this.threeLetterCode))
                    .orElseGet(() -> new OakAminoAcid(leafIdentifier, AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(this.threeLetterCode).orElseThrow(() ->
                            new IllegalArgumentException("Could not create Nucleotide with three letter code" + this.threeLetterCode))));
        } else {
            // else this is a ligand
            OakLigand OakLigand = new OakLigand(leafIdentifier, new LigandFamily(this.oneLetterCode, this.threeLetterCode));
            OakLigand.setName(this.name);
            leafSubstructure = OakLigand;
        }
        this.atoms.values().forEach(leafSubstructure::addAtom);
        connectAtoms(leafSubstructure);
        return leafSubstructure;
    }

    /**
     * Creates a leaf skeleton to be used to create complete leafs from.
     * @return A leaf skeleton.
     */
    private LeafSkeleton parseLeafSkeleton() {
        collectLines(true);
        extractBonds();
        return createLeafSkeleton();
    }

    /**
     * Creates a leaf skeleton only containing the information required to build a new leaf.
     * @return A leaf skeleton.
     */
    private LeafSkeleton createLeafSkeleton() {
        LeafSkeleton.AssignedFamily assignedFamily = null;
        StructuralFamily structuralFamily = null;
        if (isNucleotide()) {
            // check for nucleotides
            if (!this.parent.equals("?")) {
                assignedFamily = LeafSkeleton.AssignedFamily.MODIFIED_NUCLEOTIDE;
            }else{
                // TODO fix this fallback solution
                assignedFamily = LeafSkeleton.AssignedFamily.LIGAND;
            }
        } else if (isAminoAcid()) {
            // check for amino acids
            assignedFamily = LeafSkeleton.AssignedFamily.MODIFIED_AMINO_ACID;
        } else {
            // else this is a ligand
            assignedFamily = LeafSkeleton.AssignedFamily.LIGAND;
        }
        return new LeafSkeleton(this.threeLetterCode, this.parent, assignedFamily, this.bonds);
    }

    /**
     * Returns whether this molecule can be considered as a {@link Nucleotide}. This checks if the type is either
     * {@code RNA LINKING} or {@code DNA LINKING}.
     *
     * @return
     */
    private boolean isNucleotide() {
        return this.type.equalsIgnoreCase("RNA LINKING") || this.type.equalsIgnoreCase("DNA LINKING");
    }

    /**
     * Returns whether this molecule can be considered as a {@link AminoAcid}. This checks if the type is
     * {@code L-PEPTIDE LINKING} and a valid parent is specified.
     *
     * @return
     */
    private boolean isAminoAcid() {
        return this.type.equalsIgnoreCase("L-PEPTIDE LINKING"); // && AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(this.parent).isPresent();
    }

    /**
     * Connects the atoms in the leaf as specified in the bonds map.
     *
     * @param leafWithAtoms The leaf to connect.
     */
    private void connectAtoms(OakLeafSubstructure<?> leafWithAtoms) {
        int bondCounter = 0;
        for (Map.Entry<Pair<String>, BondType> bond : this.bonds.entrySet()) {
            OakBond oakBond = new OakBond(bondCounter++, bond.getValue());
            leafWithAtoms.addBondBetween(oakBond, this.atoms.get(bond.getKey().getFirst()),
                    this.atoms.get(bond.getKey().getSecond()));
        }
    }

}
