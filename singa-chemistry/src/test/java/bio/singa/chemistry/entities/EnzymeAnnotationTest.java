package bio.singa.chemistry.entities;

import bio.singa.chemistry.annotations.Annotation;
import bio.singa.chemistry.annotations.AnnotationType;
import bio.singa.features.identifiers.PubChemIdentifier;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author cl
 */
class EnzymeAnnotationTest {

    @Test
    void shouldCreateAnnotation() {
        Enzyme enzyme = new Enzyme.Builder("P05062")
                .name("Fructose-bisphosphate aldolase B")
                .build();

        Annotation<String> noteAnnotation = new Annotation<>(AnnotationType.NOTE, "This protein is involved in step 4" +
                " of " +
                "the subpathway that synthesizes D-glyceraldehyde 3-phosphate and glycerone phosphate of D-glucose.");
        Annotation<PubChemIdentifier> pubChemIdentifierAnnotation = new Annotation<>(AnnotationType
                .ADDITIONAL_IDENTIFIER, new PubChemIdentifier("CID:1234"));

        List<Annotation> annotationsOfType = enzyme.getAnnotationsOfType(AnnotationType.ADDITIONAL_IDENTIFIER);
        annotationsOfType.forEach(System.out::println);

    }

}