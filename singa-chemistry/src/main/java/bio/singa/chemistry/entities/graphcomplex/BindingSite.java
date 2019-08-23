package bio.singa.chemistry.entities.graphcomplex;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.core.utility.Pair;

import java.util.Objects;

/**
 * @author cl
 */
public class BindingSite {

    String name;

    public static BindingSite createNamed(String bindingSiteName) {
        return new BindingSite(bindingSiteName);
    }

    public static BindingSite forPair(ChemicalEntity first, ChemicalEntity second) {
        return new BindingSite(first.getIdentifier()+"-"+second.getIdentifier());
    }

    public static BindingSite forPair(Pair<ChemicalEntity> pair) {
        return forPair(pair.getFirst(), pair.getSecond());
    }

    private BindingSite(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindingSite that = (BindingSite) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
