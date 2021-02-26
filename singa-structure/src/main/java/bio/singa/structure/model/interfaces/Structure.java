package bio.singa.structure.model.interfaces;

import bio.singa.features.identifiers.PDBIdentifier;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Structures represent chemical objects in three dimensional space. <br> <br> Structures may contain one ore more
 * {@link Model}s that represent the same structure but in different situations (e.g. at different time steps for MD
 * trajectories or dynamics for NMR) <br> Structures may contain one or more {@link Chain}s that represent one
 * continuous macro molecule (most often the biggest macro molecules and its ligands are collected in one model).
 *
 * @author cl
 */
public interface Structure extends LeafSubstructureContainer, ChainContainer {

    /**
     * Returns the PDB identifier, a 4 character code, where the first character is any number followed by three
     * alphanumeric characters.
     *
     * @return The PDB identifier.
     * @see PDBIdentifier
     */
    String getPdbIdentifier();

    /**
     * Returns the title.
     *
     * @return The title of the structure.
     */
    String getTitle();

    /**
     * Returns all {@link Model}s.
     *
     * @return All models.
     */
    List<Model> getAllModels();

    /**
     * Returns all model identifiers referenced in the model.
     *
     * @return All model identifiers referenced in the model.
     */
    Set<Integer> getAllModelIdentifiers();

    /**
     * Returns the first {@link Model} (with the smallest identifier).
     *
     * @return The first model.
     */
    Model getFirstModel();

    /**
     * Returns an {@link Optional} of the {@link Model} with the given identifier. If no model with the identifier could
     * be found, an empty optional is returned.
     *
     * @param modelIdentifier The identifier of the model.
     * @return An {@link Optional} encapsulating the {@link Model}.
     */
    Optional<Model> getModel(int modelIdentifier);

    /**
     * Removes a {@link Model} with the given identifier from the structure.
     *
     * @param modelIdentifier The identifier of the model.
     */
    void removeModel(int modelIdentifier);

    /**
     * Returns an {@link Optional} of the {@link Chain} with the given identifier from the model with the given
     * identifier. If no chain or model with the identifier could be found, an empty optional is returned.
     *
     * @param modelIdentifier The identifier of the model.
     * @param chainIdentifier The identifier of the chain.
     * @return An {@link Optional} encapsulating the {@link Chain}.
     */
    Optional<Chain> getChain(int modelIdentifier, String chainIdentifier);

    double getResolution();

    /**
     * Returns a copy of this structure.
     *
     * @return A copy of this structure.
     */
    Structure getCopy();

    default String flatToString() {
        return getPdbIdentifier();
    }

}
