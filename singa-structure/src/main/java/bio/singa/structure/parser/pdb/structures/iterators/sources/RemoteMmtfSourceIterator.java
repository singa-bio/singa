package bio.singa.structure.parser.pdb.structures.iterators.sources;

import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentifierToMmtfBytesConverter;

import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class RemoteMmtfSourceIterator extends AbstractSourceIterator<String, byte[]> {

    private final IdentifierToMmtfBytesConverter converter;

    public RemoteMmtfSourceIterator(List<String> sources) {
        super(sources);
        converter = IdentifierToMmtfBytesConverter.get();
    }

    public RemoteMmtfSourceIterator(Path chainList, String separator) {
        super();
        converter = IdentifierToMmtfBytesConverter.get();
        RemoteMmtfSourceIterator.prepareChains(this, chainList, separator);
    }

    @Override
    public byte[] getContent(String source) {
        return converter.convert(source);
    }

}
