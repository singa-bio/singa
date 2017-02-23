package de.bioforscher.simulation.application.components.controlpanles;

import de.bioforscher.simulation.application.components.cells.CompartmentCell;
import de.bioforscher.simulation.application.renderer.ColorManager;
import de.bioforscher.simulation.model.compartments.Compartment;
import de.bioforscher.simulation.modules.model.Simulation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.util.Collection;

/**
 * @author cl
 */
public class CompartmentControlPanel extends GridPane {

    private ObservableList<Compartment> observedCompartments = FXCollections.observableArrayList();

    private ListView<Compartment> compartmentList = new ListView<>();

    public CompartmentControlPanel(Simulation simulation) {
        initializeData(simulation.getGraph().getCompartments());
        configureGrid();
        configureCompartmentList();
        addControlsToGrid();
    }

    private void configureGrid() {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10, 10, 10, 10));
    }

    private void configureCompartmentList() {
        this.compartmentList.setCellFactory(param -> new CompartmentCell());
        this.compartmentList.setItems(this.observedCompartments);
    }


    public Compartment getSelectedCompartment() {
        return this.compartmentList.getSelectionModel().getSelectedItem();
    }

    @Override
    public void resize(double width,double height) {
        super.resize(width, height);
        this.compartmentList.setPrefWidth(width);
        this.compartmentList.setPrefHeight(height);
    }

    private void initializeData(Collection<Compartment> compartments) {
        compartments.forEach((compartment) -> {
            this.observedCompartments.add(compartment);
            ColorManager.getInstance().setColor(compartment, ColorManager.generateRandomColor());
        });
    }

    private void addControlsToGrid() {
        this.add(this.compartmentList, 0, 0);
    }

}
