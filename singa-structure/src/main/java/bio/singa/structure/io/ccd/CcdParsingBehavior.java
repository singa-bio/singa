package bio.singa.structure.io.ccd;

import org.rcsb.cif.model.CifFile;

public interface CcdParsingBehavior {

    CifFile getCcdInformation(String identifier);

}
