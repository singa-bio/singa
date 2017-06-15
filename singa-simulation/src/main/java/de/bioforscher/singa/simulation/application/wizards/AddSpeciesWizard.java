package de.bioforscher.singa.simulation.application.wizards;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.simulation.application.BioGraphSimulation;
import de.bioforscher.singa.simulation.application.components.cells.EntityCell;
import de.bioforscher.singa.simulation.application.components.panes.SBMLSearchPane;
import de.bioforscher.singa.simulation.application.components.panes.SpeciesSearchPane;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

/**
 * @author cl
 */
public class AddSpeciesWizard extends Wizard {

    private Stage owner;
    private BioGraphSimulation simulation;
    private Set<ChemicalEntity> speciesToAdd;

    public AddSpeciesWizard(Stage owner, BioGraphSimulation simulation) {
        super(new ChooseSpeciesMethodPage(), new SpeciesFromChEBI(), new SpeciesFromSBML());
        this.owner = owner;
        this.simulation = simulation;
    }

    @Override
    public void finish() {
        this.owner.close();
        if (this.getCurrentPage().getClass().equals(SpeciesFromChEBI.class)) {
            this.speciesToAdd = (((SpeciesFromChEBI) this.getCurrentPage()).prepareSelectedSpecies());
        } else if (this.getCurrentPage().getClass().equals(SpeciesFromSBML.class)) {
            this.speciesToAdd = (((SpeciesFromSBML) this.getCurrentPage()).prepareSelectedSpecies());
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

    public Set<ChemicalEntity> getSpeciesToAdd() {
        return this.speciesToAdd;
    }

    public void setSpeciesToAdd(Set<ChemicalEntity> speciesToAdd) {
        this.speciesToAdd = speciesToAdd;
    }

}

class ChooseSpeciesMethodPage extends WizardPage {

    private RadioButton rbChEBI;
    private RadioButton rbSBML;
    private RadioButton rbManual;
    private ToggleGroup tgMethods = new ToggleGroup();

    ChooseSpeciesMethodPage() {
        super("Choose a method to add species.");
        setDescription("The Species can either be created using data from ChEBI Database or it can be described " +
                "manually using the graphical user interface.");

        this.nextButton.setDisable(true);
        this.finishButton.setDisable(true);

        this.rbChEBI.setToggleGroup(this.tgMethods);
        this.rbSBML.setToggleGroup(this.tgMethods);
        this.rbManual.setToggleGroup(this.tgMethods);

        this.tgMethods.selectedToggleProperty().addListener(
                (observableValue, oldToggle, newToggle) -> this.nextButton.setDisable(false));

    }

    @Override
    public Parent getContent() {
        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);
        content.setPadding(new Insets(10, 10, 10, 10));
        this.rbChEBI = new RadioButton("Search species from ChEBI Database.");
        this.rbSBML = new RadioButton("Using a local SBML file or from the BioModels database.");
        this.rbManual = new RadioButton("Create manually using the user interface.");
        content.add(this.rbChEBI, 0, 0);
        content.add(this.rbSBML, 0, 1);
        content.add(this.rbManual, 0, 2);
        return content;
    }

    @Override
    public void navigateToNextPage() {
        if (this.tgMethods.getSelectedToggle().equals(this.rbChEBI)) {
            navigateToPage("Species from ChEBI");
        } else if (this.tgMethods.getSelectedToggle().equals(this.rbSBML)) {
            navigateToPage("Species from SBML");
        } else {
            super.navigateToNextPage();
        }
    }
}

class SpeciesFromChEBI extends WizardPage {

    private SpeciesSearchPane searchPane;

    SpeciesFromChEBI() {
        super("Species from ChEBI");
        setDescription("Type in your search term into the text box below.");
    }

    @Override
    public Parent getContent() {
        // left half
        this.searchPane = new SpeciesSearchPane(3);
        // right half
        ListView<ChemicalEntity> speciesList = new ListView<>();
        speciesList.setCellFactory(param -> new EntityCell());
        speciesList.setItems(this.searchPane.getSelectedSpecies());
        // create SplitPane
        SplitPane split = new SplitPane(this.searchPane, speciesList);
        split.setPrefHeight(530);
        SplitPane.setResizableWithParent(split, Boolean.FALSE);
        return split;
    }

    public Set<ChemicalEntity> prepareSelectedSpecies() {
        return new HashSet<>(this.searchPane.getSelectedSpecies());
    }

}

class SpeciesFromSBML extends WizardPage {

    private SBMLSearchPane searchPane;

    public SpeciesFromSBML() {
        super("Species from SBML");
        setDescription("Choose whether to parse the species from an local SBML file or using a file from the BioModels" +
                "database");
    }

    @Override
    public Parent getContent() {
        // left half
        this.searchPane = new SBMLSearchPane(3);
        // right half
        ListView<ChemicalEntity> speciesList = new ListView<>();
        speciesList.setCellFactory(param -> new EntityCell());
        speciesList.setItems(this.searchPane.getSelectedSpecies());
        // create SplitPane
        SplitPane split = new SplitPane(this.searchPane, speciesList);
        split.setPrefHeight(530);
        SplitPane.setResizableWithParent(split, Boolean.FALSE);
        return split;
    }

    public Set<ChemicalEntity> prepareSelectedSpecies() {
        return new HashSet<>(this.searchPane.getSelectedSpecies());
    }

}

