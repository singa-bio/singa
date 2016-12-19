package de.bioforscher.simulation.application.components.chemicalEntities;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.chemistry.parser.chebi.ChEBIImageService;
import de.bioforscher.core.identifier.ChEBIIdentifier;
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

public class ChemicalEntityCard extends GridPane {

    private ChemicalEntity chemicalEntity;
    private ImageView speciesImage;
    private VBox informationBox = new VBox();

    public ChemicalEntityCard(ChemicalEntity chemicalEntity) {
        this.chemicalEntity = chemicalEntity;
        configureGrid();
        configureImageView();
        configureInformationBox();
        addComponentsToGrid();
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
        if (this.chemicalEntity.getIdentifier() instanceof ChEBIIdentifier) {
            ChEBIImageService imageService = new ChEBIImageService(this.chemicalEntity.getIdentifier().toString());
            imageService.fetchResource();
            Image imageRepresentation = new Image(imageService.getImageStream());
            this.speciesImage = new ImageView(imageRepresentation);
        } else {
            this.speciesImage = new ImageView();
        }
    }

    private void configureInformationBox() {
        Text nameText = new Text(this.chemicalEntity.getName());
        nameText.setFont(Font.font(null, FontWeight.BOLD, 16));
        TextFlow nameFlow = new TextFlow(nameText);
        Text identifierText = new Text("Identifier: " + this.chemicalEntity.getIdentifier().toString());
        Label speciesWeight = new Label("Weight: " + this.chemicalEntity.getMolarMass().toString());
        this.informationBox.setSpacing(10);
        this.informationBox.getChildren().addAll(nameFlow, identifierText, speciesWeight);
    }

    private void addComponentsToGrid() {
        this.add(this.speciesImage, 0, 0, 1, 1);
        this.add(this.informationBox, 1, 0, 1, 1);
    }

    public ChemicalEntity getChemicalEntity() {
        return this.chemicalEntity;
    }

}
