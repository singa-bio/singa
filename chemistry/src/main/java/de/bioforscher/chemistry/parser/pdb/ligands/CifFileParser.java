package de.bioforscher.chemistry.parser.pdb.ligands;

import de.bioforscher.chemistry.physical.leafes.LeafSubstructure;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leberech on 19/12/16.
 */
public class CifFileParser {

    public static LeafSubstructure<?,?> parseLeafSubstructureFromCif(List<String> lines) {

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
