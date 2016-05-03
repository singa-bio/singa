package de.bioforscher.core.annotations;

import de.bioforscher.core.identifier.model.Identifier;

/**
 * Created by Christoph on 02.05.2016.
 */
public enum AnnotationType {

    NOTE(String.class),
    ADDITIONAL_IDENTIFIER(Identifier.class);

    private final Class<?> annotationClass;

    AnnotationType(Class<?> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public Class<?> getAnnotationClass() {
        return this.annotationClass;
    }

}
