package bio.singa.chemistry.features.structure3d;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;
import bio.singa.structure.model.interfaces.AtomContainer;

/**
 * @author cl
 */
public class Structure3D extends QualitativeFeature<AtomContainer> {

    public Structure3D(AtomContainer structure, Evidence evidence) {
        super(structure, evidence);
    }

}
