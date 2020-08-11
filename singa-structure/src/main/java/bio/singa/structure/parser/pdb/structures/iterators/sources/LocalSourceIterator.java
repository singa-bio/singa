package bio.singa.structure.parser.pdb.structures.iterators.sources;

import bio.singa.structure.parser.pdb.structures.LocalPdbRepository;
import bio.singa.structure.parser.pdb.structures.iterators.converters.ContentConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentityConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.PathToObjectConverter;

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
public class LocalSourceIterator<SourceContent> extends AbstractSourceIterator<SourceContent, Object> {

    private final ContentConverter<SourceContent, Path> locationConverter;
    private final PathToObjectConverter pathConverter;

    public LocalSourceIterator(List<SourceContent> sources, ContentConverter<SourceContent, Path> converter) {
        super(sources);
        locationConverter = converter;
        pathConverter = PathToObjectConverter.get();
    }

    private LocalSourceIterator(ContentConverter<SourceContent, Path> converter) {
        super();
        locationConverter = converter;
        pathConverter = PathToObjectConverter.get();
    }

    public static LocalSourceIterator<String> fromChainList(Path chainList, String separator, ContentConverter<String, Path> converter) {
        LocalSourceIterator<String> localIterator = new LocalSourceIterator<>(converter);
        LocalSourceIterator.prepareChains(localIterator, chainList, separator);
        return localIterator;
    }

    public static LocalSourceIterator<Path> fromLocalPdb(LocalPdbRepository localPdb) {
        List<Path> paths = new ArrayList<>();
        Path localPdbPath = localPdb.getLocalPdbPath();
        try (Stream<Path> splitDirectories = Files.list(localPdbPath)) {
            for (Path splitDirectory : splitDirectories.collect(Collectors.toList())) {
                try (Stream<Path> files = Files.walk(splitDirectory)) {
                    for (Path path : files.collect(Collectors.toList())) {
                        String fileName = path.getFileName().toString().toLowerCase();
                        // skip non relevant files
                        if (fileName.endsWith(".mmtf.gz") || fileName.endsWith(".ent.gz")) {
                            paths.add(path);
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("unable to read files from " + splitDirectory, e);
                }
            }
            return new LocalSourceIterator<>(paths, IdentityConverter.get(Path.class));
        } catch (IOException e) {
            throw new UncheckedIOException("unable to read files from " + localPdbPath, e);
        }
    }

    @Override
    public Object getContent(SourceContent source) {
        return pathConverter.convert(locationConverter.convert(source));
    }

}
