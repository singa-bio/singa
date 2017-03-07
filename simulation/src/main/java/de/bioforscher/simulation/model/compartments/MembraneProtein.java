package de.bioforscher.simulation.model.compartments;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.descriptive.Protein;
import de.bioforscher.core.identifier.SimpleStringIdentifier;
import de.bioforscher.units.quantities.Permeability;

import java.util.Map;

/**
 * @author cl
 */
public class MembraneProtein extends Protein {

    private Compartment sourceCompartment;
    private Compartment targetCompartment;

    private Map<ChemicalEntity, Permeability> basePermeability;

    /**
     * Creates a new Chemical Entity with the given identifier.
     *
     * @param identifier The identifier.
     */
    public MembraneProtein(SimpleStringIdentifier identifier) {
        super(identifier);
    }

    // TODO maybe use http://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class

}
