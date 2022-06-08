package bio.singa.structure.io.general.sources;

import bio.singa.core.utility.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author cl
 */
public abstract class AbstractSourceIterator<SourceContent, ContentType> implements SourceIterator<SourceContent, ContentType> {

    private int cursor;
    private List<SourceContent> sources;
    private List<String> chains;

    public AbstractSourceIterator(List<SourceContent> sources) {
        cursor = 0;
        this.sources = sources;
        chains = new ArrayList<>();
    }

    public AbstractSourceIterator() {
        cursor = 0;
        sources = new ArrayList<>();
        chains = new ArrayList<>();
    }

    @Override
    public List<SourceContent> getSources() {
        return sources;
    }

    public void setSources(List<SourceContent> sources) {
        this.sources = sources;
    }

    @Override
    public boolean hasNext() {
        return cursor != sources.size();
    }

    @Override
    public SourceContent next() {
        SourceContent content = sources.get(cursor);
        cursor++;
        return content;
    }

    public static void prepareChains(SourceIterator<String, ?> iterator, Path chainList, String separator) {
        try {
            prepareChains(iterator, readMappingFile(chainList, separator));
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read mapping file " + chainList, e);
        }
    }

    public static void prepareChains(SourceIterator<String, ?> iterator, Collection<Pair<String>> chainList) {
        for (Pair<String> pair : chainList) {
            iterator.getSources().add(pair.getFirst());
            iterator.getChains().add(pair.getSecond());
        }
    }

    @Override
    public boolean hasChain() {
        return !chains.isEmpty();
    }

    @Override
    public String getChain() {
        return chains.get(cursor - 1);
    }

    public void setChains(List<String> chains) {
        this.chains = chains;
    }

    @Override
    public List<String> getChains() {
        return chains;
    }

    /**
     * Reads a pdb identifier chain identifier mapping file.
     *
     * @param mappingPath The path to the mapping file.
     * @param separator The String separating both.
     * @return A list of pdb identifier chain paris.
     * @throws IOException if the file could not be read.
     */
    private static List<Pair<String>> readMappingFile(Path mappingPath, String separator) throws IOException {
        InputStream inputStream = Files.newInputStream(mappingPath);
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                return composePairsForChainList(bufferedReader.lines().collect(Collectors.toList()), separator);
            }
        }
    }

    /**
     * Reads a file of pdb identifier chain identifier paris.
     *
     * @param lines The lines that should be parsed.
     * @param separator The String separating the pairs.
     * @return A list of pdb identifier chain paris.
     */
    private static List<Pair<String>> composePairsForChainList(List<String> lines, String separator) {
        ArrayList<Pair<String>> pairs = new ArrayList<>();
        for (String line : lines) {
            String[] split = line.split(separator);
            // first contains pdbid and second contains chain
            pairs.add(new Pair<>(split[0], split[1]));
        }
        return pairs;
    }

}
