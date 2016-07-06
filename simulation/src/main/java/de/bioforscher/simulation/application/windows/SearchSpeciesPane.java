package de.bioforscher.simulation.application.windows;

import de.bioforscher.chemistry.descriptive.Species;
import de.bioforscher.chemistry.parser.ChEBISearchService;
import de.bioforscher.simulation.application.components.SpeciesCard;
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

import java.util.ArrayList;
import java.util.List;

public class SearchSpeciesPane extends GridPane {

    private static final int default_results_per_page = 4;

    private List<Species> speciesList;
    private ObservableList<Species> selectedSpecies;
    private TextField searchField;
    private ProgressIndicator progressIndicator;
    private Pagination searchResults;
    private int resultsPerPage;

    public SearchSpeciesPane() {
        this(default_results_per_page);
    }

    public SearchSpeciesPane(int resultsPerPage) {
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
        this.searchField.setMaxWidth(Double.MAX_VALUE);
        this.searchField.setOnKeyReleased(this::handleShortCut);
        this.add(this.searchField, 0, 0, 2, 1);

        this.progressIndicator = new ProgressIndicator();
        this.progressIndicator.setVisible(false);
        this.add(this.progressIndicator, 0, 2, 3, 1);

        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> triggerSearch());
        this.add(searchButton, 2, 0, 1, 1);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        this.add(separator, 0, 1, 3, 1);

    }

    private void triggerSearch() {
        if (this.progressIndicator.isVisible()) {
            return;
        }

        this.getChildren().remove(this.searchResults);
        this.speciesList.clear();
        this.progressIndicator.setVisible(true);

        Task listLoader = new Task<List<Species>>() {
            {
                setOnSucceeded(workerStateEvent -> {
                    SearchSpeciesPane.this.speciesList.addAll(getValue());
                    SearchSpeciesPane.this.progressIndicator.setVisible(false);
                    SearchSpeciesPane.this.searchResults = new Pagination((int) Math.ceil(
                            (double) (SearchSpeciesPane.this.speciesList.size()) /
                                    (double) (SearchSpeciesPane.this.resultsPerPage)));
                    add(SearchSpeciesPane.this.searchResults, 0, 2, 3, 1);
                    if (SearchSpeciesPane.this.speciesList.size() > 0) {
                        SearchSpeciesPane.this.searchResults.setPageFactory(SearchSpeciesPane.this::createPage);
                    } else {
                        SearchSpeciesPane.this.searchResults.setPageFactory(SearchSpeciesPane.this::createNoResultsPage);
                    }
                });
            }

            @Override
            protected List<Species> call() throws Exception {
                String searchTerm = SearchSpeciesPane.this.searchField.getText();
                ChEBISearchService searchService = new ChEBISearchService(searchTerm);
                return searchService.search();
            }
        };

        Thread loadingThread = new Thread(listLoader, "list-loader");
        loadingThread.setDaemon(true);
        loadingThread.start();

    }

    private VBox createNoResultsPage(Integer pageIndex) {
        VBox box = new VBox(1);
        Text nothingFoundText = new Text("Sorry, we were not able to find " + "\"" + this.searchField.getText() + "\" in the ChEBI Database.");
        box.getChildren().add(nothingFoundText);
        return box;
    }

    private VBox createPage(Integer pageIndex) {
        VBox box = new VBox(5.0);
        int page = pageIndex * this.resultsPerPage;
        int i = page;
        while (i < page + this.resultsPerPage && i < this.speciesList.size()) {
            SpeciesCard card = new SpeciesCard(this.speciesList.get(i));
            card.addEventHandler(MouseEvent.MOUSE_CLICKED, this::selectCard);
            box.getChildren().add(card);
            i++;
        }
        return box;
    }

    private void selectCard(MouseEvent event) {
        if (!this.selectedSpecies.contains(((SpeciesCard) event.getSource()).getSpecies())) {
            this.selectedSpecies.add(((SpeciesCard) event.getSource()).getSpecies());
        }
    }

    public ObservableList<Species> getSelectedSpecies() {
        return this.selectedSpecies;
    }

    private void handleShortCut(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            this.triggerSearch();
        }
    }

}
