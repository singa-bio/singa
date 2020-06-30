package bio.singa.structure.parser.pdb.structures.iterators.implementations;

import bio.singa.structure.parser.pdb.structures.iterators.converters.IdentifierToMmtfBytesConverter;
import bio.singa.structure.parser.pdb.structures.iterators.AbstractIterator;

import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class OnlineMmtfIterator extends AbstractIterator<String, byte[]> {

    private final IdentifierToMmtfBytesConverter converter;

    public OnlineMmtfIterator(List<String> sources) {
        super(sources);
        converter = IdentifierToMmtfBytesConverter.get();
    }

    public OnlineMmtfIterator(Path chainList, String separator) {
        super();
        converter = IdentifierToMmtfBytesConverter.get();
        OnlineMmtfIterator.prepareChains(this, chainList, separator);
    }

    @Override
    public byte[] getContent(String source) {
        return converter.convert(source);
    }

}
