package bio.singa.structure.io.ccd;

import org.rcsb.cif.model.CifFile;

public class NoCcdParsingBehavior implements CcdParsingBehavior {

    @Override
    public CifFile getCcdInformation(String identifier) {
        return null;
    }

}
