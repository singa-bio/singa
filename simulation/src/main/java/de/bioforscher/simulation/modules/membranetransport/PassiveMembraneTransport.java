package de.bioforscher.simulation.modules.membranetransport;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.simulation.model.graphs.BioNode;
import de.bioforscher.simulation.modules.model.Module;
import de.bioforscher.simulation.modules.model.updates.CumulativeUpdateBehavior;
import de.bioforscher.simulation.modules.model.updates.PotentialUpdate;

import java.util.Set;

/**
 * @author cl
 */
public class PassiveMembraneTransport implements Module, CumulativeUpdateBehavior {

    @Override
    public void applyTo(AutomatonGraph graph) {
        
    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencesEntities() {
        return null;
    }

    @Override
    public PotentialUpdate calculateUpdate(BioNode node, ChemicalEntity entity) {
        return null;
    }
}
