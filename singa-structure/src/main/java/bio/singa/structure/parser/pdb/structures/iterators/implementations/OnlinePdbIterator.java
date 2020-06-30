package bio.singa.structure.parser.pdb.structures.iterators.implementations;

import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentifierToPdbUrlConverter;
import bio.singa.structure.parser.pdb.structures.iterators.converters.UrlToPdbLinesConverter;
import bio.singa.structure.parser.pdb.structures.iterators.AbstractIterator;

import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class OnlinePdbIterator extends AbstractIterator<String, List<String>> {

    private final UrlToPdbLinesConverter urlConverter;
    private final IdentifierToPdbUrlConverter identifierConverter;

    public OnlinePdbIterator(List<String> sources) {
        super(sources);
        urlConverter = UrlToPdbLinesConverter.get();
        identifierConverter = IdentifierToPdbUrlConverter.get();
    }

    public OnlinePdbIterator(Path chainList, String separator) {
        super();
        urlConverter = UrlToPdbLinesConverter.get();
        identifierConverter = IdentifierToPdbUrlConverter.get();
        OnlinePdbIterator.prepareChains(this, chainList, separator);
    }

    @Override
    public List<String> getContent(String source) {
        return urlConverter.convert(identifierConverter.convert(source));
    }

}
