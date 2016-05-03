package de.bioforscher.chemistry.descriptive;

import de.bioforscher.core.annotations.Annotation;
import de.bioforscher.core.annotations.AnnotationType;
import de.bioforscher.core.identifier.PubChemIdentifier;
import org.junit.Test;

import java.util.List;

/**
 * Created by Christoph on 02.05.2016.
 */
public class EnzymeAnnotationTest {

    @Test
    public void shouldCreateAnnotation() {
        Enzyme enzyme = new Enzyme.Builder("P05062")
                .name("Fructose-bisphosphate aldolase B")
                .build();

        Annotation<String> noteAnnotation = new Annotation<>(AnnotationType.NOTE, "This protein is involved in step 4" +
                " of " +
                "the subpathway that synthesizes D-glyceraldehyde 3-phosphate and glycerone phosphate from D-glucose.");
        Annotation<PubChemIdentifier> pubChemIdentifierAnnotation = new Annotation<>(AnnotationType
                .ADDITIONAL_IDENTIFIER, new PubChemIdentifier("CID 1234"));
        enzyme.addAnnotation(1, noteAnnotation);
        enzyme.addAnnotation(2, pubChemIdentifierAnnotation);

        List<Annotation> annotationsOfType = enzyme.getAnnotationsOfType(AnnotationType.ADDITIONAL_IDENTIFIER);
        annotationsOfType.forEach(System.out::println);

    }

}