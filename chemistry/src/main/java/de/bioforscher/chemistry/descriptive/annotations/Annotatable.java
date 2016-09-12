package de.bioforscher.chemistry.descriptive.annotations;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 02.05.2016.
 */
public interface Annotatable {

    List<Annotation> getAnnotations();

    default void addAnnotation(Annotation annotation) {
        this.getAnnotations().add(annotation);
    }

    default List<Annotation> getAnnotationsOfType(AnnotationType type) {
        return getAnnotations().stream()
                               .filter(annotation -> annotation.getAnnotationType() == type)
                               .collect(Collectors.toList());
    }

    default <T> List<T> getContentOfAnnotations(Class<T> expectedContent, AnnotationType type) {
        return getAnnotationsOfType(type).stream()
                                         .map(Annotation::getContent)
                                         .map(expectedContent::cast)
                                         .collect(Collectors.toList());
    }

    default <T> T getAnnotation(Class<T> expectedContent, AnnotationType type) {
        return getContentOfAnnotations(expectedContent, type).get(0);
    }

}
