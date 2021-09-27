package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;
import bio.singa.structure.model.families.AminoAcidFamily;
import bio.singa.structure.model.families.NucleotideFamily;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.oak.OakAminoAcid;
import bio.singa.structure.model.oak.OakNucleotide;
import bio.singa.structure.model.oak.OakStructure;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static bio.singa.structure.model.oak.LeafIdentifier.DEFAULT_INSERTION_CODE;

public enum SequenceAdviceToken implements PDBToken {

    RECORD_TYPE(Range.of(1, 6), Justification.LEFT),
    IDENTIFIER(Range.of(8, 11), Justification.RIGHT),
    STRUCTURE_RESIDUE_NAME(Range.of(13, 15), Justification.RIGHT),
    STRUCTURE_CHAIN_IDENTIFIER(Range.of(17), Justification.LEFT),
    STRUCTURE_RESIDUE_SERIAL(Range.of(19, 22), Justification.RIGHT),
    STRUCTURE_RESIDUE_INSERTION(Range.of(23), Justification.LEFT),
    DATABASE_NAME(Range.of(25, 28), Justification.RIGHT),
    DATABASE_ACCESSION(Range.of(30, 38), Justification.RIGHT),
    DATABASE_RESIDUE_NAME(Range.of(40, 42), Justification.RIGHT),
    DATABASE_RESIDUE_SERIAL(Range.of(44, 48), Justification.RIGHT),
    CONFLICT_COMMENT(Range.of(50, 70), Justification.RIGHT);

    public static final Pattern RECORD_PATTERN = Pattern.compile("^SEQADV.*");

    private final Range<Integer> columns;
    private final Justification justification;

    SequenceAdviceToken(Range<Integer> columns, Justification justification) {
        this.columns = columns;
        this.justification = justification;
    }

    public static void assignSequenceAdvice(OakStructure structure, String line) {
        String conflictComment = CONFLICT_COMMENT.extract(line).trim().toUpperCase();

        // TODO also possible to determine expression tags, poly A, etc.
        if (!conflictComment.contains("MUTATION")) {
            return;
        }

        String adviceChain = STRUCTURE_CHAIN_IDENTIFIER.extract(line);
        int adviceSerial = Integer.parseInt(STRUCTURE_RESIDUE_SERIAL.extract(line));
        String insertion = STRUCTURE_RESIDUE_INSERTION.extract(line);
        char adviceInsertionCode = insertion.isEmpty() ? DEFAULT_INSERTION_CODE : insertion.charAt(0);

        List<LeafSubstructure<?>> advisableResidues = structure.getAllChains().stream()
                .filter(chain -> chain.getChainIdentifier().equals(adviceChain))
                .flatMap(chain -> chain.getAllLeafSubstructures().stream())
                .filter(leaf -> leaf.getSerial().equals(adviceSerial) && leaf.getInsertionCode() == adviceInsertionCode)
                .collect(Collectors.toList());

        String originalResidueName = DATABASE_RESIDUE_NAME.extract(line).trim();
        // assign aminoacids
        advisableResidues.stream()
                .filter(OakAminoAcid.class::isInstance)
                .map (OakAminoAcid.class::cast)
                .forEach(oakAminoAcid -> {
                    oakAminoAcid.setMutation(true);
                    AminoAcidFamily wildTypeResidue = AminoAcidFamily.getAminoAcidTypeByThreeLetterCode(originalResidueName)
                            .orElse(AminoAcidFamily.UNKNOWN);
                    oakAminoAcid.setWildTypeResidue(wildTypeResidue);
                });

        advisableResidues.stream()
                .filter(OakNucleotide.class::isInstance)
                .map (OakNucleotide.class::cast)
                .forEach(oakNucleotide -> {
                    oakNucleotide.setMutation(true);
                    NucleotideFamily wildTypeNucleotide = NucleotideFamily.getNucleotideByThreeLetterCode(originalResidueName)
                            .orElse(NucleotideFamily.UNKNOWN);
                    oakNucleotide.setWildTypeNucleotide(wildTypeNucleotide);
                });

    }

    @Override
    public Range<Integer> getColumns() {
        return columns;
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
    }

}
