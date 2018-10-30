package bio.singa.mathematics.transformations.model;

/**
 * @author cl
 */
public interface Transformable<TransformationType> {

     TransformationType applyTransformation(Transformation<TransformationType> transformation);

}
