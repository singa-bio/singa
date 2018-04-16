package de.bioforscher.singa.chemistry.descriptive.features.databases.chebi;

import de.bioforscher.singa.chemistry.descriptive.features.smiles.Smiles;
import de.bioforscher.singa.features.identifiers.ChEBIIdentifier;
import de.bioforscher.singa.features.identifiers.InChIKey;
import de.bioforscher.singa.features.model.FeatureOrigin;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.structure.features.molarmass.MolarMass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cl
 */
public class ChEBIDatabase {

    public static final FeatureOrigin origin = new FeatureOrigin(FeatureOrigin.OriginType.DATABASE,
            "ChEBI Database",
            "Degtyarenko, Kirill, et al. \"ChEBI: a database and ontology for chemical entities of " +
                    "biological interest.\" Nucleic acids research 36.suppl 1 (2008): D344-D350.");

    private static final Logger logger = LoggerFactory.getLogger(ChEBIDatabase.class);

    /**
     * The instance.
     */
    private static final ChEBIDatabase instance = new ChEBIDatabase();

    public static ChEBIDatabase getInstance() {
        return instance;
    }

    public static MolarMass fetchMolarMass(Featureable featureable) {
        // try to get Chebi identifier
        ChEBIIdentifier chEBIIdentifier = featureable.getFeature(ChEBIIdentifier.class);
        // try to get weight from ChEBI Database
        return ChEBIParserService.parse(chEBIIdentifier).getFeature(MolarMass.class);
    }

    public static InChIKey fetchInchiKey(ChEBIIdentifier chEBIIdentifier) {
        return ChEBIParserService.parse(chEBIIdentifier).getFeature(InChIKey.class);
    }

    public static Smiles fetchSmiles(Featureable featureable) {
        // try to get Chebi identifier
        ChEBIIdentifier chEBIIdentifier = featureable.getFeature(ChEBIIdentifier.class);
        // try to get weight from ChEBI Database
        return ChEBIParserService.parse(chEBIIdentifier).getFeature(Smiles.class);
    }

}
