package de.bioforscher.singa.core.biology;

import de.bioforscher.singa.core.identifier.NCBITaxonomyIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifiable;
import de.bioforscher.singa.core.utility.Nameable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class Organism implements Nameable, Identifiable<NCBITaxonomyIdentifier> {

    private String scientificName;
    private String commonName;
    private NCBITaxonomyIdentifier identifier;
    private List<Taxon> lineage;

    public Organism(String scientificName) {
        this.scientificName = scientificName;
        this.lineage = new ArrayList<>();
    }

    public String getScientificName() {
        return this.scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return this.commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @Override
    public String getName() {
        return getScientificName();
    }

    @Override
    public NCBITaxonomyIdentifier getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(NCBITaxonomyIdentifier identifier) {
        this.identifier = identifier;
    }

    public List<Taxon> getLineage() {
        return this.lineage;
    }

    public void setLineage(List<Taxon> lineage) {
        this.lineage = lineage;
    }

}
