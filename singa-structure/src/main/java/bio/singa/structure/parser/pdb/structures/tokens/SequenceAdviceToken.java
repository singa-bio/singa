package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;
import bio.singa.structure.model.families.StructuralFamilies;
import bio.singa.structure.model.families.StructuralFamily;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.pdb.PdbAminoAcid;
import bio.singa.structure.model.pdb.PdbNucleotide;
import bio.singa.structure.model.pdb.PdbStructure;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static bio.singa.structure.model.pdb.PdbLeafIdentifier.DEFAULT_INSERTION_CODE;

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

    public static void assignSequenceAdvice(PdbStructure structure, String line) {
        String conflictComment = CONFLICT_COMMENT.extract(line).trim().toUpperCase();

        // TODO also possible to determine expression tags, poly A, etc.
        if (!conflictComment.contains("MUTATION")) {
            return;
        }

        String adviceChain = STRUCTURE_CHAIN_IDENTIFIER.extract(line);
        int adviceSerial = Integer.parseInt(STRUCTURE_RESIDUE_SERIAL.extract(line));
        String insertion = STRUCTURE_RESIDUE_INSERTION.extract(line);
        char adviceInsertionCode = insertion.isEmpty() ? DEFAULT_INSERTION_CODE : insertion.charAt(0);

        List<LeafSubstructure> advisableResidues = structure.getAllChains().stream()
                .filter(chain -> chain.getChainIdentifier().equals(adviceChain))
                .flatMap(chain -> chain.getAllLeafSubstructures().stream())
                .filter(leaf -> leaf.getIdentifier().getSerial() == adviceSerial && leaf.getIdentifier().getInsertionCode() == adviceInsertionCode)
                .collect(Collectors.toList());

        String originalResidueName = DATABASE_RESIDUE_NAME.extract(line).trim();
        // assign aminoacids
        advisableResidues.stream()
                .filter(PdbAminoAcid.class::isInstance)
                .map (PdbAminoAcid.class::cast)
                .forEach(oakAminoAcid -> {
                    oakAminoAcid.setMutation(true);
                    StructuralFamily wildTypeResidue = StructuralFamilies.AminoAcids.getOrUnknown(originalResidueName);
                    oakAminoAcid.setWildTypeResidue(wildTypeResidue);
                });

        advisableResidues.stream()
                .filter(PdbNucleotide.class::isInstance)
                .map (PdbNucleotide.class::cast)
                .forEach(oakNucleotide -> {
                    oakNucleotide.setMutation(true);
                    StructuralFamily wildTypeNucleotide = StructuralFamilies.Nucleotides.getOrUnknown(originalResidueName);
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
