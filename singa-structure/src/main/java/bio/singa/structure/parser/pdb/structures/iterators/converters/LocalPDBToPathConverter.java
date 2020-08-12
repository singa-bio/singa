package bio.singa.structure.parser.pdb.structures.iterators.converters;

import bio.singa.structure.parser.pdb.structures.LocalPDBRepository;

import java.nio.file.Path;

/**
 * @author cl
 */
public class LocalPDBToPathConverter implements ContentConverter<String, Path> {

    private LocalPDBToPathConverter(LocalPDBRepository localPDB) {
        this.localPDB = localPDB;
    }

    private final LocalPDBRepository localPDB;

    public static LocalPDBToPathConverter get(LocalPDBRepository localPDB) {
        return new LocalPDBToPathConverter(localPDB);
    }

    @Override
    public Path convert(String content) {
        return localPDB.getPathForPdbIdentifier(content);
    }

}
