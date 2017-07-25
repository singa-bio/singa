package de.bioforscher.singa.simulation.modules.reactions.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.ReactionRate;
import de.bioforscher.singa.features.units.UnitProvider;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.modules.model.AbstractNeighbourIndependentModule;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Reactions module defines the entirety of chemical conversions that take place in the
 * {@link Simulation Simulation}. All Reactions are calculated according to
 * their specified {@link KineticLaw KineticLaws}s.
 */
public class Reactions extends AbstractNeighbourIndependentModule {

    private List<Reaction> reactions;
    private Map<ChemicalEntity<?>, Quantity<ReactionRate>> velocities;

    public Reactions(Simulation simulation) {
        super(simulation);
        this.reactions = new ArrayList<>();
        this.velocities = new HashMap<>();
        applyAlways();
        addDeltaFunction(this::calculateDelta, bioNode -> true);
    }

    public List<Reaction> getReactions() {
        return this.reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public Delta calculateDelta(ConcentrationContainer concentrations) {
        this.velocities.clear();
        // calculate acceleration for each reaction
        Map<Reaction, Quantity<ReactionRate>> reactionRates = new HashMap<>();
        // assign reaction rates
        this.reactions.forEach(reaction -> reactionRates.put(reaction, reaction.calculateAcceleration(getCurrentNode(), getCurrentCellSection())));
        // apply acceleration to the reactants of each reaction resulting in the velocity of the concentration change
        this.reactions.forEach(reaction ->
                reaction.getStoichiometricReactants().forEach(reactant -> {
                    if (reactant.isSubstrate()) {
                        // substrates are consumed and acceleration is therefore negative
                        updateVelocity(reactant, reactionRates.get(reaction).multiply(-1));
                    } else {
                        // products are created and acceleration is therefore positive
                        updateVelocity(reactant, reactionRates.get(reaction));
                    }
                }));
        // update every concentration using the calculateUpdateMethod
        return new Delta(getCurrentCellSection(), getCurrentChemicalEntity(), Quantities.getQuantity(this.velocities.get(getCurrentChemicalEntity()).getValue(), UnitProvider.MOLE_PER_LITRE));
    }

    private void updateVelocity(StoichiometricReactant reactant, Quantity<ReactionRate> acceleration) {
        if (!this.velocities.containsKey(reactant.getEntity())) {
            // if species is not in map put it
            this.velocities.put(reactant.getEntity(), acceleration.multiply(reactant.getStoichiometricNumber()));
        } else {
            // else compute the new value
            this.velocities.compute(reactant.getEntity(), (entity, velocity) ->
                    velocity.add(acceleration.multiply(reactant.getStoichiometricNumber())));
        }
    }

}
