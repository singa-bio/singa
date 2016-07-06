package de.bioforscher.simulation.application.components;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.model.GraphAutomaton;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

import java.util.Map;
import java.util.Map.Entry;

/**
 * The class represents a context menu that is able to manipulate the graph and
 * its rendering.
 *
 * @author Christoph Leberecht
 */
class BioGraphContextMenu extends ContextMenu {

    private final SimulationCanvas owner;
    private GraphAutomaton graphAutomaton;
    private Menu speciesMenu;
    private ToggleGroup speciesGroup;

    BioGraphContextMenu(GraphAutomaton graphAutomaton, SimulationCanvas canvas) {
        this.graphAutomaton = graphAutomaton;
        this.owner = canvas;
        initialize();
    }

    private void initialize() {
        this.speciesMenu = new Menu("Highlight Species");
        this.speciesGroup = new ToggleGroup();
        Map<String, ChemicalEntity> chemicalEntities = graphAutomaton.getSpecies();
        // Add MenuItem for every Species
        if (!chemicalEntities.isEmpty()) {
            fillSpeciesMenu(chemicalEntities);
        } else {
            RadioMenuItem itemCompound = new RadioMenuItem("No species to highlight.");
            itemCompound.setUserData(null);
            itemCompound.setToggleGroup(this.speciesGroup);
            this.speciesMenu.getItems().add(itemCompound);
        }

        // Add Items
        this.getItems().add(this.speciesMenu);
    }

    private void fillSpeciesMenu(Map<String, ChemicalEntity> speciesMap) {
        for (Entry<String, ChemicalEntity> species : speciesMap.entrySet()) {
            RadioMenuItem speciesMenuItem = setupSpeciesMenuItem(species.getValue());
            this.speciesMenu.getItems().add(speciesMenuItem);
        }
    }

    private RadioMenuItem setupSpeciesMenuItem(final ChemicalEntity species) {
        RadioMenuItem itemCompound = new RadioMenuItem(species.getName());
        itemCompound.setUserData(species);
        itemCompound.setToggleGroup(this.speciesGroup);
        itemCompound.setOnAction(t -> {
            BioGraphContextMenu.this.owner.getRenderer().getBioRenderingOptions().setNodeHighlightSpecies(species);
            BioGraphContextMenu.this.owner.getRenderer().getBioRenderingOptions().setEdgeHighlightSpecies(species);
            BioGraphContextMenu.this.owner.draw();
        });
        return itemCompound;
    }

    public GraphAutomaton getGraphAutomaton() {
        return this.graphAutomaton;
    }

    public void setGraphAutomaton(GraphAutomaton graphAutomaton) {
        this.graphAutomaton = graphAutomaton;
    }

}
