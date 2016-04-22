package de.bioforscher.simulation.application.components;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIImageService;
import de.bioforscher.chemistry.parser.ChEBIParserService;
import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.util.BioGraphUtilities;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.InputStream;

public class SpeciesCard extends GridPane {

    private BioGraphSimulation owner;
    private Species species;

    private TextFlow speciesName;
    private Label speciesWeigth;
    private ImageView speciesImage;
    private Button addButton;

    public SpeciesCard(String chEBIIdentifier, BioGraphSimulation owner) {
        this.owner = owner;
        ChEBIParserService chebiService = new ChEBIParserService();
        chebiService.setResource(chEBIIdentifier);
        this.species = chebiService.fetchSpecies();
        initialize();
    }

    public SpeciesCard(Species species, BioGraphSimulation owner) {
        this.owner = owner;
        this.species = species;
        initialize();
    }

    public void initialize() {

        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10, 10, 10, 10));

        this.setStyle("-fx-border-color: black;");
        this.setPrefWidth(300);

        setUpSpeciesImageView();
        this.add(this.speciesImage, 0, 0, 1, 1);

        VBox informationBox = new VBox(10);
        Text nameText = new Text(this.species.getName());
        nameText.setFont(Font.font(null, FontWeight.BOLD, 18));

        this.speciesName = new TextFlow(nameText);
        informationBox.getChildren().add(this.speciesName);

        this.speciesWeigth = new Label("Weigth: " + this.species.getMolarMass().toString());
        informationBox.getChildren().add(this.speciesWeigth);

        this.add(informationBox, 1, 0, 1, 1);

        this.addButton = new Button("+");
        this.addButton.setOnAction(this::addSpeciesToAutomaton);

        this.add(this.addButton, 2, 1, 1, 1);

    }

    private void setUpSpeciesImageView() {
        ChEBIImageService imageService = new ChEBIImageService(this.species.getIdentifier());
        imageService.fetchResource();
        InputStream inputStream = imageService.getImageStream();
        Image speciesImage = new Image(inputStream);
        this.speciesImage = new ImageView(speciesImage);
    }

    private void addSpeciesToAutomaton(ActionEvent event) {
        BioGraphUtilities.fillGraphWithSpecies(this.owner.getAutomata().getGraph(), this.species, 1.0);
        this.owner.resetGraphContextMenu();
        this.addButton.setDisable(true);
    }

}
