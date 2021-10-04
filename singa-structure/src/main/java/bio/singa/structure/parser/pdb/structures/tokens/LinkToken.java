package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.pdb.PdbLeafSubstructure;
import bio.singa.structure.model.pdb.PdbLinkEntry;
import bio.singa.structure.model.pdb.PdbStructure;
import bio.singa.structure.model.pdb.PdbLeafIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
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

    private static final Logger logger = LoggerFactory.getLogger(LinkToken.class);
    public static final Pattern RECORD_PATTERN = Pattern.compile("^(LINK).*");

    private final Range<Integer> columns;
    private final Justification justification;

    LinkToken(Range<Integer> columns, Justification justification) {
        this.columns = columns;
        this.justification = justification;
    }

    public static PdbLinkEntry assembleLinkEntry(PdbStructure pdbStructure, String linkLine) {
        // process first atom
        String firstAtomName = FIRST_ATOM_NAME.extract(linkLine);
        String firstAtomChainIdentifier = FIRST_ATOM_CHAIN_IDENTIFIER.extract(linkLine);
        int firstAtomResidueSerial = Integer.parseInt(FIRST_ATOM_RESIDUE_SERIAL.extract(linkLine));
        String firstAtomInsertionCodeString = FIRST_ATOM_RESIDUE_INSERTION.extract(linkLine);
        PdbLeafIdentifier firstAtomLeafIdentifier = getLeafIdentifier(pdbStructure, firstAtomChainIdentifier, firstAtomResidueSerial, firstAtomInsertionCodeString);
        Optional<PdbLeafSubstructure> firstLeafSubstructureOptional = pdbStructure.getLeafSubstructure(firstAtomLeafIdentifier);
        if (!firstLeafSubstructureOptional.isPresent()) {
            logger.warn("unable to find {} for link creation", firstAtomLeafIdentifier);
            return null;
        }
        Atom firstAtom = getAtom(pdbStructure, firstAtomName, firstAtomLeafIdentifier);
        if (firstAtom == null) {
            return null;
        }
        // process second atom
        String secondAtomName = SECOND_ATOM_NAME.extract(linkLine);
        String secondAtomChainIdentifier = SECOND_ATOM_CHAIN_IDENTIFIER.extract(linkLine);
        int secondAtomResidueSerial = Integer.parseInt(SECOND_ATOM_RESIDUE_SERIAL.extract(linkLine));
        String secondAtomInsertionCodeString = SECOND_ATOM_RESIDUE_INSERTION.extract(linkLine);
        PdbLeafIdentifier secondAtomLeafIdentifier = getLeafIdentifier(pdbStructure, secondAtomChainIdentifier, secondAtomResidueSerial, secondAtomInsertionCodeString);
        Optional<PdbLeafSubstructure> secondLeafSubstructureOptional = pdbStructure.getLeafSubstructure(secondAtomLeafIdentifier);
        if (!secondLeafSubstructureOptional.isPresent()) {
            logger.warn("unable to find {} for link creation", secondAtomLeafIdentifier);
            return null;
        }
        Atom secondAtom = getAtom(pdbStructure, secondAtomName, secondAtomLeafIdentifier);
        if (secondAtom == null) {
            return null;
        }
        return new PdbLinkEntry(firstLeafSubstructureOptional.get(), firstAtom, secondLeafSubstructureOptional.get(), secondAtom);
    }

    public static String assemblePDBLine(PdbLinkEntry link) {
        StringBuilder sb = new StringBuilder();
        sb.append(RECORD_TYPE.createTokenString("LINK"))
                .append("      ")
                .append(formatAtomName(link.getFirstAtom()))
                .append(" ") // ALTERNATE_LOCATION_INDICATOR not yet implemented
                .append(FIRST_ATOM_RESIDUE_NAME.createTokenString(link.getFirstLeafSubstructure().getThreeLetterCode()))
                .append(" ")
                .append(link.getFirstLeafSubstructure().getIdentifier().getChainIdentifier())
                .append(FIRST_ATOM_RESIDUE_SERIAL.createTokenString(String.valueOf(link.getFirstLeafSubstructure().getIdentifier().getSerial())))
                .append(FIRST_ATOM_RESIDUE_INSERTION.createTokenString(String.valueOf(
                        link.getFirstLeafSubstructure().getIdentifier().getInsertionCode() == PdbLeafIdentifier.DEFAULT_INSERTION_CODE
                                ? " " : link.getFirstLeafSubstructure().getIdentifier().getInsertionCode())))
                .append("               ")
                .append(formatAtomName(link.getSecondAtom()))
                .append(" ") // ALTERNATE_LOCATION_INDICATOR not yet implemented
                .append(SECOND_ATOM_RESIDUE_NAME.createTokenString(link.getSecondLeafSubstructure().getThreeLetterCode()))
                .append(" ")
                .append(link.getSecondLeafSubstructure().getIdentifier().getChainIdentifier())
                .append(SECOND_ATOM_RESIDUE_SERIAL.createTokenString(String.valueOf(link.getSecondLeafSubstructure().getIdentifier().getSerial())))
                .append(SECOND_ATOM_RESIDUE_INSERTION.createTokenString(String.valueOf(
                        link.getSecondLeafSubstructure().getIdentifier().getInsertionCode() == PdbLeafIdentifier.DEFAULT_INSERTION_CODE
                                ? " " : link.getSecondLeafSubstructure().getIdentifier().getInsertionCode())));


        return PDBToken.endLine(sb.toString());
    }

    private static Atom getAtom(PdbStructure oakStructure, String atomName, PdbLeafIdentifier leafIdentifier) {
        Optional<PdbLeafSubstructure> leafSubstructureOptional = oakStructure.getLeafSubstructure(leafIdentifier);
        if (!leafSubstructureOptional.isPresent()) {
            logger.warn("unable to find {} for link creation", leafIdentifier);
            return null;
        }
        Optional<? extends Atom> optionalAtom = leafSubstructureOptional.get().getAtomByName(atomName);
        if (!optionalAtom.isPresent()) {
            logger.warn("unable to find {} in {} for link creation", atomName, leafIdentifier);
            return null;
        }
        return optionalAtom.get();
    }

    private static PdbLeafIdentifier getLeafIdentifier(PdbStructure oakStructure, String chainIdentifier, int residueSerial, String insertionCodeString) {
        char firstAtomInsertionCode = insertionCodeString.isEmpty() ? PdbLeafIdentifier.DEFAULT_INSERTION_CODE : insertionCodeString.charAt(0);
        return new PdbLeafIdentifier(oakStructure.getStructureIdentifier(), oakStructure.getFirstModel().getModelIdentifier(), chainIdentifier, residueSerial, firstAtomInsertionCode);
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
