package bio.singa.structure.parser.pdb.structures.iterators.converters;

import bio.singa.structure.parser.pdb.structures.LocalPdbRepository;

import java.nio.file.Path;

/**
 * @author cl
 */
public class LocalPdbToPathConverter implements ContentConverter<String, Path> {

    private LocalPdbToPathConverter(LocalPdbRepository localPDB) {
        this.localPDB = localPDB;
    }

    private final LocalPdbRepository localPDB;

    public static LocalPdbToPathConverter get(LocalPdbRepository localPDB) {
        return new LocalPdbToPathConverter(localPDB);
    }

    @Override
    public Path convert(String content) {
        return localPDB.getPathForPdbIdentifier(content);
    }

}
