package bio.singa.chemistry.annotations.taxonomy;

import bio.singa.features.identifiers.NCBITaxonomyIdentifier;
import bio.singa.features.identifiers.model.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * The organism class is used to store information about organisms, such as {@link NCBITaxonomyIdentifier}, names, and
 * linage.
 *
 * @author cl
 */
public class Organism implements Identifiable<NCBITaxonomyIdentifier> {

    /**
     * The identifier used by the NCBI taxonomy databse.
     */
    private NCBITaxonomyIdentifier identifier;

    /**
     * The scientific name.
     */
    private String scientificName;

    /**
     * The common name.
     */
    private String commonName;

    /**
     * The linage of this organism.
     */
    private List<Taxon> lineage;

    /**
     * Creates a new organism with the given scientific name.
     *
     * @param scientificName A scientific name.
     */
    public Organism(String scientificName) {
        this.scientificName = scientificName;
        lineage = new ArrayList<>();
    }

    /**
     * Returns the scientific name (such as "Sus scrofa").
     *
     * @return The scientific name.
     */
    public String getScientificName() {
        return scientificName;
    }

    /**
     * Sets the scientific name (such as "Sus scrofa").
     *
     * @param scientificName The scientific name.
     */
    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    /**
     * Returns a common name (such as "wild boar").
     *
     * @return The common name.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Sets a common name (such as "wild boar").
     *
     * @param commonName The common name.
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @Override
    public NCBITaxonomyIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the {@link NCBITaxonomyIdentifier}.
     *
     * @param identifier The identifier.
     */
    public void setIdentifier(NCBITaxonomyIdentifier identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the linage as a list of {@link Taxon}s.
     *
     * @return The linage.
     */
    public List<Taxon> getLineage() {
        return lineage;
    }

    /**
     * Sets the linage for this organism.
     *
     * @param lineage The linage.
     */
    public void setLineage(List<Taxon> lineage) {
        this.lineage = lineage;
    }

}
