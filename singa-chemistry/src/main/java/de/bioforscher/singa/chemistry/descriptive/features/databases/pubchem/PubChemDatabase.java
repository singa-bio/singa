package de.bioforscher.singa.chemistry.descriptive.features.databases.pubchem;

import de.bioforscher.singa.chemistry.descriptive.features.logp.LogP;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.Featureable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cl
 */
public class PubChemDatabase {

    private static final Logger logger = LoggerFactory.getLogger(PubChemDatabase.class);

    public static final FeatureOrigin origin = new FeatureOrigin(FeatureOrigin.OriginType.DATABASE,
            "PubChem Database",
            "Kim, Sunghwan, et al. \"PubChem substance and compound databases.\" Nucleic acids research " +
                    "44.D1 (2016): D1202-D1213.");

    /**
     * The instance.
     */
    private static final PubChemDatabase instance = new PubChemDatabase();

    public static PubChemDatabase getInstance() {
        return instance;
    }

    public static <FeaturableType extends Featureable> LogP fetchLogP(Featureable featureable) {


        return null;
    }


}
