package de.bioforscher.core.annotations;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 02.05.2016.
 */
public interface Annotatable {

    Map<Integer, Annotation> getAnnotations();

    default void addAnnotation(Integer identifier, Annotation annotation) {
        this.getAnnotations().put(identifier, annotation);
    }

    default <AnnotationClass> AnnotationClass getAnnotationContent(Integer identifier, Class<AnnotationClass>
            annotationType) {
        return annotationType.cast(this.getAnnotations().get(identifier).getContent());
    }

    default List<Annotation> getAnnotationsOfType(AnnotationType type) {
        return getAnnotations().values().stream().filter(annotation -> annotation
                .getAnnotationType() == type).collect(Collectors.toList());
    }

}
