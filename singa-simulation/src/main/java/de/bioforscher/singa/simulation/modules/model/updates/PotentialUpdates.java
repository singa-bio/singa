package de.bioforscher.singa.simulation.modules.model.updates;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.BioNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public final class PotentialUpdates {

    private PotentialUpdates() {

    }

    public static List<PotentialUpdate> collectChanges(BioNode node) {
        List<PotentialUpdate> updates = new ArrayList<>();
        for (CellSection section: node.getAllReferencedSections()) {
            for (ChemicalEntity entity: node.getAllReferencedEntities()) {
                updates.add(new PotentialUpdate(node, section, entity, node.getAvailableConcentration(entity, section)));
            }
        }
        return updates;
    }

}
