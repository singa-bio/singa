package de.bioforscher.simulation.application.components;

import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.util.BioGraphUtilities;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;

public class BioNodeContextMenu extends ContextMenu {

    private BioNode node;
    private BioGraphSimulation owner;

    public BioNodeContextMenu(BioNode node, BioGraphSimulation owner) {
        this.node = node;
        this.owner = owner;
        initialize();
    }

    public void initialize() {
        // Header
        Text headerText = new Text(this.node.toString());
        headerText.setStyle("-fx-font-weight: bold;");
        CustomMenuItem header = new CustomMenuItem(headerText);
        // Delete
        MenuItem deleteNode = new MenuItem("Delete");
        deleteNode.setOnAction(this::deleteNode);
        // Observe
        MenuItem observeNode = new MenuItem("Observe");
        observeNode.setOnAction(this::observeNode);
        // Add Items
        this.getItems().addAll(header, deleteNode, observeNode);
    }

    public void deleteNode(ActionEvent event) {
        this.owner.getGraph().removeNode(this.node.getIdentifier());
        this.owner.redrawGraph();
    }

    public void observeNode(ActionEvent event) {
        this.node.setObserved(true);
        SpeciesObserverChart newChart = SpeciesObserverChartFactory.setupSpeciesObserverChart(this.node,
                BioGraphUtilities.generateMapOfEntities(this.owner.getGraph()));
        this.owner.getCharts().add(newChart);
        this.owner.getChartContainer().getChildren().add(newChart);
        this.owner.getChartContainer().setPrefWidth(newChart.getWidth());
        this.owner.redrawGraph();
    }

    public BioNode getNode() {
        return this.node;
    }

    public void setNode(BioNode node) {
        this.node = node;
    }

}
