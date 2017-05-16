package de.bioforscher.singa.chemistry.descriptive.features.descriptor;

/**
 * @author cl
 */
public abstract class DatabaseDescriptor implements FeatureDescriptor {

    private String sourceDatabase;
    private String databasePublication;

    public String getSourceDatabase() {
        return this.sourceDatabase;
    }

    public void setSourceDatabase(String sourceDatabase) {
        this.sourceDatabase = sourceDatabase;
    }

    public String getDatabasePublication() {
        return this.databasePublication;
    }

    public void setDatabasePublication(String databasePublication) {
        this.databasePublication = databasePublication;
    }
}
