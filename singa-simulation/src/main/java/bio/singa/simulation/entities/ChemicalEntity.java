package bio.singa.simulation.entities;

import bio.singa.features.identifiers.model.Identifier;
import bio.singa.features.model.Featureable;
import bio.singa.structure.features.molarmass.MolarMass;

import java.util.List;

/**
 * Chemical Entity is an abstract class that provides the common features of all chemical substances on a descriptive
 * level. Each chemical entity should be identifiable by an
 * {@link Identifier}. Chemical entities can be annotated, posses a {@link MolarMass} and a name.
 *
 * @author cl
 * @see <a href="https://de.wikipedia.org/wiki/Simplified_Molecular_Input_Line_Entry_Specification">Wikipedia:
 * SMILES</a>
 */
public interface ChemicalEntity extends Featureable {

    String getIdentifier();

    boolean isMembraneBound();

    void setMembraneBound(boolean membraneBound);

    boolean isSmall();

    List<Identifier> getAllIdentifiers();

}