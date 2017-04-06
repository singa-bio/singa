package de.bioforscher.singa.core.biology;

import de.bioforscher.singa.core.utility.Nameable;

/**
 * @author cl
 */
public class Taxon implements Nameable {

    private String rank;
    private String name;

    public Taxon(String rank, String name) {
        this.rank = rank;
        this.name = name;
    }

    public Taxon(String name) {
        this.rank = "Unknown";
        this.name = name;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
