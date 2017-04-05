package de.bioforscher.singa.simulation.application.components.panes;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntities;
import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.ComplexedChemicalEntity;
import de.bioforscher.singa.chemistry.descriptive.Species;
import de.bioforscher.singa.simulation.application.BioGraphSimulation;
import de.bioforscher.singa.simulation.application.IconProvider;
import de.bioforscher.singa.simulation.application.components.cards.GeneralEntityCard;
import de.bioforscher.singa.simulation.modules.reactions.model.Reaction;
import de.bioforscher.singa.simulation.modules.reactions.model.Reactions;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Christoph on 30.06.2016.
 */
public class SpeciesOverviewPane extends BorderPane {

    private BioGraphSimulation owner;

    private ComboBox<String> cbGrouping;
    private TreeView<String> treeView;
    private TreeItem<String> rootItem;
    private Map<String, ChemicalEntity<?>> entityMapping;
    private Map<String, GeneralEntityCard<?>> cardMapping;

    private VBox currentDetailView;

    public SpeciesOverviewPane(BioGraphSimulation owner) {
        this.owner = owner;
        this.entityMapping = ChemicalEntities.generateEntityMapFromSet(owner.getSimulation().getChemicalEntities());
        initializeCards();
        initializeInterface();
        initializeListener();
    }

    private void initializeCards() {
        this.cardMapping = new HashMap<>();
        this.entityMapping.forEach((key, value) -> {
            this.cardMapping.put(key, new GeneralEntityCard<>(value));
        });
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
        this.cbGrouping.getItems().addAll("Type", "Reaction", "None");
        this.cbGrouping.getSelectionModel().selectFirst();
        this.cbGrouping.setPrefWidth(Double.MAX_VALUE);
        leftBox.add(this.cbGrouping, 1, 0);

        // initialize treeView ungrouped
        this.rootItem = new TreeItem<>("All");
        fillTreeGroupedByType();
        this.treeView = new TreeView<>(this.rootItem);
        this.treeView.showRootProperty().setValue(false);
        leftBox.add(this.treeView, 0, 1, 2, 1);

        // right half
        // standard hint text
        this.currentDetailView = new VBox(new Text("Select a species to view Detail."));
        this.currentDetailView.setSpacing(5);
        this.currentDetailView.setPadding(new Insets(5, 5, 5, 5));

        // create SplitPane
        SplitPane split = new SplitPane(leftBox, this.currentDetailView);
        SplitPane.setResizableWithParent(split, Boolean.FALSE);
        // add SplitPane to this
        this.setCenter(split);
    }

    private void initializeListener() {
        this.cbGrouping.valueProperty()
                .addListener((observable, oldValue, newValue) -> this.selectEntityGrouping(newValue));
        this.treeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        Pattern pattern = Pattern.compile(".*\\((.*)\\)");
                        Matcher matcher = pattern.matcher(newValue.getValue());
                        if (matcher.matches()) {
                            this.handleClick(matcher.group(1));
                        }
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
        this.owner.getSimulation().getModules().stream()
                .filter(module -> module instanceof Reactions)
                .map(Reactions.class::cast)
                .forEach(reactions -> {
                    for (Reaction reaction : reactions.getReactions()) {
                        TreeItem<String> reactionItem = createReactionTreeItem(reaction.getDisplayString());
                        this.rootItem.getChildren().add(reactionItem);
                        for (ChemicalEntity entity : reaction.getSubstrates()) {
                            TreeItem<String> speciesItem = createSpeciesTreeItem(entity.getIdentifier().toString());
                            reactionItem.getChildren().add(speciesItem);
                        }
                        for (ChemicalEntity entity : reaction.getProducts()) {
                            TreeItem<String> speciesItem = createSpeciesTreeItem(entity.getIdentifier().toString());
                            reactionItem.getChildren().add(speciesItem);
                        }
                    }
                });
    }

    private void fillTreeGroupedByType() {
        this.rootItem.getChildren().clear();
        TreeItem<String> speciesItem = new TreeItem<>("Species", new ImageView(IconProvider.MOLECULE_ICON_IMAGE));
        TreeItem<String> proteinItem = new TreeItem<>("Enzymes", new ImageView(IconProvider.PROTEIN_ICON_IMAGE));
        TreeItem<String> complexItem = new TreeItem<>("Complexes", new ImageView(IconProvider.COMPLEX_ICON_IMAGE));
        this.rootItem.getChildren().add(speciesItem);
        this.rootItem.getChildren().add(proteinItem);
        this.rootItem.getChildren().add(complexItem);
        this.owner.getSimulation().getChemicalEntities()
                .forEach(entity -> {
                    TreeItem<String> item = createSpeciesTreeItem(entity.getIdentifier().toString());
                    if (entity instanceof Species) {
                        speciesItem.getChildren().add(item);
                    } else if (entity instanceof ComplexedChemicalEntity) {
                        complexItem.getChildren().add(item);
                    } else {
                        proteinItem.getChildren().add(item);
                    }
                });
    }

    private TreeItem<String> createSpeciesTreeItem(String identifyingString) {
        ChemicalEntity chemicalEntity = this.entityMapping.get(identifyingString);
        if (chemicalEntity instanceof Species) {
            return new TreeItem<>(composeTreeName(chemicalEntity),
                    new ImageView(IconProvider.MOLECULE_ICON_IMAGE));
        } else if (chemicalEntity instanceof ComplexedChemicalEntity) {
            TreeItem<String> complex = new TreeItem<>(composeTreeName(chemicalEntity),
                    new ImageView(IconProvider.COMPLEX_ICON_IMAGE));
            ((ComplexedChemicalEntity) chemicalEntity).getAssociatedChemicalEntities().forEach(associated ->
                    complex.getChildren().add(createSpeciesTreeItem(composeTreeName(chemicalEntity))));
            return complex;
        } else {
            return new TreeItem<>(chemicalEntity.getIdentifier().toString(),
                    new ImageView(IconProvider.PROTEIN_ICON_IMAGE));
        }
    }

    private String composeTreeName(ChemicalEntity entity) {
        return entity.getName() + " (" + entity.getIdentifier().toString() + ")";
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
                fillTreeGroupedByType();
                break;
            }
            default: {
                break;
            }
        }
    }


}
