package bio.singa.chemistry.features.identifiers;

import bio.singa.chemistry.features.databases.unichem.UniChemParser;
import bio.singa.features.identifiers.InChIKey;
import bio.singa.features.identifiers.PDBLigandIdentifier;
import bio.singa.features.model.FeatureProvider;
import bio.singa.features.model.Featureable;

/**
 * @author cl
 */
public class PDBLigandIdentiferProvider extends FeatureProvider<PDBLigandIdentifier> {

    public PDBLigandIdentiferProvider() {
        setProvidedFeature(PDBLigandIdentifier.class);
        addRequirement(InChIKey.class);
    }

    @Override
    public <FeatureableType extends Featureable> PDBLigandIdentifier provide(FeatureableType featureable) {
        return UniChemParser.fetchPdbLigandIdentifier(featureable.getFeature(InChIKey.class));
    }

}
