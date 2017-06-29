package de.bioforscher.singa.simulation.modules.reactions.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.features.quantities.ReactionRate;
import de.bioforscher.singa.features.units.UnitProvider;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationDelta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.Simulation;
import de.bioforscher.singa.simulation.modules.reactions.implementations.kineticLaws.model.KineticLaw;
import tec.units.ri.quantity.Quantities;

import javax.measure.Quantity;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Reactions module defines the entirety of chemical conversions that take place in the
 * {@link Simulation Simulation}. All Reactions are calculated according to
 * their specified {@link KineticLaw KineticLaws}s
 * and new concentrations are set using the {@link Reactions#applyTo(AutomatonGraph)} method.
 */
public class Reactions implements Module {

    private List<Reaction> reactions;
    private Map<ChemicalEntity<?>, Quantity<ReactionRate>> velocities;

    public Reactions() {
        this.reactions = new ArrayList<>();
        this.velocities = new HashMap<>();
    }

    public List<Reaction> getReactions() {
        return this.reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    @Override
    public void applyTo(AutomatonGraph graph) {
        for (BioNode node : graph.getNodes()) {
            calculateDeltas(node);
        }
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return this.reactions.stream()
                .map(Reaction::collectAllReferencedEntities)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public void calculateDeltas(BioNode node) {
        // TODO calculate acceleration also based on compartments
        // for each cell section
        for (CellSection section : node.getAllReferencedSections()) {
            // for each entity
            for (ChemicalEntity entity : node.getAllReferencedEntities()) {
                // calculate acceleration for each reaction
                Map<Reaction, Quantity<ReactionRate>> reactionRates = new HashMap<>();
                // assign reaction rates
                this.reactions.forEach(reaction -> reactionRates.put(reaction, reaction.calculateAcceleration(node, section)));
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
                Quantity<MolarConcentration> updatedQuantity = node.getConcentration(entity)
                        .add(Quantities.getQuantity(this.velocities.get(entity).getValue(), UnitProvider.MOLE_PER_LITRE));
                node.addDelta(new ConcentrationDelta(section, entity, updatedQuantity));
            }
        }
        this.velocities.clear();
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
