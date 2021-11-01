package bio.singa.structure.io.general.converters;

import bio.singa.structure.io.general.LocalStructureRepository;

import java.nio.file.Path;

/**
 * @author cl
 */
public class LocalPdbToPathConverter implements ContentConverter<String, Path> {

    private LocalPdbToPathConverter(LocalStructureRepository localPDB) {
        this.localPDB = localPDB;
    }

    private final LocalStructureRepository localPDB;

    public static LocalPdbToPathConverter get(LocalStructureRepository localPDB) {
        return new LocalPdbToPathConverter(localPDB);
    }

    @Override
    public Path convert(String content) {
        return localPDB.getPathForStructure(content);
    }

}
