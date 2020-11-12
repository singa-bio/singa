package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;
import bio.singa.features.identifiers.LeafIdentifier;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.oak.LinkEntry;
import bio.singa.structure.model.oak.OakStructure;
import bio.singa.structure.parser.pdb.structures.StructureParserException;

import java.util.regex.Pattern;

import static bio.singa.structure.parser.pdb.structures.tokens.AtomToken.formatAtomName;

/**
 * The LINK records specify connectivity between residues that is not implied by the primary structure. Connectivity is
 * expressed in terms of the atom names. They also include the distance associated with the each linkage following the
 * symmetry operations at the end of each record.
 *
 * @author cl
 */
public enum LinkToken implements PDBToken {

    RECORD_TYPE(Range.of(1, 6), Justification.LEFT),
    FIRST_ATOM_NAME(Range.of(13, 16), Justification.LEFT),
    FIRST_ATOM_ALTERNATE_LOCATION_INDICATOR(Range.of(17), Justification.LEFT),
    FIRST_ATOM_RESIDUE_NAME(Range.of(18, 20), Justification.RIGHT),
    FIRST_ATOM_CHAIN_IDENTIFIER(Range.of(22), Justification.LEFT),
    FIRST_ATOM_RESIDUE_SERIAL(Range.of(23, 26), Justification.RIGHT),
    FIRST_ATOM_RESIDUE_INSERTION(Range.of(27), Justification.LEFT),
    SECOND_ATOM_NAME(Range.of(43, 46), Justification.LEFT),
    SECOND_ATOM_ALTERNATE_LOCATION_INDICATOR(Range.of(47), Justification.LEFT),
    SECOND_ATOM_RESIDUE_NAME(Range.of(48, 50), Justification.RIGHT),
    SECOND_ATOM_CHAIN_IDENTIFIER(Range.of(52), Justification.LEFT),
    SECOND_ATOM_RESIDUE_SERIAL(Range.of(53, 56), Justification.RIGHT),
    SECOND_ATOM_RESIDUE_INSERTION(Range.of(57), Justification.LEFT),
    FIRST_ATOM_SYMMETRY_OPERATOR(Range.of(60, 65), Justification.RIGHT),
    SECOND_ATOM_SYMMETRY_OPERATOR(Range.of(67, 72), Justification.RIGHT),
    DISTANCE(Range.of(74, 78), Justification.RIGHT);


    public static final Pattern RECORD_PATTERN = Pattern.compile("^(LINK).*");

    private final Range<Integer> columns;
    private final Justification justification;

    LinkToken(Range<Integer> columns, Justification justification) {
        this.columns = columns;
        this.justification = justification;
    }

    public static LinkEntry assembleLinkEntry(OakStructure oakStructure, String linkLine) {
        // process first atom
        String firstAtomName = FIRST_ATOM_NAME.extract(linkLine);
        String firstAtomChainIdentifier = FIRST_ATOM_CHAIN_IDENTIFIER.extract(linkLine);
        int firstAtomResidueSerial = Integer.parseInt(FIRST_ATOM_RESIDUE_SERIAL.extract(linkLine));
        String firstAtomInsertionCodeString = FIRST_ATOM_RESIDUE_INSERTION.extract(linkLine);
        LeafIdentifier firstAtomLeafIdentifier = getLeafIdentifier(oakStructure, firstAtomChainIdentifier, firstAtomResidueSerial, firstAtomInsertionCodeString);
        LeafSubstructure<?> firstLeafSubstructure = oakStructure.getLeafSubstructure(firstAtomLeafIdentifier)
                .orElseThrow(() -> new StructureParserException("unable to find " + firstAtomLeafIdentifier + " for link creation"));
        Atom firstAtom = getAtom(oakStructure, firstAtomName, firstAtomLeafIdentifier);
        // process second atom
        String secondAtomName = SECOND_ATOM_NAME.extract(linkLine);
        String secondAtomChainIdentifier = SECOND_ATOM_CHAIN_IDENTIFIER.extract(linkLine);
        int secondAtomResidueSerial = Integer.parseInt(SECOND_ATOM_RESIDUE_SERIAL.extract(linkLine));
        String secondAtomInsertionCodeString = SECOND_ATOM_RESIDUE_INSERTION.extract(linkLine);
        LeafIdentifier secondAtomLeafIdentifier = getLeafIdentifier(oakStructure, secondAtomChainIdentifier, secondAtomResidueSerial, secondAtomInsertionCodeString);
        LeafSubstructure<?> secondLeafSubstructure = oakStructure.getLeafSubstructure(secondAtomLeafIdentifier)
                .orElseThrow(() -> new StructureParserException("unable to find " + secondAtomLeafIdentifier + " for link creation"));
        Atom secondAtom = getAtom(oakStructure, secondAtomName, secondAtomLeafIdentifier);

        return new LinkEntry(firstLeafSubstructure, firstAtom, secondLeafSubstructure, secondAtom);
    }

    public static String assemblePDBLine(LinkEntry link) {
        StringBuilder sb = new StringBuilder();
        sb.append(RECORD_TYPE.createTokenString("LINK"))
                .append("      ")
                .append(formatAtomName(link.getFirstAtom()))
                .append(" ") // ALTERNATE_LOCATION_INDICATOR not yet implemented
                .append(FIRST_ATOM_RESIDUE_NAME.createTokenString(link.getFirstLeafSubstructure().getThreeLetterCode().toUpperCase()))
                .append(" ")
                .append(link.getFirstLeafSubstructure().getChainIdentifier())
                .append(FIRST_ATOM_RESIDUE_SERIAL.createTokenString(String.valueOf(link.getFirstLeafSubstructure().getIdentifier().getSerial())))
                .append(FIRST_ATOM_RESIDUE_INSERTION.createTokenString(String.valueOf(
                        link.getFirstLeafSubstructure().getInsertionCode() == LeafIdentifier.DEFAULT_INSERTION_CODE
                                ? " " : link.getFirstLeafSubstructure().getInsertionCode())))
                .append("               ")
                .append(formatAtomName(link.getSecondAtom()))
                .append(" ") // ALTERNATE_LOCATION_INDICATOR not yet implemented
                .append(SECOND_ATOM_RESIDUE_NAME.createTokenString(link.getSecondLeafSubstructure().getThreeLetterCode().toUpperCase()))
                .append(" ")
                .append(link.getSecondLeafSubstructure().getChainIdentifier())
                .append(SECOND_ATOM_RESIDUE_SERIAL.createTokenString(String.valueOf(link.getSecondLeafSubstructure().getIdentifier().getSerial())))
                .append(SECOND_ATOM_RESIDUE_INSERTION.createTokenString(String.valueOf(
                        link.getSecondLeafSubstructure().getInsertionCode() == LeafIdentifier.DEFAULT_INSERTION_CODE
                                ? " " : link.getSecondLeafSubstructure().getInsertionCode())));


        return PDBToken.endLine(sb.toString());
    }

    private static Atom getAtom(OakStructure oakStructure, String atomName, LeafIdentifier leafIdentifier) {
        LeafSubstructure<?> firstLeafSubstructure = oakStructure.getLeafSubstructure(leafIdentifier)
                .orElseThrow(() -> new StructureParserException("unable to find " + leafIdentifier + " for link creation"));
        return firstLeafSubstructure.getAtomByName(atomName)
                .orElseThrow(() -> new StructureParserException("unable to find " + atomName + " in " + leafIdentifier + " for link creation"));
    }

    private static LeafIdentifier getLeafIdentifier(OakStructure oakStructure, String chainIdentifier, int residueSerial, String insertionCodeString) {
        char firstAtomInsertionCode = insertionCodeString.isEmpty() ? LeafIdentifier.DEFAULT_INSERTION_CODE : insertionCodeString.charAt(0);
        return new LeafIdentifier(oakStructure.getPdbIdentifier(), oakStructure.getFirstModel().getModelIdentifier(), chainIdentifier, residueSerial, firstAtomInsertionCode);
    }

    @Override
    public java.util.regex.Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

    @Override
    public Range<Integer> getColumns() {
        return columns;
    }

    private String createTokenString(String content) {
        int totalLength = columns.getUpperBound() - columns.getLowerBound() - content.length();
        StringBuilder filler = new StringBuilder();
        for (int i = 0; i < totalLength + 1; i++) {
            filler.append(" ");
        }
        if (justification == Justification.LEFT) {
            return content + filler;
        }
        return filler + content;
    }

}