package de.bioforscher.simulation.application.components.species;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBIImageService;
import javafx.geometry.Insets;
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

    private Species species;
    private ImageView speciesImage;
    private VBox informationBox = new VBox();

    public SpeciesCard(Species species) {
        this.species = species;
        configureGrid();
        configureImageView();
        configureInformationBox();
        addComponentsToGrid();
    }

    public SpeciesCard(ChemicalEntity species) {
        this((Species) species);
    }

    private void configureGrid() {
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setStyle("-fx-border-color: #dcdcdc;" +
                "-fx-border-radius: 5;" +
                "-fx-background-color: white;" +
                "-fx-background-radius: 5;");
        this.setMinSize(200, 100);
        this.setPrefSize(200, 100);
    }

    private void configureImageView() {
        ChEBIImageService imageService = new ChEBIImageService(this.species.getIdentifier());
        imageService.fetchResource();
        InputStream inputStream = imageService.getImageStream();
        Image speciesImage = new Image(inputStream);
        this.speciesImage = new ImageView(speciesImage);
    }

    private void configureInformationBox() {
        Text nameText = new Text(this.species.getName());
        nameText.setFont(Font.font(null, FontWeight.BOLD, 16));
        TextFlow nameFlow = new TextFlow(nameText);
        Text identifierText = new Text("Identifier: " + this.species.getIdentifier().toString());
        Label speciesWeight = new Label("Weight: " + this.species.getMolarMass().toString());
        this.informationBox.setSpacing(10);
        this.informationBox.getChildren().addAll(nameFlow, identifierText, speciesWeight);
    }

    private void addComponentsToGrid() {
        this.add(this.speciesImage, 0, 0, 1, 1);
        this.add(this.informationBox, 1, 0, 1, 1);
    }

    public Species getSpecies() {
        return this.species;
    }

}
