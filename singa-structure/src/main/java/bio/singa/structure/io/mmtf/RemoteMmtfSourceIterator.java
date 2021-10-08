package bio.singa.structure.io.mmtf;

import bio.singa.structure.io.general.converters.PdbIdentifierToMmtfBytesConverter;
import bio.singa.structure.io.general.sources.AbstractSourceIterator;

import java.nio.file.Path;
import java.util.List;

/**
 * @author cl
 */
public class RemoteMmtfSourceIterator extends AbstractSourceIterator<String, byte[]> {

    private final PdbIdentifierToMmtfBytesConverter converter;

    public RemoteMmtfSourceIterator(List<String> sources) {
        super(sources);
        converter = PdbIdentifierToMmtfBytesConverter.get();
    }

    public RemoteMmtfSourceIterator(Path chainList, String separator) {
        super();
        converter = PdbIdentifierToMmtfBytesConverter.get();
        prepareChains(this, chainList, separator);
    }

    @Override
    public byte[] getContent(String source) {
        return converter.convert(source);
    }

}
