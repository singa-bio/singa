package bio.singa.structure.io.pdb;

import bio.singa.core.utility.Pair;
import bio.singa.structure.io.general.converters.PdbIdentifierToUrlConverter;
import bio.singa.structure.io.general.converters.UrlToLinesConverter;
import bio.singa.structure.io.general.sources.AbstractSourceIterator;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 * @author cl
 */
public class RemotePdbSourceIterator extends AbstractSourceIterator<String, List<String>> {

    private final UrlToLinesConverter urlConverter;
    private final PdbIdentifierToUrlConverter identifierConverter;

    public RemotePdbSourceIterator(List<String> sources) {
        super(sources);
        urlConverter = UrlToLinesConverter.get();
        identifierConverter = PdbIdentifierToUrlConverter.get();
    }

    public RemotePdbSourceIterator(Path chainList, String separator) {
        super();
        urlConverter = UrlToLinesConverter.get();
        identifierConverter = PdbIdentifierToUrlConverter.get();
        prepareChains(this, chainList, separator);
    }

    public RemotePdbSourceIterator(Collection<Pair<String>> chainList) {
        super();
        urlConverter = UrlToLinesConverter.get();
        identifierConverter = PdbIdentifierToUrlConverter.get();
        prepareChains(this, chainList);
    }

    @Override
    public List<String> getContent(String source) {
        return urlConverter.convert(identifierConverter.convert(source));
    }

}
