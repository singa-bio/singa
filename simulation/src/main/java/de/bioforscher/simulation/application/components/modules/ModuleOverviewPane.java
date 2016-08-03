package de.bioforscher.simulation.application.components.modules;

import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.modules.AvailableModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

/**
 * Created by Christoph on 03.08.2016.
 */
public class ModuleOverviewPane extends GridPane {

    private BioGraphSimulation owner;
    ListView<AvailableModule> moduleList = new ListView<>();

    private ObservableList<AvailableModule> modules = FXCollections.observableArrayList();

    public ModuleOverviewPane(BioGraphSimulation owner) {
        this.owner = owner;
        this.modules.addAll(AvailableModule.values());
        initializeModuleList();
        addComponentsToGrid();
    }

    private void initializeModuleList() {
        this.moduleList.setCellFactory(module -> new ModuleCell());
        this.moduleList.setItems(this.modules);
        this.moduleList.setPrefWidth(150);
    }

    private void addComponentsToGrid() {
        this.add(this.moduleList, 0, 0);
    }


}
