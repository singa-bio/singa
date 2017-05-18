package de.bioforscher.singa.chemistry.descriptive.features.databases;

import de.bioforscher.singa.chemistry.descriptive.features.FeatureDescriptor;

/**
 * @author cl
 */
public abstract class DatabaseDescriptor  implements FeatureDescriptor {

    protected String sourceName;
    protected String sourcePublication;

    protected void setSourceName(String methodName) {
        this.sourceName = methodName;
    }

    protected void setSourcePublication(String methodPublication) {
        this.sourcePublication = methodPublication;
    }

    @Override
    public String getSourceName() {
        return this.sourceName;
    }

    @Override
    public String getSourcePublication() {
        return this.sourcePublication;
    }


}
