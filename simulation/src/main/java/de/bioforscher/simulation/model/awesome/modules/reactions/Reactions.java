package de.bioforscher.simulation.model.awesome.modules.reactions;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.model.awesome.modules.model.ImmediateUpdateBehavior;
import de.bioforscher.simulation.model.awesome.modules.model.Module;
import de.bioforscher.simulation.model.awesome.modules.model.PotentialUpdate;

/**
 * Created by Christoph on 06.07.2016.
 */
public class Reactions implements Module, ImmediateUpdateBehavior {

    @Override
    public void applyTo(AutomatonGraph graph) {

    }

    @Override
    public PotentialUpdate calculateUpdate(BioNode node, ChemicalEntity entity) {
        return null;
    }

}
