package de.bioforscher.chemistry.parser.pdb.ligands;

import de.bioforscher.chemistry.descriptive.elements.Element;
import de.bioforscher.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.atoms.RegularAtom;
import de.bioforscher.chemistry.physical.families.AminoAcidFamily;
import de.bioforscher.chemistry.physical.families.LigandFamily;
import de.bioforscher.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.chemistry.physical.leafes.AminoAcid;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.model.BondType;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.core.utility.Pair;
import de.bioforscher.mathematics.vectors.Vector3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leberech on 19/12/16.
 */
public class CifFileParser {

    private static final int DEFAULT_VALUE_SPACING = 49;

    private List<String> lines;
    private String name;
    private String type;
    private String oneLetterCode;
    private String threeLetterCode;
    private String parent;

    private Map<String, Atom> atoms;
    private Map<Pair<String>, String> bonds;

    private CifFileParser(List<String> lines) {
        this.lines = lines;
        this.atoms = new HashMap<>();
        this.bonds = new HashMap<>();
    }


    public static LeafSubstructure<?, ?> parseLeafSubstructureFromCif(List<String> lines) {
        CifFileParser parser = new CifFileParser(lines);
        return parser.createLeaf();
    }

    public static LeafSubstructure<?, ?> parseLeafSubstructureFromCif(List<String> lines, Map<String, Atom> atoms, LeafIdentifier identifier) {
        CifFileParser parser = new CifFileParser(lines);
        return parser.createLeaf(atoms, identifier);
    }

    private LeafSubstructure<?, ?> createLeaf(Map<String, Atom> atoms, LeafIdentifier leafIdentifier) {
        this.atoms = atoms;
        boolean bondSection = false;
        List<String> bondLines = new ArrayList<>();
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
                    break;
                }
                bondLines.add(line);
            }

        }
        extractBonds(bondLines);
        return createLeaf(leafIdentifier);
    }

    private LeafSubstructure<?, ?> createLeaf() {
        boolean bondSection = false;
        boolean atomSection = false;
        List<String> bondLines = new ArrayList<>();
        List<String> atomLines = new ArrayList<>();
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
                bondLines.add(line);
            }

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
                atomLines.add(line);
            }

        }
        extractAtoms(atomLines);
        extractBonds(bondLines);
        return createLeaf(LeafIdentifier.fromString("A-1"));
    }

    private void extractAtoms(List<String> atomLines) {
        for (String line : atomLines) {
            String[] splitLine = line.split("\\s+");
            /// 1 = atom name, 3 = element, 9 = x coordinate, 10 = y coordinate, 11 = z coordinates, 17 = identifer
            int identifier = Integer.valueOf(splitLine[17]);
            Element element = ElementProvider.getElementBySymbol(splitLine[3]).orElse(ElementProvider.UNKOWN);
            String atomName = splitLine[1];
            Vector3D coordinates = new Vector3D(Double.valueOf(splitLine[9]), Double.valueOf(splitLine[10]), Double.valueOf(splitLine[11]));
            Atom atom = new RegularAtom(identifier, element, atomName, coordinates);
            this.atoms.put(atomName, atom);
        }
    }

    private void extractBonds(List<String> bondLines) {
        // for each of the collected bond lines
        for (String line : bondLines) {
            String[] splitLine = line.split("\\s+");
            // 1 = first atom, 2 = second atom, 3 = bond type
            this.bonds.put(new Pair<>(splitLine[1].replace("\"", ""), splitLine[2].replace("\"", "")), splitLine[3]);
        }
    }

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

    private LeafSubstructure<?, ?> createLeaf(LeafIdentifier leafIdentifier) {
        LeafSubstructure<?, ?> leafSubstructure = null;
        if (isNucleotide()) {
            // check for nucleotides
            if (!this.parent.equals("?")) {
                NucleotideFamily nucleotideFamily = NucleotideFamily.getNucleotideByThreeLetterCode(this.parent)
                        .orElse(NucleotideFamily.UNKNOWN);
                leafSubstructure = new Nucleotide(leafIdentifier, nucleotideFamily, this.threeLetterCode);
            }
        } else if (isAminoAcid()) {
            // check for amino acids
            AminoAcidFamily aminoAcidFamily = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(this.parent)
                    .orElse(AminoAcidFamily.UNKNOWN);
            leafSubstructure = new AminoAcid(leafIdentifier, aminoAcidFamily, this.threeLetterCode);
        } else {
            // else this is a ligand
            leafSubstructure = new AtomContainer<>(leafIdentifier, new LigandFamily(this.oneLetterCode, this.threeLetterCode));
        }
        this.atoms.values().forEach(leafSubstructure::addNode);
        connectAtoms(leafSubstructure);
        return leafSubstructure;
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
        return this.type.equalsIgnoreCase("L-PEPTIDE LINKING") && AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(this.parent).isPresent();
    }

    /**
     * Connects the atoms in the leaf as specified in the bonds map.
     *
     * @param leafWithAtoms The leaf to connect.
     */
    private void connectAtoms(LeafSubstructure<?, ?> leafWithAtoms) {
        for (Map.Entry<Pair<String>, String> bond : this.bonds.entrySet()) {
            leafWithAtoms.addEdgeBetween(this.atoms.get(bond.getKey().getFirst()),
                    this.atoms.get(bond.getKey().getSecond()),
                    BondType.getBondTypeByCifName(bond.getValue()).orElse(BondType.SINGLE_BOND));
        }
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

    public static String getLeafType(List<String> lines) {
        for (String line : lines) {
            if (line.startsWith("_chem_comp.type")) {
                Pattern pattern = Pattern.compile("\"(.*)\"");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return "UNKNOWN";
    }

}
