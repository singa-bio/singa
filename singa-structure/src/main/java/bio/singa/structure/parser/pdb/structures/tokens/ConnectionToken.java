package bio.singa.structure.parser.pdb.structures.tokens;

import bio.singa.core.utility.Range;
import bio.singa.features.identifiers.LeafIdentifier;
import bio.singa.features.identifiers.UniqueAtomIdentifer;
import bio.singa.structure.model.interfaces.Atom;
import bio.singa.structure.model.interfaces.LeafSubstructure;
import bio.singa.structure.model.oak.OakAtom;
import bio.singa.structure.model.oak.OakLeafSubstructure;
import bio.singa.structure.model.oak.OakStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

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
        Map.Entry<UniqueAtomIdentifer, Atom> uniqueAtomEntry;
        if (isNumeric(sourceAtomString)) {
            Optional<Map.Entry<UniqueAtomIdentifer, Atom>> uniqueAtomEntryOptional = structure.getUniqueAtomEntry(Integer.parseInt(sourceAtomString));
            if (!uniqueAtomEntryOptional.isPresent()) {
                logger.warn("structure contains a invalid CONECT record: connected atom not found");
                return;
            }
            uniqueAtomEntry = uniqueAtomEntryOptional.get();
        } else {
            logger.warn("structure contains a invalid CONECT record: no source atom");
            return;
        }
        UniqueAtomIdentifer atomIdentifer = uniqueAtomEntry.getKey();
        Optional<LeafSubstructure<?>> leafSubstructureOptional = structure.getLeafSubstructure(new LeafIdentifier(atomIdentifer.getPdbIdentifier(),
                atomIdentifer.getModelIdentifier(),
                atomIdentifer.getChainIdentifier(),
                atomIdentifer.getLeafSerial(),
                atomIdentifer.getLeafInsertionCode()));
        if (!leafSubstructureOptional.isPresent()) {
            logger.warn("structure contains a invalid CONECT record: no related leaf substructure found");
            return;
        }
        OakLeafSubstructure<?> leafsubstructure = ((OakLeafSubstructure<?>) leafSubstructureOptional.get());
        OakAtom sourceAtom = ((OakAtom) uniqueAtomEntry.getValue());

        String firstTargetAtomString = CONNECTION_TARGET_ATOM_1.extract(connectionLine);
        addBond(leafsubstructure, sourceAtom, firstTargetAtomString);

        String secondTargetAtomString = CONNECTION_TARGET_ATOM_2.extract(connectionLine);
        addBond(leafsubstructure, sourceAtom, secondTargetAtomString);

        String thirdTargetAtomString = CONNECTION_TARGET_ATOM_3.extract(connectionLine);
        addBond(leafsubstructure, sourceAtom, thirdTargetAtomString);

        String fourthTargetAtomString = CONNECTION_TARGET_ATOM_4.extract(connectionLine);
        addBond(leafsubstructure, sourceAtom, fourthTargetAtomString);

    }

    private static void addBond(OakLeafSubstructure<?> leafsubstructure, OakAtom sourceAtom, String fourthTargetAtomString) {
        Optional<OakAtom> fourthTargetOptional = extractAtom(leafsubstructure, fourthTargetAtomString);
        fourthTargetOptional.ifPresent(oakAtom -> {
            if (!leafsubstructure.hasBond(sourceAtom, oakAtom)) {
                leafsubstructure.addBondBetween(sourceAtom, oakAtom);
            }
        });
    }

    public static Optional<OakAtom> extractAtom(OakLeafSubstructure<?> leafsubstructure, String targetAtomString) {
        if (isNumeric(targetAtomString)) {
            // FIXME it is possible the the target of the connection is referenced in another leaf
            Optional<Atom> targetOptionAtom = leafsubstructure.getAtom(Integer.parseInt(targetAtomString));
            if (!targetOptionAtom.isPresent()) {
                logger.warn("structure contains a invalid CONECT record: connected atom not found");
                return Optional.empty();
            } else {
                return Optional.of(((OakAtom) targetOptionAtom.get()));
            }
        } else {
            return Optional.empty();
        }
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

}
