package de.bioforscher.singa.chemistry.annotations;

/**
 * An Annotation in a chemical/biological environment is a relevant information about the Object that holds it, albeit
 * not directly required for any application or method of this object. Certain {@link AnnotationType}s are predetermined
 * in order to provide a primary identification criteria. Further, any Annotation can be customized by setting a
 * description such as "function", "is inhibited by", etc. (with {@link Annotation#setDescription(String)}). Finally the
 * content of an Annotation can be any Object. Every Object that implements the {@link Annotatable} interface can be
 * given any Annotation.
 *
 * @param <ContentType> The type content of the Annotation.
 * @author cl
 */
public class Annotation<ContentType> {

    /**
     * The {@link AnnotationType} provides a primary identification criteria.
     */
    private AnnotationType annotationType;

    /**
     * The description describes the Annotation further.
     */
    private String description;

    /**
     * The actual content of this annotation.
     */
    private ContentType content;

    /**
     * Creates a new Annotation with the given {@link AnnotationType}.
     *
     * @param annotationType The {@link AnnotationType}.
     */
    public Annotation(AnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    /**
     * Creates a new Annotation with the given {@link AnnotationType} and content.
     *
     * @param annotationType The {@link AnnotationType}.
     * @param content The content.
     */
    public Annotation(AnnotationType annotationType, ContentType content) {
        this.annotationType = annotationType;
        this.content = content;
    }

    /**
     * Creates a new Annotation with the given {@link AnnotationType}, description, and content.
     *
     * @param annotationType The {@link AnnotationType}.
     * @param description A description for this annotation. (e.g. "function", "is inhibited by", etc.)
     * @param content The content.
     */
    public Annotation(AnnotationType annotationType, String description, ContentType content) {
        this.annotationType = annotationType;
        this.description = description;
        this.content = content;
    }

    /**
     * Returns the {@link AnnotationType}.
     *
     * @return The {@link AnnotationType}.
     */
    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    /**
     * Sets the {@link AnnotationType}.
     *
     * @param annotationType The {@link AnnotationType}.
     */
    public void setAnnotationType(AnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    /**
     * Returns the description.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description The description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the content.
     *
     * @return The content.
     */
    public ContentType getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content The content.
     */
    public void setContent(ContentType content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return annotationType.toString() + (annotationType == AnnotationType.NOTE ? " on "+description : "")+": " + content.toString();
    }
}
