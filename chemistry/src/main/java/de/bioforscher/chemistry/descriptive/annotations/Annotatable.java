package de.bioforscher.chemistry.descriptive.annotations;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Annotations in a chemical/biological environment are relevant information about the Object implementing this
 * interface, albeit not directly required for any application or method of this object. The Object implementing this
 * interface should provide a {@link List} to store individual {@link Annotation}s. Methods to retrieve specific
 * {@link Annotation}s, filtered by {@link AnnotationType} or description are supplied as default implementations.
 *
 * @author cl
 */
public interface Annotatable {

    /**
     * Returns all {@link Annotation}s belonging to this object.
     *
     * @return All {@link Annotation}s belonging to this object.
     */
    List<Annotation> getAnnotations();

    /**
     * Adds an {@link Annotation}.
     *
     * @param annotation The annotation.
     */
    default void addAnnotation(Annotation annotation) {
        this.getAnnotations().add(annotation);
    }

    /**
     * Retrieves all {@link Annotation}s that belong to the given {@link AnnotationType}.
     *
     * @param type The {@link AnnotationType} of the {@link Annotation}s to gather.
     * @return All {@link Annotation}s that belong to the given {@link AnnotationType}.
     */
    default List<Annotation> getAnnotationsOfType(AnnotationType type) {
        return getAnnotations().stream()
                               .filter(annotation -> annotation.getAnnotationType() == type)
                               .collect(Collectors.toList());
    }

    /**
     * Retrieves the content of all {@link Annotation}s that belong to the given {@link AnnotationType}.
     *
     * @param expectedContent The class of the resulting content.
     * @param type The {@link AnnotationType} of the {@link Annotation}s to gather.
     * @param <ContentType> Any class will suffice. The user (you, using this method) needs to make sure that the
     * content of the Annotation is castable into the expected class.
     * @return The content of all {@link Annotation}s that belong to the given {@link AnnotationType}.
     */
    default <ContentType> List<ContentType> getContentOfAnnotations(Class<ContentType> expectedContent,
                                                                    AnnotationType type) {
        return getAnnotationsOfType(type).stream()
                                         .map(Annotation::getContent)
                                         .map(expectedContent::cast)
                                         .collect(Collectors.toList());
    }

    /**
     * Retrieves the content of all {@link Annotation}s that belong to the given {@link AnnotationType} and description.
     *
     * @param expectedContent The class of the resulting content.
     * @param description The desired description.
     * @param type The {@link AnnotationType} of the {@link Annotation}s to gather.
     * @param <ContentType> Any class will suffice. The user (you, using this method) needs to make sure that the
     * content of the Annotation is castable into the expected class.
     * @return tThe content of all {@link Annotation}s that belong to the given {@link AnnotationType} and description.
     */
    default <ContentType> List<ContentType> getContentOfAnnotations(Class<ContentType> expectedContent,
                                                                    String description, AnnotationType type) {
        return getAnnotationsOfType(type).stream()
                                         .filter(annotation -> annotation.getDescription().equals(description))
                                         .map(Annotation::getContent)
                                         .map(expectedContent::cast)
                                         .collect(Collectors.toList());
    }

    /**
     * Retrieves the first {@link Annotation} matching the given {@link AnnotationType}. This method can be used if
     * you are only expecting one instance of an Annotation to be in the List of Annotations.
     *
     * @param expectedContent The class of the resulting content.
     * @param type The {@link AnnotationType} of the {@link Annotation}s to gather.
     * @param <ContentType> Any class will suffice. The user (you, using this method) needs to make sure that the
     * content of the Annotation is castable into the expected class.
     * @return The first {@link Annotation} matching the given {@link AnnotationType}
     */
    default <ContentType> ContentType getContentOfAnnotation(Class<ContentType> expectedContent, AnnotationType type) {
        return getContentOfAnnotations(expectedContent, type).get(0);
    }

    /**
     * Retrieves the first {@link Annotation} matching the given {@link AnnotationType} and description. This method can
     * be used if you are only expecting one instance of an Annotation to be in the List of Annotations.
     *
     * @param expectedContent The class of the resulting content.
     * @param description The desired description.
     * @param type The {@link AnnotationType} of the {@link Annotation}s to gather.
     * @param <ContentType> Any class will suffice. The user (you, using this method) needs to make sure that the
     * content of the Annotation is castable into the expected class.
     * @return The first {@link Annotation} matching the given {@link AnnotationType} and description
     */
    default <ContentType> ContentType getContentOfAnnotation(Class<ContentType> expectedContent, String description,
                                                             AnnotationType type) {
        return getContentOfAnnotations(expectedContent, description, type).get(0);
    }

}
