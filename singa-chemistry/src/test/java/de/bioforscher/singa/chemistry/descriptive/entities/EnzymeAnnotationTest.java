package de.bioforscher.singa.chemistry.descriptive.entities;

import de.bioforscher.singa.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.singa.chemistry.descriptive.annotations.AnnotationType;
import de.bioforscher.singa.core.identifier.PubChemIdentifier;
import org.junit.Test;

import java.util.List;

/**
 * @author cl
 */
public class EnzymeAnnotationTest {

    @Test
    public void shouldCreateAnnotation() {
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