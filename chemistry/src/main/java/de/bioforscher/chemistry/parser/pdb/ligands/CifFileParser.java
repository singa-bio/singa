package de.bioforscher.chemistry.parser.pdb.ligands;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.families.LigandFamily;
import de.bioforscher.chemistry.physical.families.NucleotideFamily;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
import de.bioforscher.chemistry.physical.leafes.Nucleotide;
import de.bioforscher.chemistry.physical.model.BondType;
import de.bioforscher.chemistry.physical.model.LeafIdentifier;
import de.bioforscher.core.utility.Pair;

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

    private Map<String,Atom> atoms;
    private Map<Pair<String>, String> bonds;

    private CifFileParser (List<String> lines) {
        this.lines = lines;
        this.bonds =  new HashMap<>();
    }


    public static LeafSubstructure<?, ?> parseLeafSubstructureFromCif(List<String> lines) {

        return null;
    }

    public static LeafSubstructure<?, ?> parseLeafSubstructureFromCif(List<String> lines, Map<String, Atom> atoms, LeafIdentifier identifier) {
        CifFileParser parser = new CifFileParser(lines);
        return parser.assambleLeaf(atoms, identifier);
    }

    private LeafSubstructure<?, ?> assambleLeaf(Map<String, Atom> atoms, LeafIdentifier leafIdentifier) {
        this.atoms = atoms;
        boolean bondSection = false;
        // extract information
        for (String line : this.lines) {
            // extract compound name
            if (line.startsWith("_chem_comp.name")) {
                // this.name = extractValue(line);
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
                // 1 = first atom, 2 = second atom, 3 = bond type
                String[] splitLine = line.split("\\s+");
                this.bonds.put(new Pair<>(splitLine[1].replace("\"", ""), splitLine[2].replace("\"", "")), splitLine[3]);
            }
        }
        // create leaf
        // check for nucleotides
        LeafSubstructure<?,?> leafSubstructure = null;
        if (this.type.equals("RNA LINKING") || this.type.equals("DNA LINKING")) {
            if (!this.parent.equals("?")) {
                NucleotideFamily nucleotideFamily = NucleotideFamily.getNucleotideByThreeLetterCode(this.parent)
                        .orElse(NucleotideFamily.UNKNOWN);
                leafSubstructure = new Nucleotide(leafIdentifier, nucleotideFamily, this.threeLetterCode);
                atoms.values().forEach(leafSubstructure::addNode);
                connectAtoms(leafSubstructure);
            }
        }

        return leafSubstructure;
    }

    private void connectAtoms(LeafSubstructure leafWithAtoms) {
        for (Map.Entry<Pair<String>, String> bond : this.bonds.entrySet()) {
            leafWithAtoms.addEdgeBetween(this.atoms.get(bond.getKey().getFirst()),
                    this.atoms.get(bond.getKey().getSecond()),
                    BondType.getBondTypeByCifName(bond.getValue()).orElse(BondType.SINGLE_BOND));
        }
    }

    private static String extractValue(String line) {
        return line.substring(DEFAULT_VALUE_SPACING).replace("\"","").trim();
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
