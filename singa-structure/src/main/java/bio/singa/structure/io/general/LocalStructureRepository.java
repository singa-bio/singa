package bio.singa.structure.io.general;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class represents a local PDB installation.
 */
public class LocalStructureRepository {

    /**
     * The default folder structure of local pdb installations.
     */
    static final Path BASE_PATH_PDB = Paths.get("data/structures/divided/");

    private final SourceLocation sourceLocation;

    /**
     * The path to the local pdb.
     */
    private final Path localPdbPath;

    /**
     * Creates a new reference for a local pdb installation.
     *
     * @param localPdbLocation The location of the local PDB installation.
     * @param sourceLocation The type of file used (either {@link SourceLocation#OFFLINE_MMTF} or {@link
     * SourceLocation#OFFLINE_PDB}).
     */
    public LocalStructureRepository(String localPdbLocation, SourceLocation sourceLocation) {
        this(localPdbLocation, sourceLocation, BASE_PATH_PDB);
    }

    /**
     * Creates a new reference for a local pdb installation.
     *
     * @param localPdbLocation The location of the local PDB installation.
     * @param sourceLocation The type of file used (either {@link SourceLocation#OFFLINE_MMTF} or {@link
     * SourceLocation#OFFLINE_PDB}).
     * @param basePathPdb The base PDB path if different from data/structures/divided/
     */
    public LocalStructureRepository(String localPdbLocation, SourceLocation sourceLocation, Path basePathPdb) {
        this.sourceLocation = sourceLocation;
        switch (sourceLocation) {
            case OFFLINE_MMTF:
                localPdbPath = Paths.get(localPdbLocation).resolve(basePathPdb).resolve("mmtf");
                break;
            case OFFLINE_PDB:
                localPdbPath = Paths.get(localPdbLocation).resolve(basePathPdb).resolve("pdb");
                break;
            case OFFLINE_MMCIF:
                localPdbPath = Paths.get(localPdbLocation).resolve(basePathPdb).resolve("pdb");
                break;
            default:
                throw new IllegalArgumentException("Source location mus be offline.");
        }

    }

    /**
     * Returns the path to the local pdb.
     *
     * @return The path to the local pdb.
     */
    public Path getLocalPdbPath() {
        return localPdbPath;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    /**
     * Returns the full path of a given PDB-ID in respect to the local PDB copy.
     *
     * @param pdbIdentifier The PDB-ID for which the full path should be retrieved.
     * @return The full path of the given PDB-ID.
     */
    public Path getPathForPdbIdentifier(String pdbIdentifier) {
        pdbIdentifier = pdbIdentifier.toLowerCase();
        final Path middleIdentifierPath = localPdbPath.resolve(pdbIdentifier.substring(1, 3));
        if (sourceLocation == SourceLocation.OFFLINE_PDB) {
            return middleIdentifierPath.resolve("pdb" + pdbIdentifier + ".ent.gz");
        }
        return middleIdentifierPath.resolve(pdbIdentifier + ".mmtf.gz");
    }

    @Override
    public String toString() {
        return "LocalPDB{" +
                "sourceLocation=" + sourceLocation +
                ", localPdbPath=" + localPdbPath +
                '}';
    }
}
