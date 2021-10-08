package bio.singa.structure.io.general;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringJoiner;

/**
 * @author cl
 */
public class LocalCcdRepository {

    /**
     * The default folder structure of local PDBeChem installations.
     */
    static final Path BASE_PATH_PDBE_CIF = Paths.get("pub/databases/msd/pdbechem_v2/");

    private final Path localPath;

    /**
     * Creates a new reference for a local pdb installation.
     *
     * @param localPdbLocation The location of the local PDBeChem installation.
     */
    public LocalCcdRepository(String localPdbLocation) {
        this(localPdbLocation, BASE_PATH_PDBE_CIF);
    }

    /**
     * Creates a new reference for a local pdb installation.
     *
     * @param localPdbLocation The location of the local PDBeChem installation.
     * @param basePathPdb The base PDBeChem path if different from pub/databases/msd/pdbechem_v2/
     */
    public LocalCcdRepository(String localPdbLocation, Path basePathPdb) {
        localPath = Paths.get(localPdbLocation).resolve(basePathPdb);
    }

    /**
     * Returns the path to the local pdb.
     *
     * @return The path to the local pdb.
     */
    public Path getLocalPath() {
        return localPath;
    }


    /**
     * Returns the full path of a given ligand id in respect to the local PDB copy.
     *
     * @param ligandIdentifier The three letter ligand identifier for which the full path should be retrieved.
     * @return The full path of the given ligand id
     */
    public Path getPathForLigandIdentifier(String ligandIdentifier) {
        ligandIdentifier = ligandIdentifier.toUpperCase();
        return localPath
                // split directory location
                .resolve(String.valueOf(ligandIdentifier.charAt(0)))
                // ligand directory
                .resolve(ligandIdentifier)
                // cif file
                .resolve(ligandIdentifier+".cif");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LocalCcdRepository.class.getSimpleName() + "[", "]")
                .add("localPath=" + localPath)
                .toString();
    }
}
