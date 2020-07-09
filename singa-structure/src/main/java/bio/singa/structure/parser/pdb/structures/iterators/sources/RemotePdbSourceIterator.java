package bio.singa.structure.parser.pdb.structures.iterators.sources;

import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentifierToPdbUrlConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.UrlToPdbLinesConverter;

import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class RemotePdbSourceIterator extends AbstractSourceIterator<String, List<String>> {

    private final UrlToPdbLinesConverter urlConverter;
    private final IdentifierToPdbUrlConverter identifierConverter;

    public RemotePdbSourceIterator(List<String> sources) {
        super(sources);
        urlConverter = UrlToPdbLinesConverter.get();
        identifierConverter = IdentifierToPdbUrlConverter.get();
    }

    public RemotePdbSourceIterator(Path chainList, String separator) {
        super();
        urlConverter = UrlToPdbLinesConverter.get();
        identifierConverter = IdentifierToPdbUrlConverter.get();
        RemotePdbSourceIterator.prepareChains(this, chainList, separator);
    }

    @Override
    public List<String> getContent(String source) {
        return urlConverter.convert(identifierConverter.convert(source));
    }

}
