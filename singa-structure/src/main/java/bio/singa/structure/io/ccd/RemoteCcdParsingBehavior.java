package bio.singa.structure.io.ccd;

import org.rcsb.cif.CifIO;
import org.rcsb.cif.model.CifFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;

import static bio.singa.structure.io.general.converters.CifStaticOptions.CIF_PLAIN;

public class RemoteCcdParsingBehavior implements CcdParsingBehavior {

    private static final String CIF_FETCH_URL = "https://files.rcsb.org/ligands/view/%s.cif";

    @Override
    public CifFile getCcdInformation(String identifier) {
        try {
            URL url = new URL(String.format(CIF_FETCH_URL, identifier));
            return CifIO.readFromURL(url, CIF_PLAIN);
        } catch (IOException e) {
            return null;
        }
    }
}
