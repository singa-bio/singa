package de.bioforscher.mathematics.graphs.model;

import de.bioforscher.mathematics.vectors.Vector;

/**
 * Created by Christoph on 23/11/2016.
 */
public class GenericNode<ContentType> extends AbstractNode<GenericNode<ContentType>, Vector> {

    private ContentType content;

    public GenericNode(int identifier) {
        super(identifier);
    }

    public GenericNode(int identifier, ContentType content) {
        super(identifier);
        this.content = content;
    }

    public ContentType getContent() {
        return this.content;
    }

    public void setContent(ContentType content) {
        this.content = content;
    }
}
