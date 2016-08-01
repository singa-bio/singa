package de.bioforscher.simulation.application.components;

import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.components.plots.ConcentrationPlot;
import de.bioforscher.simulation.application.components.plots.PlotCard;
import de.bioforscher.simulation.model.BioNode;
import de.bioforscher.simulation.modules.model.Simulation;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;

public class BioNodeContextMenu extends ContextMenu {

    private final BioGraphSimulation owner;
    private BioNode node;

    private CustomMenuItem header = new CustomMenuItem();
    private MenuItem delete = new MenuItem();
    private MenuItem observe = new MenuItem();

    public BioNodeContextMenu(BioNode node, BioGraphSimulation owner) {
        this.node = node;
        this.owner = owner;
        configureHeader();
        configureDeleteItem();
        configureObserve();
        addItemsToMenu();
    }

    private void configureHeader() {
        Text headerText = new Text(this.node.toString());
        headerText.setStyle("-fx-font-weight: bold;");
        this.header.setContent(headerText);
    }

    private void configureDeleteItem() {
        this.delete.setText("Delete");
        this.delete.setOnAction(this::deleteNode);
    }

    private void configureObserve() {
        this.observe.setText("Observe");
        this.observe.setOnAction(this::observeNode);
    }

    private void addItemsToMenu() {
        this.getItems().addAll(this.header, this.delete, this.observe);
    }

   private void deleteNode(ActionEvent event) {
        this.owner.getGraph().removeNode(this.node.getIdentifier());
        this.owner.redrawGraph();
    }

    private void observeNode(ActionEvent event) {
        this.node.setObserved(true);
        Simulation simulation = this.owner.getSimulation();
        ConcentrationPlot plot = new ConcentrationPlot(simulation.getSpecies(), this.node, simulation);
        simulation.getListeners().add(plot);
        this.owner.getPlotPane().getPlotCards().add(new PlotCard(plot));
        this.owner.redrawGraph();
    }

    public BioNode getNode() {
        return this.node;
    }

    public void setNode(BioNode node) {
        this.node = node;
    }

}
