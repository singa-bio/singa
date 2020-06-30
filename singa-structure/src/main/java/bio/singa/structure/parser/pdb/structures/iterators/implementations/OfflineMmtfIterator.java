package bio.singa.structure.parser.pdb.structures.iterators.implementations;

import bio.singa.structure.parser.pdb.structures.StructureParser;
import bio.singa.structure.parser.pdb.structures.iterators.AbstractIterator;
import bio.singa.structure.parser.pdb.structures.iterators.converters.ContentConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentityConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.PathToMmtfBytesConverter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author cl
 */
public class OfflineMmtfIterator<SourceContent> extends AbstractIterator<SourceContent, byte[]>  {

    private final ContentConverter<SourceContent, Path> locationConverter;
    private final PathToMmtfBytesConverter pathConverter;

    public OfflineMmtfIterator(List<SourceContent> sources, ContentConverter<SourceContent, Path> converter) {
        super(sources);
        locationConverter = converter;
        pathConverter = PathToMmtfBytesConverter.get();
    }

    private OfflineMmtfIterator(ContentConverter<SourceContent, Path> converter) {
        super();
        locationConverter = converter;
        pathConverter = PathToMmtfBytesConverter.get();
    }

    public static OfflineMmtfIterator<String> fromChainList(Path chainList, String separator, ContentConverter<String, Path> converter) {
        OfflineMmtfIterator<String> offlinePdbIterator = new OfflineMmtfIterator<>(converter);
        OfflineMmtfIterator.prepareChains(offlinePdbIterator, chainList, separator);
        return offlinePdbIterator;
    }

    public static OfflineMmtfIterator<Path> fromLocalPdb(StructureParser.LocalPdb localPdb) {
        List<Path> paths = new ArrayList<>();
        Path localPdbPath = localPdb.getLocalPdbPath();
        try (Stream<Path> splitDirectories = Files.list(localPdbPath)) {
            for (Path splitDirectory : splitDirectories.collect(Collectors.toList())) {
                try (Stream<Path> files = Files.walk(splitDirectory)) {
                    for (Path path : files.collect(Collectors.toList())) {
                        String fileName = path.getFileName().toString().toLowerCase();
                        // skip non .mmtf.gz files
                        if (!fileName.endsWith(".mmtf.gz")) {
                            continue;
                        }
                        paths.add(path);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("unable to read files from " + splitDirectory, e);
                }
            }
            return new OfflineMmtfIterator<>(paths, IdentityConverter.get(Path.class));
        } catch (IOException e) {
            throw new UncheckedIOException("unable to read files from " + localPdbPath, e);
        }
    }


    @Override
    public byte[] getContent(SourceContent source) {
        return pathConverter.convert(locationConverter.convert(source));
    }

}
