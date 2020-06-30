package bio.singa.structure.parser.pdb.structures.iterators.converters;

import bio.singa.structure.parser.pdb.structures.StructureParser;

import java.nio.file.Path;

/**
 * @author cl
 */
public class LocalPdbToPathConverter implements ContentConverter<String, Path> {

    private LocalPdbToPathConverter(StructureParser.LocalPdb localPDB) {
        this.localPDB = localPDB;
    }

    private final StructureParser.LocalPdb localPDB;

    public static LocalPdbToPathConverter get(StructureParser.LocalPdb localPDB) {
        return new LocalPdbToPathConverter(localPDB);
    }

    @Override
    public Path convert(String content) {
        return localPDB.getPathForPdbIdentifier(content);
    }

}
