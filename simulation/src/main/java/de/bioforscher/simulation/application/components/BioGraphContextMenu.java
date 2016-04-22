package de.bioforscher.simulation.application.components;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.simulation.model.AutomatonGraph;
import de.bioforscher.simulation.util.BioGraphUtilities;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * The class represents a context menu that is able to manipulate the graph and
 * its rendering.
 *
 * @author Christoph Leberecht
 */
public class BioGraphContextMenu extends ContextMenu {

    private AutomatonGraph graph;
    private SimulationCanvas owner;

    private Menu speciesMenu;
    private ToggleGroup speciesGroup;

    public BioGraphContextMenu(AutomatonGraph graph, SimulationCanvas canvas) {
        this.graph = graph;
        this.owner = canvas;
        initialize();
    }

    public void initialize() {
        this.speciesMenu = new Menu("Highlight Species");
        this.speciesGroup = new ToggleGroup();
        HashMap<String, Species> speciesMap = BioGraphUtilities.generateMapOfEntities(this.graph);
        // Add MenuItem for every Species
        if (!speciesMap.isEmpty()) {
            fillSpeciesMenu(speciesMap);
        } else {
            RadioMenuItem itemCompound = new RadioMenuItem("No species to highlight.");
            itemCompound.setUserData(null);
            itemCompound.setToggleGroup(this.speciesGroup);
            this.speciesMenu.getItems().add(itemCompound);
        }

        // Add Items
        this.getItems().add(this.speciesMenu);
    }

    public void fillSpeciesMenu(HashMap<String, Species> speciesMap) {
        for (Entry<String, Species> species : speciesMap.entrySet()) {
            RadioMenuItem speciesMenuItem = setupSpeciesMenuItem(species.getValue());
            this.speciesMenu.getItems().add(speciesMenuItem);
        }
    }

    public RadioMenuItem setupSpeciesMenuItem(Species species) {
        RadioMenuItem itemCompound = new RadioMenuItem(species.getName());
        itemCompound.setUserData(species);
        itemCompound.setToggleGroup(this.speciesGroup);
        // Add action listener
        // TODO convert to method reference
        itemCompound.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                BioGraphContextMenu.this.owner.getRenderer().getBioRenderingOptions().setNodeHighlightSpecies(species);
                BioGraphContextMenu.this.owner.getRenderer().getBioRenderingOptions().setEdgeHighlightSpecies(species);
                BioGraphContextMenu.this.owner.draw();
            }
        });
        return itemCompound;
    }

    public SimulationCanvas getOwner() {
        return this.owner;
    }

    public void setCanvas(SimulationCanvas canvas) {
        this.owner = canvas;
    }

    public AutomatonGraph getGraph() {
        return this.graph;
    }

    public void setGraph(AutomatonGraph graph) {
        this.graph = graph;
        initialize();
    }

}
