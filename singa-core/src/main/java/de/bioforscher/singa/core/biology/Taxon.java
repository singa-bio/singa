package de.bioforscher.singa.core.biology;

/**
 * A taxon is usually known by a particular name and given a particular ranking.
 * <p>
 * A taxonomic rank is the relative level of a group of organisms in a taxonomic hierarchy.
 * Examples of taxonomic ranks are species, genus, family, order, class, phylum, kingdom, domain, etc.
 * <p>
 * The name is a descriptive term for this group, its hierarchical clade.
 *
 * @author cl
 */
public class Taxon {

    /**
     * A taxonomic rank is the relative level of a group of organisms in a taxonomic hierarchy.
     */
    private String rank;

    /**
     * The name is a descriptive term for this group, its hierarchical clade.
     */
    private String name;

    /**
     * Creates a new Taxon with rank and name.
     *
     * @param rank The rank.
     * @param name The name.
     */
    public Taxon(String rank, String name) {
        this.rank = rank;
        this.name = name;
    }

    /**
     * Creates a taxon with unknown or unspecified rank.
     *
     * @param name The name.
     */
    public Taxon(String name) {
        this("Unknown", name);
    }

    /**
     * Returns the rank of this taxon.
     *
     * @return The rank of this taxon.
     */
    public String getRank() {
        return rank;
    }

    /**
     * Sets the rank of this taxon.
     *
     * @param rank The rank.
     */
    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this taxon.
     *
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

}
