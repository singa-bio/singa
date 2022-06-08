package bio.singa.structure.io.cif;

import bio.singa.core.utility.Pair;
import bio.singa.structure.io.general.converters.PdbIdentifierToUrlConverter;
import bio.singa.structure.io.general.converters.UrlToLinesConverter;
import bio.singa.structure.io.general.sources.AbstractSourceIterator;
import org.rcsb.cif.CifIO;
import org.rcsb.cif.model.CifFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class RemoteMmCifSourceIterator extends AbstractSourceIterator<String, CifFile> {

    private MmcifIdentifierToUrlConverter identifierConverter;

    public RemoteMmCifSourceIterator(List<String> sources) {
        super(sources);
        identifierConverter = MmcifIdentifierToUrlConverter.get();
    }

    public RemoteMmCifSourceIterator(Path chainList, String separator) {
        super();
        identifierConverter = MmcifIdentifierToUrlConverter.get();
        prepareChains(this, chainList, separator);
    }

    public RemoteMmCifSourceIterator(Collection<Pair<String>> chainList) {
        super();
        identifierConverter = MmcifIdentifierToUrlConverter.get();
        prepareChains(this, chainList);
    }

    @Override
    public CifFile getContent(String source) {
        try {
            return CifIO.readFromURL(identifierConverter.convert(source));
        } catch (IOException e) {
            throw new UncheckedIOException("unable to read cif file " + source, e);
        }
    }

}
