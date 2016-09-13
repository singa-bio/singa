package de.bioforscher.chemistry.descriptive.annotations;

/**
 * Created by Christoph on 02.05.2016.
 */
public class Annotation<ContentType> {

    private AnnotationType annotationType;
    private String description;
    private ContentType content;

    public Annotation(AnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    public Annotation(AnnotationType annotationType, ContentType content) {
        this.annotationType = annotationType;
        this.content = content;
    }

    public Annotation(AnnotationType annotationType, String description, ContentType content) {
        this.annotationType = annotationType;
        this.description = description;
        this.content = content;
    }


    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(AnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContentType getContent() {
        return content;
    }

    public void setContent(ContentType content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return annotationType.toString() + ": " + content.toString();
    }
}
