package de.bioforscher.core.biology;

import de.bioforscher.core.utility.Nameable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Christoph on 14.07.2016.
 */
public class Organism implements Nameable {

    private String name;
    private List<String> synonyms;
    private List<Rank> lineage;

    public Organism(String name) {
        this.name = name;
        this.synonyms = new ArrayList<>();
        this.lineage = new LinkedList<>();
    }

    @Override
    public String getName() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSynonyms() {
        return this.synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public List<Rank> getLineage() {
        return this.lineage;
    }

    public void setLineage(List<Rank> lineage) {
        this.lineage = lineage;
    }

}
