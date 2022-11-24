package bio.singa.structure.io.general.sources;

import bio.singa.core.utility.Pair;
import bio.singa.structure.io.general.SourceLocation;
import bio.singa.structure.io.general.converters.ContentConverter;
import bio.singa.structure.io.general.converters.IdentityConverter;
import bio.singa.structure.io.general.converters.PathToObjectConverter;
import bio.singa.structure.io.general.LocalStructureRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
        prepareChains(localIterator, chainList, separator);
        return localIterator;
    }

    public static LocalSourceIterator<String> fromChainList(Collection<Pair<String>> chainList, ContentConverter<String, Path> converter) {
        LocalSourceIterator<String> localIterator = new LocalSourceIterator<>(converter);
        prepareChains(localIterator, chainList);
        return localIterator;
    }

    public static LocalSourceIterator<Path> fromLocalPdb(LocalStructureRepository localPdb, int limit, boolean shuffle) {
        List<Path> paths = new ArrayList<>();
        Path localPdbPath = localPdb.getLocalPdbPath();
        SourceLocation sourceLocation = localPdb.getSourceLocation();
        boolean checkForLimit = limit != -1;
        try (Stream<Path> splitDirectories = Files.list(localPdbPath)) {
            for (Path splitDirectory : splitDirectories.collect(Collectors.toList())) {
                try (Stream<Path> files = Files.walk(splitDirectory)) {
                    for (Path path : files.collect(Collectors.toList())) {
                        String fileName = path.getFileName().toString().toLowerCase();
                        // skip non relevant files
                        if (hasExpectedEnding(sourceLocation, fileName)) {
                            if (checkForLimit && !shuffle) {
                                if (paths.size() >= limit) {
                                    return new LocalSourceIterator<>(paths, IdentityConverter.get(Path.class));
                                }
                            }
                            paths.add(path);
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("unable to read files from " + splitDirectory, e);
                }
            }
            if (shuffle) {
                Collections.shuffle(paths);
                if (checkForLimit) {
                    limit = Math.min(limit, paths.size());
                    paths = paths.subList(0, limit - 1);
                }
            }
            return new LocalSourceIterator<>(paths, IdentityConverter.get(Path.class));
        } catch (IOException e) {
            throw new UncheckedIOException("unable to read files from " + localPdbPath, e);
        }
    }

    private static boolean hasExpectedEnding(SourceLocation sourceLocation, String fileName) {
        switch (sourceLocation) {
            case OFFLINE_PDB:
                return fileName.endsWith(".ent.gz");
            case OFFLINE_MMTF:
                return fileName.endsWith(".mmtf.gz");
            case OFFLINE_MMCIF:
                return fileName.endsWith(".cif.gz");
            case OFFLINE_BCIF:
                return fileName.endsWith(".bcif");
        }
        return false;
    }

    @Override
    public Object getContent(SourceContent source) {
        return pathConverter.convert(locationConverter.convert(source));
    }

}
