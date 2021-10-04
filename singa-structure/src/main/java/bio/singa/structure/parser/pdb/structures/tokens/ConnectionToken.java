package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;
import bio.singa.structure.model.oak.UniqueAtomIdentifier;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.oak.OakAtom;
import bio.singa.structure.model.oak.OakLeafSubstructure;
import bio.singa.structure.model.oak.OakStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public enum ConnectionToken implements PDBToken {

    RECORD_TYPE(Range.of(1, 6), Justification.LEFT),
    CONNECTION_SOURCE_ATOM(Range.of(7, 11), Justification.RIGHT),
    CONNECTION_TARGET_ATOM_1(Range.of(12, 16), Justification.RIGHT),
    CONNECTION_TARGET_ATOM_2(Range.of(17, 21), Justification.RIGHT),
    CONNECTION_TARGET_ATOM_3(Range.of(22, 26), Justification.RIGHT),
    CONNECTION_TARGET_ATOM_4(Range.of(27, 31), Justification.RIGHT);

    public static final Pattern RECORD_PATTERN = Pattern.compile("^(CONECT).*");
    private static final Logger logger = LoggerFactory.getLogger(ConnectionToken.class);
    private static final Pattern numericPattern = Pattern.compile("\\d+");
    private final Range<Integer> columns;
    private final Justification justification;

    ConnectionToken(Range<Integer> columns, Justification justification) {
        this.columns = columns;
        this.justification = justification;
    }

    /**
     * Assigns connections as graph edges in the structure.
     * FIXME: double bonds up if they occur multiple times
     *
     * @param connectionLine
     * @param structure
     */
    public static void assignConnections(OakStructure structure, String connectionLine) {
        String sourceAtomString = CONNECTION_SOURCE_ATOM.extract(connectionLine);
        Map.Entry<UniqueAtomIdentifier, OakAtom> uniqueAtomEntry;
        if (isNumeric(sourceAtomString)) {
            Optional<Map.Entry<UniqueAtomIdentifier, OakAtom>> uniqueAtomEntryOptional = structure.getUniqueAtomEntry(Integer.parseInt(sourceAtomString));
            if (!uniqueAtomEntryOptional.isPresent()) {
                logger.warn("could not add connection for atom {}, source atom could not be found in the structure", sourceAtomString);
                return;
            }
            uniqueAtomEntry = uniqueAtomEntryOptional.get();
        } else {
            logger.warn("could not add connection for atom {}, invalid atom identifier", sourceAtomString);
            return;
        }
        UniqueAtomIdentifier atomIdentifier = uniqueAtomEntry.getKey();
        Optional<LeafSubstructure> leafSubstructureOptional = structure.getLeafSubstructure(atomIdentifier.getLeafIdentifier());
        if (!leafSubstructureOptional.isPresent()) {
            logger.warn("could not add connection for leaf {}, leaf could not be found in the structure", atomIdentifier.getLeafIdentifier());
            return;
        }
        OakLeafSubstructure leafsubstructure = ((OakLeafSubstructure) leafSubstructureOptional.get());
        OakAtom sourceAtom = ((OakAtom) uniqueAtomEntry.getValue());

        String firstTargetAtomString = CONNECTION_TARGET_ATOM_1.extract(connectionLine);
        addBond(structure, leafsubstructure, sourceAtom, firstTargetAtomString);

        String secondTargetAtomString = CONNECTION_TARGET_ATOM_2.extract(connectionLine);
        addBond(structure, leafsubstructure, sourceAtom, secondTargetAtomString);

        String thirdTargetAtomString = CONNECTION_TARGET_ATOM_3.extract(connectionLine);
        addBond(structure, leafsubstructure, sourceAtom, thirdTargetAtomString);

        String fourthTargetAtomString = CONNECTION_TARGET_ATOM_4.extract(connectionLine);
        addBond(structure, leafsubstructure, sourceAtom, fourthTargetAtomString);

    }

    private static void addBond(OakStructure structure, OakLeafSubstructure leafsubstructure, OakAtom sourceAtom, String targetAtomString) {
        Optional<OakAtom> targetOptional = extractAtom(structure, leafsubstructure, targetAtomString);
        if (targetOptional.isPresent()) {
            OakAtom targetAtom = targetOptional.get();
            if (!leafsubstructure.hasBond(sourceAtom, targetAtom)) {
                leafsubstructure.addBondBetween(sourceAtom, targetAtom);
            }
        }
    }

    public static Optional<OakAtom> extractAtom(OakStructure structure, OakLeafSubstructure leafsubstructure, String targetAtomString) {
        if (isNumeric(targetAtomString)) {
            // FIXME it is possible the the target of the connection is referenced in another leaf
            int targetAtom = Integer.parseInt(targetAtomString);
            Optional<OakAtom> targetOptionAtom = leafsubstructure.getAtom(targetAtom);
            if (!targetOptionAtom.isPresent()) {
                Optional<OakAtom> optionalBackupAtom = structure.getAtom(targetAtom);
                if (optionalBackupAtom.isPresent()) {
                    return Optional.of(((OakAtom) optionalBackupAtom.get()));
                }
                logger.warn("could not add connection for atom {}, target atom could not be found in the structure", targetAtomString);
                return Optional.empty();
            } else {
                return Optional.of(((OakAtom) targetOptionAtom.get()));
            }
        } else {
            return Optional.empty();
        }
    }

    public static String assemblePDBLines(OakLeafSubstructure leafsubstructure) {
        StringBuilder builder = new StringBuilder();
        for (Atom atom : leafsubstructure.getAllAtoms()) {
            OakAtom firstAtom = (OakAtom) atom;
            List<OakAtom> connectedAtoms = leafsubstructure.getBonds().stream()
                    // determine all bonds where the atom is involved in
                    .filter(oakBond -> oakBond.getSource().equals(firstAtom) || oakBond.getTarget().equals(firstAtom))
                    // determine the atoms that are NOT the source atom
                    .map(oakBond -> {
                        if (oakBond.getSource().equals(firstAtom)) {
                            return oakBond.getTarget();
                        } else {
                            return oakBond.getSource();
                        }
                    }).sorted(Comparator.comparingInt(OakAtom::getAtomIdentifier))
                    .collect(Collectors.toList());
            if (connectedAtoms.isEmpty()) {
                // FIXME this will skip entire CONECT atom record generation of a single dangling atom is in the ligand, e.g. terminal OXT for MD-derived peptides
                return "";
            }
            if (connectedAtoms.size() > 4) {
                logger.warn("more than 4 atoms are connected to {}", atom);
            }
            // concatenate information
            builder.append(RECORD_TYPE.createTokenString("CONECT"))
                    .append(CONNECTION_SOURCE_ATOM.createTokenString(String.valueOf(atom.getAtomIdentifier())));
            builder.append(CONNECTION_TARGET_ATOM_1.createTokenString(String.valueOf(connectedAtoms.get(0).getAtomIdentifier())));
            if (connectedAtoms.size() > 1) {
                builder.append(CONNECTION_TARGET_ATOM_2.createTokenString(String.valueOf(connectedAtoms.get(1).getAtomIdentifier())));
            }
            if (connectedAtoms.size() > 2) {
                builder.append(CONNECTION_TARGET_ATOM_3.createTokenString(String.valueOf(connectedAtoms.get(2).getAtomIdentifier())));
            }
            if (connectedAtoms.size() > 3) {
                builder.append(CONNECTION_TARGET_ATOM_4.createTokenString(String.valueOf(connectedAtoms.get(3).getAtomIdentifier())));
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null || strNum.isEmpty()) {
            return false;
        }
        return numericPattern.matcher(strNum).matches();
    }

    @Override
    public Range<Integer> getColumns() {
        return columns;
    }

    @Override
    public Pattern getRecordNamePattern() {
        return RECORD_PATTERN;
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
