package bio.singa.structure.parser.pdb.structures.iterators.sources;

import bio.singa.structure.parser.pdb.structures.iterators.converters.PDBIdentifierToURLConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.URLToPDBLinesConverter;

import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class RemotePDBSourceIterator extends AbstractSourceIterator<String, List<String>> {

    private final URLToPDBLinesConverter urlConverter;
    private final PDBIdentifierToURLConverter identifierConverter;

    public RemotePDBSourceIterator(List<String> sources) {
        super(sources);
        urlConverter = URLToPDBLinesConverter.get();
        identifierConverter = PDBIdentifierToURLConverter.get();
    }

    public RemotePDBSourceIterator(Path chainList, String separator) {
        super();
        urlConverter = URLToPDBLinesConverter.get();
        identifierConverter = PDBIdentifierToURLConverter.get();
        RemotePDBSourceIterator.prepareChains(this, chainList, separator);
    }

    @Override
    public List<String> getContent(String source) {
        return urlConverter.convert(identifierConverter.convert(source));
    }

}
