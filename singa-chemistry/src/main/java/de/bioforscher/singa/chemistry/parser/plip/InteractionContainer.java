package de.bioforscher.singa.chemistry.parser.plip;

import de.bioforscher.singa.chemistry.descriptive.elements.ElementProvider;
import de.bioforscher.singa.chemistry.physical.model.Structure;
import de.bioforscher.singa.mathematics.vectors.Vector3D;

import java.util.ArrayList;

/**
 * @author cl
 */
public class InteractionContainer {

    private ArrayList<Interaction> interactions;

    public InteractionContainer() {
        this.interactions = new ArrayList<>();
    }

    public void addInteraction(Interaction interaction) {
        this.interactions.add(interaction);
    }

    public void convertToEdgeFor(Structure structure) {
        // for now add some pseudo atoms
        for (Interaction interaction : this.interactions) {
            Vector3D centroid = interaction.getLigandCoordiante().add(interaction.getProteinCoordinate()).multiply(0.5);
            structure.addAtom(0, interaction.getSource().getChainIdentifier(), ElementProvider.ARSENIC, interaction.getClass().getSimpleName(), centroid);
        }
    }

}
