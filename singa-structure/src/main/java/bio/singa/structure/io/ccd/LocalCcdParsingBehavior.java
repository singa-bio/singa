package bio.singa.structure.io.ccd;

import bio.singa.structure.io.general.LocalCcdRepository;
import org.rcsb.cif.CifIO;
import org.rcsb.cif.model.CifFile;

import java.io.IOException;
import java.io.UncheckedIOException;

import static bio.singa.structure.io.general.converters.CifStaticOptions.CIF_PLAIN;

public class LocalCcdParsingBehavior implements CcdParsingBehavior {

    private final LocalCcdRepository localCcdRepository;

    public LocalCcdParsingBehavior(LocalCcdRepository localCcdRepository) {
        this.localCcdRepository = localCcdRepository;
    }

    @Override
    public CifFile getCcdInformation(String identifier) {
        try {
            return CifIO.readFromPath(localCcdRepository.getPathForLigandIdentifier(identifier), CIF_PLAIN);
        } catch (IOException e) {
            throw new UncheckedIOException("unable to get compound for identifier " + identifier, e);
        }
    }



}
