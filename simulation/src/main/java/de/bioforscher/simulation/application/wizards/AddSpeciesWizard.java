package de.bioforscher.simulation.application.wizards;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.components.SpeciesCell;
import de.bioforscher.simulation.application.windows.SearchSpeciesPane;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 05.07.2016.
 */
public class AddSpeciesWizard extends Wizard {

    private Stage owner;
    private BioGraphSimulation simulation;
    private Set<Species> speciesToAdd;

    public AddSpeciesWizard(Stage owner, BioGraphSimulation simulation) {
        super(new ChooseSpeciesMethodPage(), new SearchSpeciesPage());
        this.owner = owner;
        this.simulation = simulation;
    }

    @Override
    public void finish() {
        this.owner.close();
        if (this.getCurrentPage().getClass().equals(SearchSpeciesPage.class)) {
            this.speciesToAdd = (((SearchSpeciesPage) this.getCurrentPage()).prepareSelectedSpecies());
        }
    }

    @Override
    public void cancel() {
        this.owner.close();
    }

    public BioGraphSimulation getSimulation() {
        return this.simulation;
    }

    public void setSimulation(BioGraphSimulation simulation) {
        this.simulation = simulation;
    }

    public Set<Species> getSpeciesToAdd() {
        return this.speciesToAdd;
    }

    public void setSpeciesToAdd(Set<Species> speciesToAdd) {
        this.speciesToAdd = speciesToAdd;
    }

}

class ChooseSpeciesMethodPage extends WizardPage {

    private RadioButton rbChEBI;
    private RadioButton rbManual;
    private ToggleGroup tgMethods = new ToggleGroup();

    public ChooseSpeciesMethodPage() {
        super("Choose a method to add species.");
        setDescription("The Species can either be created using data from ChEBI Database or it can be described " +
                "manually using the graphical user interface.");

        this.nextButton.setDisable(true);
        this.finishButton.setDisable(true);

        this.rbChEBI.setToggleGroup(this.tgMethods);
        this.rbManual.setToggleGroup(this.tgMethods);

        this.tgMethods.selectedToggleProperty().addListener(
                (observableValue, oldToggle, newToggle) -> {
                    this.nextButton.setDisable(false);
                });

    }

    @Override
    public Parent getContent() {
        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);
        content.setPadding(new Insets(10, 10, 10, 10));
        this.rbChEBI = new RadioButton("Search species from ChEBI Database.");
        this.rbManual = new RadioButton("Create manually using the user interface.");
        content.add(this.rbChEBI, 0, 0);
        content.add(this.rbManual, 0, 1);
        return content;
    }

    @Override
    public void nextPage() {
        if (this.tgMethods.getSelectedToggle().equals(this.rbChEBI)) {
            super.nextPage();
        }
    }
}

class SearchSpeciesPage extends WizardPage {

    private SplitPane split;
    private SearchSpeciesPane searchPane;

    public SearchSpeciesPage() {
        super("Search Species...");
        setDescription("Type in your search term into the text box below. ");
    }

    @Override
    public Parent getContent() {
        // left half
        this.searchPane = new SearchSpeciesPane(3);
        // right half
        ListView<Species> speciesList = new ListView<>();
        speciesList.setCellFactory(param -> new SpeciesCell());
        speciesList.setItems(this.searchPane.getSelectedSpecies());
        // create SplitPane
        this.split = new SplitPane(this.searchPane, speciesList);
        this.split.setPrefHeight(530);
        SplitPane.setResizableWithParent(this.split, Boolean.FALSE);
        return this.split;
    }

    public Set<Species> prepareSelectedSpecies() {
        return this.searchPane.getSelectedSpecies().stream().collect(Collectors.toSet());
    }


}