package de.bioforscher.singa.simulation.modules.membranetransport;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import de.bioforscher.singa.simulation.modules.model.Module;
import de.bioforscher.singa.simulation.modules.model.updates.CumulativeUpdateBehavior;
import de.bioforscher.singa.simulation.modules.model.updates.PotentialUpdate;

import java.util.List;
import java.util.Set;

/**
 * @author cl
 */
public class ActiveMembraneTransport implements Module, CumulativeUpdateBehavior {

    @Override
    public void applyTo(AutomatonGraph graph) {

    }

    @Override
    public Set<ChemicalEntity<?>> collectAllReferencedEntities() {
        return null;
    }

    @Override
    public List<PotentialUpdate> calculateUpdates(BioNode node) {
        return null;
    }

}
