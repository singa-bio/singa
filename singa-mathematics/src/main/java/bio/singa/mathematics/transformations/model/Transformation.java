package bio.singa.mathematics.transformations.model;

@FunctionalInterface
public interface Transformation<TransformedType> {

    TransformedType applyTo(TransformedType numberConcept);

}
