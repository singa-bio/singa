package de.bioforscher.singa.simulation.application.components.cards;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.annotations.Annotation;
import de.bioforscher.singa.chemistry.parser.chebi.ChEBIImageService;
import de.bioforscher.singa.core.identifier.ChEBIIdentifier;
import de.bioforscher.singa.core.identifier.model.Identifier;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.measure.Quantity;
import java.util.List;

/**
 * @author cl
 */
public class GeneralEntityCard<EntityType extends ChemicalEntity<?>> extends GridPane {

    private EntityType chemicalEntity;

    private ImageView imageView = new ImageView();
    private Label primaryName = new Label();
    private Label primaryIdentifier = new Label();
    private Label weight = new Label();

    private TreeView<String> annotationsTree;
    private TreeItem<String> nameAnnotations = new TreeItem<>("Additional names");
    private TreeItem<String> identifiersAnnotations = new TreeItem<>("Additional identifiers");
    private TreeItem<String> organismAnnotation = new TreeItem<>("Organisms");
    private TreeItem<String> functionAnnotation = new TreeItem<>("Function");
    private TreeItem<String> otherAnnotation = new TreeItem<>("Other");


    public GeneralEntityCard(EntityType chemicalEntity) {
        this.chemicalEntity = chemicalEntity;

        configureGrid();
        configureImageView();

        configurePrimaryName(this.chemicalEntity.getName());
        configurePrimaryIdentifier(this.chemicalEntity.getIdentifier());
        configureWeight(this.chemicalEntity.getMolarMass());
        configureAnnotationsTree(this.chemicalEntity.getAnnotations());
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
        this.setPrefSize(200, 500);
    }

    private void configureImageView() {
        if (ChEBIIdentifier.PATTERN.matcher(this.chemicalEntity.getIdentifier().toString()).matches()) {
            this.imageView = new ImageView(retrieveImage(this.chemicalEntity.getIdentifier()));
            return;
        } else {
            for (Identifier identifier : this.chemicalEntity.getAdditionalIdentifiers()) {
                if (ChEBIIdentifier.PATTERN.matcher(identifier.toString()).matches()) {
                    this.imageView = new ImageView(retrieveImage(identifier));
                    return;
                }
            }
        }
        this.imageView = new ImageView();
    }

    private Image retrieveImage(Identifier identifier) {
        ChEBIImageService imageService = new ChEBIImageService(identifier.toString());
        return new Image(imageService.parse());
    }

    private void configurePrimaryName(String primaryName) {
        this.primaryName.setText(primaryName);
        this.primaryName.setFont(Font.font(null, FontWeight.BOLD, 16));
    }

    private void configurePrimaryIdentifier(Identifier primaryIdentifier) {
        this.primaryIdentifier.setText(primaryIdentifier.toString());
    }

    private void configureWeight(Quantity mass) {
        this.weight.setText("MolarMass :" + mass.toString());
    }

    private void configureAnnotationsTree(List<Annotation> annotations) {
        TreeItem<String> rootItem = new TreeItem<>("Annotations");
        rootItem.getChildren().add(this.nameAnnotations);
        rootItem.getChildren().add(this.identifiersAnnotations);
        rootItem.getChildren().add(this.organismAnnotation);
        rootItem.getChildren().add(this.functionAnnotation);
        rootItem.getChildren().add(this.otherAnnotation);
        this.annotationsTree = new TreeView<>(rootItem);
        this.annotationsTree.setStyle("-fx-background-color:transparent;");
        annotations.forEach(this::addAnnotationItem);
    }

    private void addAnnotationItem(Annotation annotation) {
        TreeItem<String> item = new TreeItem<>();
        switch (annotation.getAnnotationType()) {
            case ADDITIONAL_NAME: {
                item.setValue(annotation.getContent().toString());
                this.nameAnnotations.getChildren().add(item);
                break;
            }
            case ADDITIONAL_IDENTIFIER: {
                item.setValue(annotation.getContent().toString());
                this.identifiersAnnotations.getChildren().add(item);
                break;
            }
            case ORGANISM: {
                item.setValue(annotation.getDescription() + " " + annotation.getContent().toString());
                this.organismAnnotation.getChildren().add(item);
                break;
            }
            case NOTE: {
                if (annotation.getDescription() != null && annotation.getDescription().equals("function")) {
                    item.setValue(annotation.getContent().toString());
                    this.functionAnnotation.getChildren().add(item);
                }
            }
            default : {
                item.setValue(annotation.getDescription()+": "+annotation.getContent().toString());
                this.functionAnnotation.getChildren().add(item);
            }
        }
    }

    private void addComponentsToGrid() {
        this.add(new VBox(this.primaryName, this.primaryIdentifier, this.weight), 0, 0, 1, 1);
        this.add(this.imageView, 0, 1, 1, 1);
        this.add(this.annotationsTree, 0, 2, 1, 1);
    }

}
