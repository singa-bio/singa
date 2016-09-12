package de.bioforscher.core.biology;

import de.bioforscher.core.identifier.NCBITaxonomyIdentifier;
import de.bioforscher.core.identifier.model.Identifiable;
import de.bioforscher.core.utility.Nameable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph on 14.07.2016.
 */
public class Organism implements Nameable, Identifiable<NCBITaxonomyIdentifier> {

    private String name;
    private NCBITaxonomyIdentifier identifier;
    private List<String> synonyms;
    private List<Taxon> lineage;

    public Organism(String name) {
        this.name = name;
        this.synonyms = new ArrayList<>();
        this.lineage = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public NCBITaxonomyIdentifier getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(NCBITaxonomyIdentifier identifier) {
        this.identifier = identifier;
    }

    public List<String> getSynonyms() {
        return this.synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public List<Taxon> getLineage() {
        return this.lineage;
    }

    public void setLineage(List<Taxon> lineage) {
        this.lineage = lineage;
    }

}
