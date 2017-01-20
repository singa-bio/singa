package de.bioforscher.chemistry.parser.pdb.ligands;

import de.bioforscher.chemistry.physical.atoms.Atom;
import de.bioforscher.chemistry.physical.families.LigandFamily;
import de.bioforscher.chemistry.physical.leafes.AtomContainer;
import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;
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



    public static LeafSubstructure<?, ?> parseLeafSubstructureFromCif(List<String> lines) {

        return null;
    }

    public static LeafSubstructure<?, ?> parseLeafSubstructureFromCif(List<String> lines, Map<String, Atom> atoms, LeafIdentifier identifier) {
        Map<Pair<String>, String> pairs = new HashMap<>();
        boolean bondSection = false;
        for (String line : lines) {
            // extract compound name (may use "...")
            if (line.startsWith("_chem_comp.name")) {

            }
            // extract compound type (may use "...")
            if (line.startsWith("_chem_comp.type")) {

            }
            // extract one letter code
            if (line.startsWith("_chem_comp.one_letter_code")) {

            }
            // extract three letter code
            if (line.startsWith("_chem_comp.three_letter_code")) {

            }
            // extract parent
            if (line.startsWith("_chem_comp.mon_nstd_parent_comp_id")) {

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
                String[] splitLine = line.split("\\W+");
                pairs.put(new Pair<>(splitLine[1], splitLine[2]), splitLine[3]);
            }
        }
        // TODO get
        // name, one letter code, three letter code, type
        // and connect bonds using the given atoms




        return null;
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
