package de.bioforscher.singa.simulation.application.components.panes;

import de.bioforscher.singa.chemistry.descriptive.ChemicalEntity;
import de.bioforscher.singa.simulation.application.IconProvider;
import de.bioforscher.singa.simulation.application.components.cards.ChemicalEntityCard;
import de.bioforscher.singa.simulation.parser.sbml.BioModelsParserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by leberech on 31/01/17.
 */
public class SBMLSearchPane extends GridPane {

    private static final int default_results_per_page = 4;

    private List<ChemicalEntity> speciesList;
    private ObservableList<ChemicalEntity> selectedSpecies;
    private TextField searchField;
    private ProgressIndicator progressIndicator;
    private Pagination searchResults;
    private int resultsPerPage;

    public SBMLSearchPane() {
        this(default_results_per_page);
    }

    public SBMLSearchPane(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
        this.speciesList = new ArrayList<>();
        this.selectedSpecies = FXCollections.observableArrayList();
        initialize();
    }

    private void initialize() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10, 10, 10, 10));

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.LEFT);
        column1.setHgrow(Priority.ALWAYS);
        column1.setPercentWidth(80);
        this.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.RIGHT);
        column2.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().add(column2);

        this.searchField = new TextField("");
        this.searchField.setTooltip(new Tooltip("Use a BioModels identifer (e.g. BIOMD0000000001) to fetch chemical entities from."));
        this.searchField.setMaxWidth(Double.MAX_VALUE);
        this.searchField.setOnKeyReleased(this::handleShortCut);
        this.add(this.searchField, 0, 0, 2, 1);

        this.progressIndicator = new ProgressIndicator();
        this.progressIndicator.setVisible(false);
        this.add(this.progressIndicator, 0, 2, 3, 1);

        Button fetchButton = IconProvider.FontAwesome.createIconButton(IconProvider.FontAwesome.ICON_DATABASE);
        fetchButton.setOnAction(event -> triggerSearch());
        fetchButton.setTooltip(new Tooltip("Search BioModels online."));
        this.add(fetchButton, 2, 0, 1, 1);

        Button loadButton = IconProvider.FontAwesome.createIconButton(IconProvider.FontAwesome.ICON_FILE_XML);
        loadButton.setTooltip(new Tooltip("Use file on your hard drive."));
        loadButton.setOnAction(event -> selectFile());
        this.add(loadButton, 3, 0, 1, 1);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        this.add(separator, 0, 1, 3, 1);

    }

    private void selectFile() {
        if (this.progressIndicator.isVisible()) {
            return;
        }

        this.getChildren().remove(this.searchResults);
        this.speciesList.clear();
        this.progressIndicator.setVisible(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load SBML-File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            this.handleResults(BioModelsParserService.parseModelFromFile(file.getPath()).getChemicalEntities().values().stream()
                    .collect(Collectors.toList()));
            this.searchField.setText(file.getName());
        }
        this.progressIndicator.setVisible(false);
    }

    private void triggerSearch() {
        if (this.progressIndicator.isVisible()) {
            return;
        }

        this.getChildren().remove(this.searchResults);
        this.speciesList.clear();
        this.progressIndicator.setVisible(true);

        Task listLoader = new Task<List<ChemicalEntity>>() {
            {
                setOnSucceeded(workerStateEvent -> SBMLSearchPane.this.handleResults(getValue()));
            }

            @Override
            protected List<ChemicalEntity> call() throws Exception {
                String searchTerm = SBMLSearchPane.this.searchField.getText();
                return BioModelsParserService.parseModelById(searchTerm).getChemicalEntities().values().stream().collect(Collectors.toList());
            }
        };

        Thread loadingThread = new Thread(listLoader, "list-loader");
        loadingThread.setDaemon(true);
        loadingThread.start();

    }

    private void handleResults(List<ChemicalEntity> results) {
        this.speciesList.addAll(results);
        this.progressIndicator.setVisible(false);
        this.searchResults = new Pagination((int) Math.ceil(
                (double) (this.speciesList.size()) /
                        (double) (this.resultsPerPage)));
        add(this.searchResults, 0, 2, 4, 1);
        if (this.speciesList.size() > 0) {
            this.searchResults.setPageFactory(this::createPage);
        } else {
            this.searchResults.setPageFactory(this::createNoResultsPage);
        }
    }

    private VBox createNoResultsPage(Integer pageIndex) {
        VBox box = new VBox(1);
        Text nothingFoundText = new Text("Sorry, we were not able to find " + "\"" + this.searchField.getText() +
                "\" on the BioModels Database.");
        box.getChildren().add(nothingFoundText);
        return box;
    }

    private VBox createPage(Integer pageIndex) {
        VBox box = new VBox(5.0);
        int page = pageIndex * this.resultsPerPage;
        int i = page;
        while (i < page + this.resultsPerPage && i < this.speciesList.size()) {
            ChemicalEntityCard card = new ChemicalEntityCard(this.speciesList.get(i));
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, this::selectCard);
            box.getChildren().add(card);
            i++;
        }
        return box;
    }

    private void selectCard(MouseEvent event) {
        if (!this.selectedSpecies.contains(((ChemicalEntityCard) event.getSource()).getChemicalEntity())) {
            this.selectedSpecies.add(((ChemicalEntityCard) event.getSource()).getChemicalEntity());
        }
    }

    public ObservableList<ChemicalEntity> getSelectedSpecies() {
        return this.selectedSpecies;
    }

    private void handleShortCut(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            this.triggerSearch();
        }
    }

}
