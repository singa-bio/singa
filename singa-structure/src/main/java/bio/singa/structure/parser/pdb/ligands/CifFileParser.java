package bio.singa.structure.parser.pdb.ligands;

import bio.singa.chemistry.model.CovalentBondType;
import bio.singa.chemistry.model.elements.Element;
import bio.singa.chemistry.model.elements.ElementProvider;
import bio.singa.core.utility.Pair;
import bio.singa.structure.model.oak.LeafIdentifier;
import bio.singa.mathematics.vectors.Vector3D;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.LigandFamily;
import bio.singa.structure.model.families.NucleotideFamily;
import bio.singa.structure.model.interfaces.AminoAcid;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.interfaces.Nucleotide;
import bio.singa.structure.model.oak.*;
import bio.singa.structure.parser.pdb.structures.tokens.LeafSkeleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cl
 */
public class CifFileParser {

    private static final Logger logger = LoggerFactory.getLogger(CifFileParser.class);

    private static final Pattern DEFAULT_VALUE_PATTERN = Pattern.compile("([\\w.]+)\\s+(.+)");
    private static final Pattern quotationPattern = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
    private final List<String> lines;
    private final List<String> atomLines;
    private final List<String> bondLines;

    private final Map<String, OakAtom> atoms;
    private final Map<String, Vector3D> defaultCoordinates;
    private final Map<String, Vector3D> idealCoordinates;
    private final Map<Pair<String>, CovalentBondType> bonds;
    private boolean defaultIncomplete;
    private boolean idealIncomplete;
    private String name;
    private String type;
    private String oneLetterCode;
    private String threeLetterCode;
    private String parent;
    private String inchi;
    private boolean singleAtom;
    private boolean singleBond;

    private CifFileParser(List<String> lines) {
        this.lines = lines;
        atoms = new HashMap<>();
        defaultCoordinates = new HashMap<>();
        idealCoordinates = new HashMap<>();
        bonds = new HashMap<>();
        bondLines = new ArrayList<>();
        atomLines = new ArrayList<>();
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
        // assumes key-value pair per line, separated by white space characters
        // FIXME errors occurring because CIF files have been apparently been reformatted by RCSB, multi line entries might occur, see e.g. _chem_comp.name in http://files.rcsb.org/ligands/view/5MU.cif
        // FIXME DEFAULT_VALUE_SPACING concept does not work anymore
        Matcher matcher = DEFAULT_VALUE_PATTERN.matcher(line);
        if (matcher.matches()) {
            String valueMatch = matcher.group(2);
            return valueMatch.replace("\"", "").trim();
        }
        return "";
    }

    private static String replaceQuotation(String quotedString) {
        if (!quotedString.contains("\"")) {
            return quotedString;
        }
        Matcher matcher = quotationPattern.matcher(quotedString);
        String target = quotedString;
        while (matcher.find()) {
            String substring = matcher.group();
            String replacement = substring.replaceAll("\\s+", "_");
            target = target.replace(substring, replacement);
        }
        return target;
    }

    /**
     * Collect all relevant lines that are later required for extracting information.
     */
    private void collectLines() {
        boolean bondSection = false;
        boolean atomSection = false;
        boolean descriptorSection = false;

        // extract information
        ListIterator<String> lineIterator = lines.listIterator();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            // extract information
            extractInformation(line);
            extractLigandName(line, lineIterator);
            // signifies start of bond section
            if (line.startsWith("_chem_comp_bond")) {
                bondSection = true;
            }
            if (bondSection) {
                // extract bonds connecting the given atoms
                if (line.startsWith("#")) {
                    bondSection = false;
                    continue;
                }
                // if there is only one atom, split results in multiple entries
                String[] split = line.split("\\s+");
                if (line.startsWith("_") && split.length > 1) {
                    processBondBlock(lineIterator);
                    bondSection = false;
                    continue;
                }
                if (!line.startsWith("_")) {
                    bondLines.add(line);
                }
            }
            if (line.startsWith("_chem_comp_atom")) {
                atomSection = true;
            }
            if (atomSection) {
                // extract bonds connecting the given atoms
                if (line.startsWith("#")) {
                    atomSection = false;
                    continue;
                }
                String[] split = line.split("\\s+");
                if (line.startsWith("_") && split.length > 1) {
                    processAtomBlock(lineIterator);
                    atomSection = false;
                    continue;
                }
                if (!line.startsWith("_")) {
                    atomLines.add(line);
                }
            }
            // chemical identifier section
            if (line.startsWith("_pdbx_chem_comp_descriptor.descriptor")) {
                descriptorSection = true;
            } else if (descriptorSection) {
                if (line.startsWith("#")) {
                    descriptorSection = false;
                    continue;
                }
                String[] splitLine = line.split("\\s+");
                if (splitLine.length == 1) {
                    continue;
                }
                if (splitLine[1].equals("InChI")) {
                    // multi line InChI detected
                    if (splitLine.length == 4) {
                        StringJoiner assembledInchi = new StringJoiner("");
                        while (lineIterator.hasNext()) {
                            String inchiLine = lineIterator.next().trim();
                            if (inchiLine.startsWith(";")) {
                                if (inchiLine.length() == 1) {
                                    // last line is empty
                                    inchi = assembledInchi.toString();
                                    break;
                                }
                                // start of multiline InChI
                                assembledInchi.add(inchiLine.replaceAll("\"", "").replaceFirst(";", ""));
                            }
                            if (inchiLine.startsWith("\"")) {
                                // start of multiline InChI
                                assembledInchi.add(inchiLine.replaceAll("\"", ""));
                                if (inchiLine.endsWith("\"")) {
                                    // last line is empty
                                    inchi = assembledInchi.toString();
                                    break;
                                }
                            }
                        }
                    } else {
                        // single line InChI detected
                        inchi = splitLine[4].replace("\"", "");
                    }
                }
            }
        }

    }

    private void processBondBlock(ListIterator<String> lineIterator) {
        lineIterator.previous();
        String firstAtom = "";
        String secondAtom = "";
        CovalentBondType bond = CovalentBondType.SINGLE_BOND;

        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            if (line.startsWith("#")) {
                addBond(firstAtom, secondAtom, bond);
                return;
            }
            String[] split = line.split("\\s+");
            if (split.length < 2) {
                // skip potential empty line
                continue;
            }
            String id = split[0];
            String value = split[1];
            if ("_chem_comp_bond.atom_id_1".equals(id)) {
                firstAtom = value;
            } else if ("_chem_comp_bond.atom_id_2".equals(id)) {
                secondAtom = value;
            } else if ("_chem_comp_bond.value_order".equals(id)) {
                bond = CovalentBondType.getBondForCifString(value);
            }
        }
    }

    private void processAtomBlock(ListIterator<String> lineIterator) {
        lineIterator.previous();
        String element = "";
        String charge = "";
        String atomName = "";
        String index = "";

        String xDefault = "";
        String yDefault = "";
        String zDefault = "";

        String xIdeal = "";
        String yIdeal = "";
        String zIdeal = "";

        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            if (line.startsWith("#")) {
                addAtom(index, atomName, element, charge);
                addIdealCoordinates(atomName, xIdeal, yIdeal, zIdeal);
                addDefaultCoordinates(atomName, xDefault, yDefault, zDefault);
                return;
            }
            String[] split = line.split("\\s+");
            if (split.length < 2) {
                // skip potential empty line
                continue;
            }
            String key = split[0];
            String value = split[1];
            if ("_chem_comp_atom.type_symbol".equals(key)) {
                element = value;
            } else if ("_chem_comp_atom.atom_id".equals(key)) {
                atomName = value;
            } else if ("_chem_comp_atom.charge".equals(key)) {
                charge = value;
            } else if ("_chem_comp_atom.model_Cartn_x".equals(key)) {
                xDefault = value;
            } else if ("_chem_comp_atom.model_Cartn_y".equals(key)) {
                yDefault = value;
            } else if ("_chem_comp_atom.model_Cartn_z".equals(key)) {
                zDefault = value;
            } else if ("_chem_comp_atom.pdbx_model_Cartn_x_ideal".equals(key)) {
                xIdeal = value;
            } else if ("_chem_comp_atom.pdbx_model_Cartn_y_ideal".equals(key)) {
                yIdeal = value;
            } else if ("_chem_comp_atom.pdbx_model_Cartn_z_ideal".equals(key)) {
                zIdeal = value;
            } else if ("_chem_comp_atom.pdbx_ordinal".equals(key)) {
                index = value;
            }
        }
    }

    private void addAtom(String indexString, String atomNameString, String elementString, String charge) {
        Element element = ElementProvider.getElementBySymbol(elementString)
                .orElse(ElementProvider.UNKOWN);
        if (!charge.equals("?")) {
            element.asIon(Integer.parseInt(charge));
        }
        String atomName = atomNameString.replace("\"", "");
        OakAtom atom = new OakAtom(Integer.parseInt(indexString), element, atomName);
        atoms.put(atomName, atom);
    }

    private void addIdealCoordinates(String atomName, String x, String y, String z) {
        if (x.equals("?") || y.equals("?") || z.equals("?")) {
            idealIncomplete = true;
            return;
        }
        idealCoordinates.put(atomName, new Vector3D(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)));
    }

    private void addDefaultCoordinates(String atomName, String x, String y, String z) {
        if (x.equals("?") || y.equals("?") || z.equals("?")) {
            defaultIncomplete = true;
            return;
        }
        defaultCoordinates.put(atomName, new Vector3D(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z)));
    }

    private void addBond(String firstAtom, String secondAtom, CovalentBondType bond) {
        bonds.put(new Pair<>(firstAtom.replace("\"", ""),
                        secondAtom.replace("\"", "")),
                bond);
    }

    /**
     * Extracts and creates atoms from the extracted lines.
     */
    private void extractAtoms() {
        for (String line : atomLines) {
            // take care of text in quotation marks
            line = replaceQuotation(line);
            String[] splitLine = line.split("\\s+");
            if (splitLine.length == 0) {
                continue;
            }
            // 1 = atom name, 3 = element, 4 = charge,
            // 9 = x coordinate, 10 = y coordinate, 11 = z coordinates,
            // 12 = ideal x coordinate, 13 = ideal y coordinate, 14 = ideal z coordinates,
            // 17 = identifer
            addAtom(splitLine[splitLine.length-1], splitLine[1], splitLine[3], splitLine[4]);
            addDefaultCoordinates( splitLine[1], splitLine[9], splitLine[10], splitLine[11]);
            addIdealCoordinates( splitLine[1], splitLine[12], splitLine[13], splitLine[14]);
        }
    }

    /**
     * Extracts the information required to create bonds.
     */
    private void extractBonds() {
        // for each of the collected bond lines
        for (String line : bondLines) {
            String[] splitLine = line.split("\\s+");
            // 1 = first atom, 2 = second atom, 3 = bond type
            if (splitLine.length == 0) {
                continue;
            }
            addBond(splitLine[1], splitLine[2], CovalentBondType.getBondForCifString(splitLine[3]));
        }
    }

    private void extractLigandName(String currentLine, ListIterator<String> lineIterator) {
        // extract compound name
        if (currentLine.startsWith("_chem_comp.name")) {
            // option 1: inline name
            name = extractValue(currentLine);
            if (name.isEmpty()) {
                // option 2: multi line name
                StringJoiner assembledName = new StringJoiner("");
                while (lineIterator.hasNext()) {
                    currentLine = lineIterator.next();
                    if (currentLine.startsWith("_")) {
                        name = assembledName.toString();
                        lineIterator.previous();
                        break;
                    }
                    if (currentLine.startsWith(";")) {
                        if (currentLine.length() == 1) {
                            // last line is empty
                            name = assembledName.toString();
                            break;
                        }
                        // start of multiline name
                        assembledName.add(currentLine.replaceAll("\"", "").replaceFirst(";", ""));
                    }
                    if (currentLine.startsWith("\"")) {
                        // start of multiline name
                        assembledName.add(currentLine.replaceAll("\"", ""));
                        if (currentLine.endsWith("\"")) {
                            // last line is empty
                            name = assembledName.toString();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Extracts information about the ligand.
     *
     * @param line The line.
     */
    private void extractInformation(String line) {
        // extract compound type
        if (line.startsWith("_chem_comp.type")) {
            type = extractValue(line);
        }
        // extract one letter code
        if (line.startsWith("_chem_comp.one_letter_code")) {
            oneLetterCode = extractValue(line);
        }
        // extract three letter code
        if (line.startsWith("_chem_comp.id")) {
            threeLetterCode = extractValue(line);
        }
        // extract parent
        if (line.startsWith("_chem_comp.mon_nstd_parent_comp_id")) {
            parent = extractValue(line);
        }
    }

    /**
     * Parses a leaf from scratch using only information provided in the cif file.
     *
     * @return A leaf.
     */
    private OakLeafSubstructure<?> parseCompleteLeafSubstructure() {
        collectLines();
        extractAtoms();
        extractBonds();
        return createLeafSubstructure(LeafIdentifier.fromSimpleString("A-1"));
    }

    /**
     * Creates a complete {@link LeafSubstructure} using the information collected until the call of this method.
     *
     * @param leafIdentifier The identifier this leaf should have.
     * @return A complete {@link LeafSubstructure} using the information collected until the call of this method.
     */
    private OakLeafSubstructure<?> createLeafSubstructure(LeafIdentifier leafIdentifier) {
        OakLeafSubstructure<?> leafSubstructure;
        if (isNucleotide()) {
            // check for nucleotides
            Optional<NucleotideFamily> nucleotideFamily = NucleotideFamily.getNucleotideByThreeLetterCode(parent);
            leafSubstructure = nucleotideFamily.map(nucleotideFamily1 -> new OakNucleotide(leafIdentifier, nucleotideFamily1, threeLetterCode))
                    .orElseGet(() -> new OakNucleotide(leafIdentifier, NucleotideFamily.getNucleotideByThreeLetterCode(threeLetterCode).orElseThrow(() ->
                            new IllegalArgumentException("Could not create Nucleotide with three letter code" + threeLetterCode))));

        } else if (isAminoAcid()) {
            // check for amino acids
            Optional<AminoAcidFamily> aminoAcidFamily = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(parent);
            leafSubstructure = aminoAcidFamily.map(aminoAcidFamily1 -> new OakAminoAcid(leafIdentifier, aminoAcidFamily1, threeLetterCode))
                    .orElseGet(() -> new OakAminoAcid(leafIdentifier, AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(threeLetterCode).orElseThrow(() ->
                            new IllegalArgumentException("Could not create Nucleotide with three letter code" + threeLetterCode))));
        } else {
            // else this is a ligand
            OakLigand OakLigand = new OakLigand(leafIdentifier, new LigandFamily(oneLetterCode, threeLetterCode));
            OakLigand.setName(name);
            leafSubstructure = OakLigand;
        }

        if (!idealIncomplete) {
            for (Map.Entry<String, OakAtom> entry : atoms.entrySet()) {
                String name = entry.getKey();
                OakAtom atom = entry.getValue();
                atom.setPosition(idealCoordinates.get(name));
            }
        } else if (!defaultIncomplete) {
            logger.warn("unable to assign ideal coordinates to ligand {}, using default coordinates.", threeLetterCode);
            for (Map.Entry<String, OakAtom> entry : atoms.entrySet()) {
                String name = entry.getKey();
                OakAtom atom = entry.getValue();
                atom.setPosition(defaultCoordinates.get(name));
            }
        } else {
            // both are incomplete
            logger.warn("unable to assign full coordinates to ligand {}", threeLetterCode);
            for (Map.Entry<String, OakAtom> entry : atoms.entrySet()) {
                String name = entry.getKey();
                OakAtom atom = entry.getValue();
                Vector3D idealPosition = idealCoordinates.get(name);
                if (idealPosition != null) {
                    atom.setPosition(idealPosition);
                } else {
                    atom.setPosition(new Vector3D(Double.NaN, Double.NaN, Double.NaN));
                    logger.warn("could not set coordinate for {} in {}.", name, threeLetterCode);
                }
            }
        }

        atoms.values().forEach(leafSubstructure::addAtom);
        connectAtoms(leafSubstructure);
        return leafSubstructure;
    }

    /**
     * Creates a leaf skeleton to be used to create complete leafs from.
     *
     * @return A leaf skeleton.
     */
    private LeafSkeleton parseLeafSkeleton() {
        collectLines();
        extractAtoms();
        extractBonds();
        return createLeafSkeleton();
    }

    /**
     * Creates a leaf skeleton only containing the information required to build a new leaf.
     *
     * @return A leaf skeleton.
     */
    private LeafSkeleton createLeafSkeleton() {
        LeafSkeleton.AssignedFamily assignedFamily;
        if (isNucleotide()) {
            // check for nucleotides
            if (!parent.equals(LeafSkeleton.DEFAULT_PARENT)) {
                assignedFamily = LeafSkeleton.AssignedFamily.MODIFIED_NUCLEOTIDE;
            } else {
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
        LeafSkeleton leafSkeleton = new LeafSkeleton(threeLetterCode, parent, assignedFamily, bonds);
        leafSkeleton.setAtoms(atoms);
        leafSkeleton.setName(name);
        leafSkeleton.setInchi(inchi);
        return leafSkeleton;
    }

    /**
     * Returns whether this molecule can be considered as a {@link Nucleotide}. This checks if the type is either {@code
     * RNA LINKING} or {@code DNA LINKING}.
     *
     * @return True if the entity is a nucleotide.
     */
    private boolean isNucleotide() {
        return type.equalsIgnoreCase("RNA LINKING") || type.equalsIgnoreCase("DNA LINKING");
    }

    /**
     * Returns whether this molecule can be considered as a {@link AminoAcid}. This checks if the type is {@code
     * L-PEPTIDE LINKING} and a valid parent is specified.
     *
     * @return True if entity is amino acid.
     */
    private boolean isAminoAcid() {
        return type.equalsIgnoreCase("L-PEPTIDE LINKING"); // && AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(this.parent).isPresent();
    }

    /**
     * Connects the atoms in the leaf as specified in the bonds map.
     *
     * @param leafWithAtoms The leaf to connect.
     */
    private void connectAtoms(OakLeafSubstructure<?> leafWithAtoms) {
        int bondCounter = 0;
        for (Map.Entry<Pair<String>, CovalentBondType> bond : bonds.entrySet()) {
            OakBond oakBond = new OakBond(bondCounter++, bond.getValue());
            leafWithAtoms.addBondBetween(oakBond, atoms.get(bond.getKey().getFirst()),
                    atoms.get(bond.getKey().getSecond()));
        }
    }

}
