package de.bioforscher.singa.simulation.application.components.controlpanles;

import de.bioforscher.singa.simulation.application.components.cells.SectionCell;
import de.bioforscher.singa.simulation.application.renderer.ColorManager;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.modules.model.Simulation;
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

    private ObservableList<CellSection> observedCompartments = FXCollections.observableArrayList();

    private ListView<CellSection> compartmentList = new ListView<>();

    public CompartmentControlPanel(Simulation simulation) {
        initializeData(simulation.getGraph().getSections());
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
        this.compartmentList.setCellFactory(param -> new SectionCell());
        this.compartmentList.setItems(this.observedCompartments);
    }


    public CellSection getSelectedCellSection() {
        return this.compartmentList.getSelectionModel().getSelectedItem();
    }

    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        this.compartmentList.setPrefWidth(width);
        this.compartmentList.setPrefHeight(height);
    }

    private void initializeData(Collection<CellSection> cellSections) {
        cellSections.forEach((cellSection) -> {
            this.observedCompartments.add(cellSection);
            ColorManager.getInstance().setColor(cellSection, ColorManager.generateRandomColor());
        });
    }

    public void updateData(Collection<CellSection> cellSections) {
        cellSections.forEach((cellSection) -> {
            if (!this.observedCompartments.contains(cellSection)) {
                this.observedCompartments.add(cellSection);
                ColorManager.getInstance().setColor(cellSection, ColorManager.generateRandomColor());
            }
        });
    }

    private void addControlsToGrid() {
        this.add(this.compartmentList, 0, 0);
    }

}
