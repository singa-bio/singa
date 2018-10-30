package bio.singa.mathematics.transformations.model;

import bio.singa.mathematics.vectors.BitVector;

/**
 * @author cl
 */
public interface BitvectorTransformation extends Transformation<BitVector> {

    @Override
    BitVector applyTo(BitVector matrix);

}
