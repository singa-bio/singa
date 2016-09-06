package de.bioforscher.core.biology;

/**
 * Created by Christoph on 14.07.2016.
 */
public class Rank {

    private String taxon;
    private String name;

    public Rank(String taxon, String name) {
        this.taxon = taxon;
        this.name = name;
    }

    public String getTaxon() {
        return this.taxon;
    }

    public void setTaxon(String taxon) {
        this.taxon = taxon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
