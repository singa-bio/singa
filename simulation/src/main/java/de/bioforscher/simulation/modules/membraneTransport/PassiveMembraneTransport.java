package de.bioforscher.simulation.modules.membraneTransport;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.model.CumulativeUpdateBehavior;
import de.bioforscher.simulation.modules.model.Module;
import de.bioforscher.simulation.modules.model.PotentialUpdate;

import java.util.Set;

/**
 * Created by Christoph on 17/11/2016.
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
