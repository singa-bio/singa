package de.bioforscher.simulation.application.windows;

import de.bioforscher.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.simulation.application.BioGraphSimulation;
import de.bioforscher.simulation.application.IconProvider;
import de.bioforscher.simulation.application.components.SpeciesCard;
import de.bioforscher.simulation.reactions.Reaction;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Christoph on 30.06.2016.
 */
public class SpeciesOverviewPane extends BorderPane {

    private BioGraphSimulation owner;
    private SplitPane split;

    private ComboBox<String> cbGrouping;
    private TreeView<String> treeView;
    private TreeItem<String> rootItem;
    private Map<String, ChemicalEntity> entityMapping;
    private Map<String, SpeciesCard> cardMapping;

    private VBox currentDetailView;

    public SpeciesOverviewPane(BioGraphSimulation owner) {
        this.owner = owner;
        this.entityMapping = owner.getAutomata().getSpecies();
        initializeCards();
        initializeInterface();
        initializeListener();
    }

    private void initializeCards() {
        this.cardMapping = new HashMap<>();
        for (String identifier : this.entityMapping.keySet()) {
            this.cardMapping.put(identifier, new SpeciesCard(this.entityMapping.get(identifier)));
        }
    }

    private void initializeInterface() {
        // left half
        // setup grid constraints
        GridPane leftBox = new GridPane();
        leftBox.setPrefWidth(300);
        ColumnConstraints column0 = new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true);
        ColumnConstraints column1 = new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.ALWAYS, HPos.RIGHT, true);
        leftBox.getColumnConstraints().add(0, column0);
        leftBox.getColumnConstraints().add(1, column1);
        RowConstraints row0 = new RowConstraints();
        RowConstraints row1 = new RowConstraints(300, 300, Double.MAX_VALUE, Priority.ALWAYS, VPos.TOP, true);
        leftBox.getRowConstraints().add(0, row0);
        leftBox.getRowConstraints().add(1, row1);

        // sorting / grouping ComboBox
        Text cbGroupingText = new Text("Group by:");
        leftBox.add(cbGroupingText, 0, 0);
        this.cbGrouping = new ComboBox<>();
        this.cbGrouping.getItems().addAll("None", "Reaction", "Type");
        this.cbGrouping.getSelectionModel().selectFirst();
        this.cbGrouping.setPrefWidth(Double.MAX_VALUE);
        leftBox.add(this.cbGrouping, 1, 0);

        // initialize treeView ungrouped
        this.rootItem = new TreeItem<>("All");
        fillTreeUnGrouped();
        this.treeView = new TreeView<>(this.rootItem);
        this.treeView.showRootProperty().setValue(false);
        leftBox.add(this.treeView, 0, 1, 2, 1);

        // right half
        // standard hint text
        this.currentDetailView = new VBox(new Text("Select a species to view Detail."));
        this.currentDetailView.setSpacing(5);
        this.currentDetailView.setPadding(new Insets(5, 5, 5, 5));

        // create SplitPane
        this.split = new SplitPane(leftBox, this.currentDetailView);
        SplitPane.setResizableWithParent(this.split, Boolean.FALSE);
        // add SplitPane to this
        this.setCenter(this.split);
    }

    private void initializeListener() {
        this.cbGrouping.valueProperty()
                .addListener((observable, oldValue, newValue) -> this.selectEntityGrouping(newValue));
        this.treeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        this.handleClick(newValue.getValue());
                    }
                });
    }

    private void fillTreeUnGrouped() {
        this.rootItem.getChildren().clear();
        for (String identifier : this.entityMapping.keySet()) {
            TreeItem<String> item = createSpeciesTreeItem(identifier);
            this.rootItem.getChildren().add(item);
        }
    }

    private void fillTreeGroupedByReaction() {
        this.rootItem.getChildren().clear();
        for (Reaction reaction : this.owner.getAutomata().getReactions()) {
            TreeItem<String> reactionItem = createReactionTreeItem(reaction.getReactionString());
            this.rootItem.getChildren().add(reactionItem);
            for (ChemicalEntity entity : reaction.getSubstrates()) {
                TreeItem<String> speciesItem = createSpeciesTreeItem(entity.getName());
                reactionItem.getChildren().add(speciesItem);
            }
            for (ChemicalEntity entity : reaction.getProducts()) {
                TreeItem<String> speciesItem = createSpeciesTreeItem(entity.getName());
                reactionItem.getChildren().add(speciesItem);
            }
        }
    }

    private TreeItem<String> createSpeciesTreeItem(String identifyingString) {
        return new TreeItem<>(this.entityMapping.get(identifyingString).getName(),
                new ImageView(IconProvider.MOLECULE_ICON_IMAGE));
    }

    private TreeItem<String> createReactionTreeItem(String identifyingString) {
        return new TreeItem<>(identifyingString,
                new ImageView(IconProvider.GENERIC_REACTION_ICON_IMAGE));
    }

    private void handleClick(final String identifier) {
        if (this.cardMapping.containsKey(identifier)) {
            initializeDetails(identifier);
        }
    }

    private void initializeDetails(final String identifier) {
        // remove previous stuff
        this.currentDetailView.getChildren().remove(0);
        // add new card
        this.currentDetailView.getChildren().add(0, this.cardMapping.get(identifier));
    }

    private void selectEntityGrouping(String groupingOption) {
        this.treeView.getSelectionModel().clearSelection();
        switch (groupingOption) {
            case "None": {
                fillTreeUnGrouped();
                break;
            }
            case "Reaction": {
                fillTreeGroupedByReaction();
                break;
            }
            case "Type": {
                break;
            }
            default: {
                break;
            }
        }
    }


}
