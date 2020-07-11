package bio.singa.chemistry.features.structure2d;

import bio.singa.chemistry.model.MoleculeGraph;
import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

/**
 * @author cl
 */
public class Structure2D extends QualitativeFeature<MoleculeGraph> {

    public Structure2D(MoleculeGraph structure, Evidence evidence) {
        super(structure, evidence);
    }

}
